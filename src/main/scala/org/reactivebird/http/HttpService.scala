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



object HttpService {

  import Akka.exec

  private[this] def retry[T](noTimes: Int)(block: =>Future[T]): Future[T] = {
    val ns = (1 to noTimes).iterator
    val attempts = ns.map(_ => () => block)
    attempts.reduceLeft { (a, b) =>
      () => a() recoverWith { case _ => b() }
    }()
  }

  private[http] def withRetry(noTimes: Int)(pipeline: SendReceive): SendReceive = {
    request => {
      retry(noTimes)(pipeline(request))
    }
  }
}

trait HttpService {

  import Akka.{system, exec, timeout}
  import HttpService.withRetry

  lazy private val sendReceiveFut = for (
    Http.HostConnectorInfo(connector, _) <-
    IO(Http) ? Http.HostConnectorSetup(host, port = 443, sslEncryption = true)
  ) yield (sendReceive(connector))

  def authorizer: RequestTransformer

  val retryCount: Int

  lazy private val pipeline = sendReceiveFut map { sendReceive =>
    withRetry(retryCount) {
      authorizer ~> sendReceive ~> errorFilter
    }
  }

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
