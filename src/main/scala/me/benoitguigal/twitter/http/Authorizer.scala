package me.benoitguigal.twitter.http

import spray.client.pipelining._
import me.benoitguigal.twitter.oauth.OAuth._
import me.benoitguigal.twitter.{Token, Consumer}
import spray.http.HttpRequest


object Authorizer {

  def apply(consumer: Consumer, token: Token): RequestTransformer = {
    request: HttpRequest => request.authorize(consumer, token)
  }

  def apply(consumer: Consumer, oauthCallback: String): RequestTransformer = {
    request: HttpRequest => request.authorize(consumer, oauthCallback)
  }

}
