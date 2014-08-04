package org.reactivebird.http

import spray.client.pipelining._
import org.reactivebird.oauth.OAuth._
import org.reactivebird.{Token, Consumer}
import spray.http.HttpRequest


object Authorizer {

  def apply(consumer: Consumer, token: Token): RequestTransformer = {
    request: HttpRequest => request.authorize(consumer, token)
  }

  def apply(consumer: Consumer, oauthCallback: String): RequestTransformer = {
    request: HttpRequest => request.authorize(consumer, oauthCallback)
  }

}
