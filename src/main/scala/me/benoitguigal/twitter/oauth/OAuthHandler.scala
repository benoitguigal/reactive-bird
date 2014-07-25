package me.benoitguigal.twitter.oauth

import akka.actor.ActorSystem
import me.benoitguigal.twitter.http.{HttpPipeline, HttpService}
import me.benoitguigal.twitter.{Token, Consumer}
import scala.concurrent.Future


case class OAuthHandler(consumer: Consumer) {

  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  val temporaryCredentialsRequestUri = "/oauth/request_token"
  val tokenRequestUri = "/oauth/access_token"

  def requestToken(oauthCallback: String): Future[RequestToken] = {
    val httpService = new HttpService {
      override def pipeline = HttpPipeline(consumer, oauthCallback)
    }
    httpService.post(temporaryCredentialsRequestUri, Map()) map { r =>
      RequestToken.fromResponseBody(r.entity.asString)
    }
  }

  def requestToken = requestToken("")

  def accessToken(tokenKey: String, oauthVerifier: String): Future[AccessToken] = {
    val httpService = new HttpService {
      override def pipeline = HttpPipeline(consumer, Token(tokenKey, None))
    }
    val content = s"oauth_verifier=$oauthVerifier"
    httpService.post(tokenRequestUri, Map(), Some(content)) map { r =>
      AccessToken.fromResponseBody(r.entity.asString)
    }
  }

}
