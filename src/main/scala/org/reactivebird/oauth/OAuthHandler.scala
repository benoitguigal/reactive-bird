package org.reactivebird.oauth


import org.reactivebird.http._
import org.reactivebird._
import scala.concurrent.{ExecutionContext, Future}
import akka.util.Timeout
import akka.actor.ActorSystem
import org.reactivebird.http.Authorizing._
import org.reactivebird.Consumer
import scala.Some
import java.util.concurrent.TimeUnit


case class OAuthHandler(
    consumer: Consumer,
    retryCount: Int = 1)(
    implicit val system: ActorSystem,
    val timeout: Timeout = Timeout(60, TimeUnit.SECONDS))
  extends HttpService
  with Retrying
  with StatusFiltering {

  val temporaryCredentialsRequestUri = "/oauth/request_token"
  val resourceOwnerAuthorizationUri = "/oauth/authenticate"
  val tokenRequestUri = "/oauth/access_token"

  implicit val exec: ExecutionContext = system.dispatcher

  def requestToken(oauthCallback: String): Future[RequestToken] = {
    val authorizer = Authorizer(consumer, oauthCallback)
    implicit val pipeline = getPipeline map { p => withAuthorizer(p, authorizer) }
    post(temporaryCredentialsRequestUri, Map()) map { r =>
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
    val content = s"oauth_verifier=$oauthVerifier"
    val authorizer = Authorizer(consumer, Token(tokenKey, None))
    implicit val pipeline = getPipeline map { p => withAuthorizer(p, authorizer) }
    post(tokenRequestUri, Map(), Some(content)) map { r =>
      AccessToken.fromResponseBody(r.entity.asString)
    }
  }

}
