package org.reactivebird.oauth


import org.reactivebird.http._
import org.reactivebird._
import scala.concurrent.{ExecutionContext, Future}
import akka.util.Timeout
import akka.actor.ActorSystem
import org.reactivebird.Consumer
import java.util.concurrent.TimeUnit
import PipelineFilter.ResponseIdentity


case class OAuthHandler(
    consumer: Consumer,
    retryCount: Int = 1)(
    implicit val system: ActorSystem,
    val timeout: Timeout = Timeout(60, TimeUnit.SECONDS))
  extends ServiceProxy
  with ServiceFactory {

  val temporaryCredentialsRequestUri = "/oauth/request_token"
  val resourceOwnerAuthorizationUri = "/oauth/authenticate"
  val tokenRequestUri = "/oauth/access_token"

  implicit val ec: ExecutionContext = system.dispatcher

  def requestToken(oauthCallback: String): Future[RequestToken] = {
    val authorizer = Authorizer(consumer, oauthCallback)
    val authorizedPipeline = pipeline map { p =>
      PipelineFilter(authorizer, ResponseIdentity(_))(ec) andThen p
    }
    post(temporaryCredentialsRequestUri, Map())(authorizedPipeline) map { r =>
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
    val authorizedPipeline = pipeline map { p =>
      PipelineFilter(authorizer, ResponseIdentity(_))(ec) andThen p
    }
    post(tokenRequestUri, Map(), Some(content))(authorizedPipeline) map { r =>
      AccessToken.fromResponseBody(r.entity.asString)
    }
  }

}
