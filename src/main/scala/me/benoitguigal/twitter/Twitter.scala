package me.benoitguigal.twitter

import spray.http._
import spray.client.pipelining._
import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import me.benoitguigal.twitter.oauth._
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import spray.http.Uri.Query
import scala.concurrent.Future
import me.benoitguigal.twitter.api.Timeline
import TwitterError.errorFilter
import me.benoitguigal.twitter.wrappers.{WrapperTypes, DefaultWrapperTypes}
import OAuth.CanBeAuthorizedHttpRequest
import spray.http.HttpRequest
import me.benoitguigal.twitter.oauth.Token
import me.benoitguigal.twitter.oauth.Consumer
import spray.http.HttpResponse


object Twitter {

  def apply(consumer: Consumer) = new Twitter(consumer) with DefaultWrapperTypes
}

abstract class Twitter(val consumer: Consumer) extends WrapperTypes with Timeline {

  private[this] implicit val system = ActorSystem()
  implicit val exec = system.dispatcher // execution context for futures
  private[this] implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  val requestLogger: (HttpRequest => HttpRequest) = r => { println(r); r }

  private[this] lazy val pipeline = for (
    Http.HostConnectorInfo(connector, _) <-
    IO(Http) ? Http.HostConnectorSetup(host, port = 443, sslEncryption = true)
  ) yield (
      requestLogger
      ~> sendReceive(connector)
      ~> errorFilter
  )

  def get(token: Token, path: String, params: Map[String, String]): Future[HttpResponse] = {
    val uri = Uri.from(path = path, query = Query(params))
    val request = Get(uri).authorize(consumer, token)
    pipeline.flatMap(_(request))
  }

  def post(token: Token, path: String, params: Map[String, String]): Future[HttpResponse] = {
    val uri = Uri.from(path = path, query = Query(params))
    val request = {
      val baseRequest = Post(uri)
      val requestWithContent = if (params.nonEmpty) {
        val urlEncodedParams = params map { case (k, v) => k + "=" + v } mkString "&"
        val r = baseRequest.withHeadersAndEntity(
            List(HttpHeaders.`Content-Type`(MediaTypes.`application/x-www-form-urlencoded`)),
            HttpEntity(ContentType(MediaTypes.`application/x-www-form-urlencoded`), urlEncodedParams)
        )
        r.withHeaders(HttpHeaders.`Content-Length`(r.entity.data.length))
      } else {
        baseRequest
      }
      requestWithContent.authorize(consumer, token)
    }
    pipeline.flatMap(_(request))
  }

  def requestToken(oauthCallback: String): Future[RequestToken] = {
    val uri = Uri.from(path = "/oauth/request_token")
    val request = Post(uri).authorize(consumer, oauthCallback)
    pipeline.flatMap(_(request)) map { r =>
      RequestToken.fromResponseBody(r.entity.asString)
    }
  }

  def accessToken(oauthToken: String, oauthVerifier: String): Future[AccessToken] = {
    val token = Token(oauthToken, None)
    post(token, "/oauth/access_token", Map("oauth_verifier" -> oauthVerifier)) map { r =>
      AccessToken.fromResponseBody(r.entity.asString)
    }
  }

}
