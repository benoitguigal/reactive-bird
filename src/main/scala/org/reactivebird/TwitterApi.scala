package org.reactivebird

import org.reactivebird.http.{Authorizer, HttpService}
import org.reactivebird.api._
import org.reactivebird.models.ModelFactory


class TwitterApi(val consumer: Consumer, val token: Token)
  extends HttpService
  with ModelFactory
  with Timeline
  with Tweets
  with Search
  with FriendsAndFollowers
  with Users
  with DirectMessages {

  override def authorizer = Authorizer(consumer, token)
}

