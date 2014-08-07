package org.reactivebird

import org.reactivebird.http._
import org.reactivebird.api._
import org.reactivebird.models.ModelFactory

class TwitterApi(consumer: Consumer, token: Token, val retryCount: Int = 1, val cacheResult: Boolean = false)
  extends HttpService
  with Retrying
  with Caching
  with ModelFactory
  with Timeline
  with Tweets
  with Search
  with FriendsAndFollowers
  with Users
  with DirectMessages
  with Favorites {

  override protected def authorizer = Authorizer(consumer, token)
}

