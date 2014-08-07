package org.reactivebird.http

import spray.caching.{LruCache, Cache}
import spray.client.pipelining.SendReceive
import spray.http.{HttpResponse, HttpMethods}
import org.reactivebird.Akka.exec


object Caching {

  private[http] def withCache(cache: Cache[HttpResponse])(pipeline: SendReceive): SendReceive = {
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

  private val cache: Cache[HttpResponse] = LruCache()
  val cacheResult: Boolean

  override protected def getPipeline = {
    if (cacheResult) {
      super.getPipeline map { p =>
        withCache(cache){ p }
      }
    } else {
      super.getPipeline
    }

  }

}
