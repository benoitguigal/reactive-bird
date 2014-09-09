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
import scala.concurrent.Future
import spray.client.pipelining.SendReceive
import ServiceFilter._
import org.reactivebird.http.filters.{RetryFilter, CacheFilter}
import spray.can.Http

class TwitterApi(
    consumer: Consumer,
    token: Token)(
    implicit val system: ActorSystem)
  extends ServiceProxy
  with ServiceFactory
  with ModelFactory
  with Timeline
  with Tweets
  with Search
  with FriendsAndFollowers
  with Users
  with DirectMessages
  with Favorites {

  private val config = ConfigFactory.load()
  private val cacheResult = config.getBoolean("reactivebird.cache-result")
  private val retryCount = config.getInt("reactivebird.retry-count")
  private lazy val timeToLive = config.getDuration("reactivebird.time-to-live", DAYS)

  implicit val ec = system.dispatcher

  private val cache: Cache[HttpResponse] = LruCache(timeToLive = Duration(timeToLive, DAYS))

  private val authorizer = Authorizer(consumer, token)

  private val filters: Seq[ServiceFilter] = Seq(
    ServiceFilter(authorizer, ResponseIdentity(_)),
    ServiceFilter(RequestIdentity(_), TwitterError.responseTransformer),
    new RetryFilter(retryCount),
    new CacheFilter(cacheResult, cache))

  private val setup = Http.HostConnectorSetup(host, port = 443, sslEncryption = true)

  implicit val service: Future[SendReceive] = Service(setup) map { service =>
    filters.foldRight(service) { (f, s) =>
      f andThen s
    }
  }
}

