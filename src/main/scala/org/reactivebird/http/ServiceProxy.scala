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


trait ServiceProxy {

  implicit val ec : ExecutionContext

  implicit val service: Future[SendReceive]

  def get(path: String, params: Map[String, String]): Future[HttpResponse] = {

    val uri = Uri.from(path = path, query = Query(params))
    val request = Get(uri)
    service.flatMap(_(request))
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
    service.flatMap(_(request))
  }
}
