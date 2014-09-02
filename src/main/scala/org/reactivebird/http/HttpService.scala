package org.reactivebird.http


import spray.client.pipelining._
import scala.concurrent.{ExecutionContext, Future}
import spray.http._
import spray.http.Uri.Query
import spray.http.HttpResponse
import spray.can.Http
import akka.io.IO
import org.reactivebird.host
import akka.pattern.ask
import akka.actor.ActorSystem
import akka.util.Timeout
import java.util.concurrent.TimeUnit


trait HttpService {

  implicit val system: ActorSystem
  implicit val exec : ExecutionContext

  implicit private val timeout = Timeout(60, TimeUnit.SECONDS)

  lazy private val sendReceiveFut = for (
    Http.HostConnectorInfo(connector, _) <-
    IO(Http) ? Http.HostConnectorSetup(host, port = 443, sslEncryption = true)
  ) yield (sendReceive(connector))

  protected def getPipeline = sendReceiveFut

  lazy private val pipeline = getPipeline

  def get(path: String, params: Map[String, String])(
    implicit pipeline: Future[SendReceive] = pipeline): Future[HttpResponse] = {

    val uri = Uri.from(path = path, query = Query(params))
    val request = Get(uri)
    pipeline.flatMap(_(request))
  }

  def post(path: String, params: Map[String, String], content: Option[String] = None)(
    implicit pipeline: Future[SendReceive] = pipeline): Future[HttpResponse] = {

    val uri = Uri.from(path = path, query = Query(params))
    val request = {
      val baseRequest = Post(uri)
      if (content.nonEmpty) {
        val r = baseRequest.withHeadersAndEntity(
          List(HttpHeaders.`Content-Type`(MediaTypes.`application/x-www-form-urlencoded`)),
          HttpEntity(ContentType(MediaTypes.`application/x-www-form-urlencoded`), content.get)
        )
        r.withHeaders(HttpHeaders.`Content-Length`(r.entity.data.length))
      } else {
        baseRequest
      }
    }
    pipeline.flatMap(_(request))
  }

}
