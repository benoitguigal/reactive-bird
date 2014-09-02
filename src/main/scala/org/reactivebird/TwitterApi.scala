package org.reactivebird

import org.reactivebird.http._
import org.reactivebird.api._
import org.reactivebird.models.ModelFactory
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

class TwitterApi(
    consumer: Consumer,
    token: Token)(
    implicit val system: ActorSystem)
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

  private val config = ConfigFactory.load()
  protected val cacheResult = config.getBoolean("reactivebird.cache-result")
  protected val retryCount = config.getInt("reactivebird.retry-count")

  implicit val exec = system.dispatcher

  override protected def authorizer = Authorizer(consumer, token)
}

