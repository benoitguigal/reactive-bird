package org.reactivebird

import org.reactivebird.http._
import org.reactivebird.api._
import org.reactivebird.models.ModelFactory
import java.util.concurrent.TimeUnit
import akka.util.Timeout
import akka.actor.ActorSystem

class TwitterApi(
    consumer: Consumer,
    token: Token,
    val retryCount: Int = 1,
    val cacheResult: Boolean = false)(
    implicit val system: ActorSystem,
    val timeout: Timeout = Timeout(60, TimeUnit.SECONDS))
  extends HttpService
  with StatusFiltering
  with Retrying
  with Caching
  with Authorizing
  with ModelFactory
  with Timeline
  with Tweets
  with Search
  with FriendsAndFollowers
  with Users
  with DirectMessages
  with Favorites {

  implicit val exec = system.dispatcher

  override protected def authorizer = Authorizer(consumer, token)
}

