package org.reactivebird.http


import spray.client.pipelining._
import scala.concurrent.Future
import spray.http._
import spray.http.Uri.Query
import spray.http.HttpResponse
import spray.can.Http
import akka.io.IO
import org.reactivebird.Akka
import org.reactivebird.host
import akka.pattern.ask
import org.reactivebird.TwitterError.errorFilter


trait HttpService {

  import Akka.{system, exec, timeout}

  lazy private val sendReceiveFut = for (
    Http.HostConnectorInfo(connector, _) <-
    IO(Http) ? Http.HostConnectorSetup(host, port = 443, sslEncryption = true)
  ) yield (sendReceive(connector))

  def authorizer: RequestTransformer

  protected def getPipeline = sendReceiveFut map { sendReceive =>
    authorizer ~> sendReceive ~> errorFilter
  }

  lazy private val pipeline = getPipeline

  def get(path: String, params: Map[String, String]): Future[HttpResponse] = {
    val uri = Uri.from(path = path, query = Query(params))
    val request = Get(uri)
    pipeline.flatMap(_(request))
  }

  def post(path: String, params: Map[String, String], content: Option[String] = None): Future[HttpResponse] = {
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
