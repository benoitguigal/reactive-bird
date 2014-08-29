package org.reactivebird.http

import spray.caching.{LruCache, Cache}
import spray.client.pipelining.SendReceive
import spray.http.{HttpResponse, HttpMethods}
import scala.concurrent.ExecutionContext


object Caching {

  private[http] def withCache(pipeline: SendReceive, cache: Cache[HttpResponse])(implicit exec: ExecutionContext): SendReceive = {
    request => {
      if (request.method == HttpMethods.GET)
        cache(request.uri.toString, () => pipeline(request))
      else
        pipeline(request)
    }
  }

}

trait Caching extends HttpService {

  import Caching._

  implicit val exec: ExecutionContext

  val cache: Cache[HttpResponse] = LruCache()
  val cacheResult: Boolean

  override protected def getPipeline = {
    if (cacheResult) {
      super.getPipeline map { p =>
        withCache(p, cache)
      }
    } else {
      super.getPipeline
    }

  }

}
