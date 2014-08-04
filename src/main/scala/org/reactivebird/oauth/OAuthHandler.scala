package org.reactivebird.oauth


import org.reactivebird.http.{Authorizer, HttpService}
import org.reactivebird.{Akka, Consumer, Token}
import scala.concurrent.Future
import org.reactivebird.{scheme, host}


case class OAuthHandler(consumer: Consumer) {

  import Akka.exec

  val temporaryCredentialsRequestUri = "/oauth/request_token"
  val resourceOwnerAuthorizationUri = "/oauth/authenticate"
  val tokenRequestUri = "/oauth/access_token"

  def requestToken(oauthCallback: String): Future[RequestToken] = {
    val httpService = new HttpService {
      def authorizer = Authorizer(consumer, oauthCallback)
    }
    httpService.post(temporaryCredentialsRequestUri, Map()) map { r =>
      RequestToken.fromResponseBody(r.entity.asString)
    }
  }

  def requestToken: Future[RequestToken] = requestToken("")

  def authorizationUrl(oauthCallback: String): Future[String] = {
    requestToken(oauthCallback) map { requestToken =>
      s"$scheme://$host$resourceOwnerAuthorizationUri"
    }
  }

  def authorizationUrl: Future[String] = authorizationUrl("")

  def accessToken(tokenKey: String, oauthVerifier: String): Future[AccessToken] = {
    val httpService = new HttpService {
      def authorizer = Authorizer(consumer, Token(tokenKey, None))
    }
    val content = s"oauth_verifier=$oauthVerifier"
    httpService.post(tokenRequestUri, Map(), Some(content)) map { r =>
      AccessToken.fromResponseBody(r.entity.asString)
    }
  }

}
