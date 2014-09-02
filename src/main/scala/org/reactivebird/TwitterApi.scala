package org.reactivebird

import org.reactivebird.http._
import org.reactivebird.api._
import org.reactivebird.models.ModelFactory
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import spray.caching.Cache
import spray.http.HttpResponse
import spray.caching.LruCache
import java.util.concurrent.TimeUnit.DAYS
import scala.concurrent.duration.Duration

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
  private lazy val timeToLive = config.getDuration("reactivebird.time-to-live", DAYS)

  implicit val exec = system.dispatcher

  protected val cache: Cache[HttpResponse] = LruCache(timeToLive = Duration(timeToLive, DAYS))

  override protected def authorizer = Authorizer(consumer, token)
}

