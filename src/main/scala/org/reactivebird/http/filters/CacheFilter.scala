package org.reactivebird.http.filters

import spray.caching.Cache
import spray.http._
import scala.concurrent.ExecutionContext
import org.reactivebird.http.ServiceFilter
import spray.client.pipelining._
import spray.http.HttpRequest
import spray.http.HttpResponse


class CacheFilter(cacheResult: Boolean, cache: Cache[HttpResponse])(implicit val ec: ExecutionContext)
  extends ServiceFilter {

  def apply(request: HttpRequest, service: SendReceive) = {
    if (cacheResult) {
      if (request.method == HttpMethods.GET)
        cache(request.uri.toString, () => service(request))
      else
        service(request)
    } else {
      service(request)
    }
  }

}
