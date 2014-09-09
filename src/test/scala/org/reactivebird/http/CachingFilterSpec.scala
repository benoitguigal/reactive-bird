package org.reactivebird.http

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import spray.client.pipelining.SendReceive
import spray.http.{Uri, HttpMethods, HttpRequest, HttpResponse}
import spray.caching.LruCache
import org.mockito.Mockito._
import org.mockito.Matchers._
import scala.concurrent.{Await, Future}
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global


class CachingFilterSpec extends FlatSpec with MockitoSugar with Matchers {


  it should "cache HttpResponse" in {
    val pipeline = mock[SendReceive]
    val response = mock[HttpResponse]
    when(pipeline.apply(any[HttpRequest])).thenReturn(Future(response))
    val cache = LruCache[HttpResponse]()
    val filter = new CachingFilter(true, cache)
    val cachingPipeline = filter andThen pipeline
    val request = mock[HttpRequest]
    when(request.method).thenReturn(HttpMethods.GET)
    when(request.uri).thenReturn(Uri("https://api.twitter.com/1.1/search"))
    val ret1 = Await.result(cachingPipeline(request), Duration(5, TimeUnit.SECONDS))
    val ret2 = Await.result(cachingPipeline(request), Duration(5, TimeUnit.SECONDS))
    ret1 should equal(response)
    ret2 should equal(response)
    verify(pipeline, times(1)).apply(request)
  }

  it should "not cache HttpResponse when request is a POST" in {
    val pipeline = mock[SendReceive]
    val response = mock[HttpResponse]
    when(pipeline.apply(any[HttpRequest])).thenReturn(Future(response))
    val cache = LruCache[HttpResponse]()
    val filter = new CachingFilter(true, cache)
    val cachingPipeline = filter andThen pipeline
    val request = mock[HttpRequest]
    when(request.method).thenReturn(HttpMethods.POST)
    when(request.uri).thenReturn(Uri("https://api.twitter.com/1.1/destroy"))
    val ret1 = Await.result(cachingPipeline(request), Duration(5, TimeUnit.SECONDS))
    val ret2 = Await.result(cachingPipeline(request), Duration(5, TimeUnit.SECONDS))
    ret1 should equal(response)
    ret2 should equal(response)
    verify(pipeline, times(2)).apply(request)
  }

}
