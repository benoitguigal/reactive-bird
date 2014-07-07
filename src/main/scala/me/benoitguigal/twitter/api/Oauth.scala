package me.benoitguigal.twitter.api

import me.benoitguigal.twitter.oauth._
import scala.concurrent.Future
import me.benoitguigal.twitter.TwitterApi


trait Oauth {
  self: TwitterApi =>

  import TwitterApi.exec

  def requestToken(oauthCallback: String): Future[RequestToken] = {
    post("/oauth/request_token", Map()) map { r =>
      RequestToken.fromResponseBody(r.entity.asString)
    }
  }

  def accessToken(oauthVerifier: String): Future[AccessToken] = {
    post("/oauth/access_token", Map("oauth_verifier" -> oauthVerifier)) map { r =>
      AccessToken.fromResponseBody(r.entity.asString)
    }
  }

}
