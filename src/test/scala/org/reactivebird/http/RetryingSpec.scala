package org.reactivebird.http

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import spray.client.pipelining.SendReceive
import spray.http.{HttpRequest, HttpResponse}
import scala.concurrent.{Await, Future}
import org.mockito.Mockito._
import org.mockito.Matchers._
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global


class RetryingSpec extends FlatSpec with MockitoSugar with Matchers {

 import Retrying._

  it should "retry calls and throw if none succeeds" in {
    val sendReceive = mock[SendReceive]
    class MyException extends Exception
    when(sendReceive.apply(any[HttpRequest])).thenReturn(Future.failed(new MyException))
    val sendReceiveWithRetry = withRetry(sendReceive, 3)
    val request = mock[HttpRequest]
    intercept[MyException]{
      Await.result(sendReceiveWithRetry(request), Duration(5, TimeUnit.SECONDS))
    }
    verify(sendReceive, times(3)).apply(request)
  }

  it should "retry calls and return value if one call succeeds" in {
    val sendReceive = mock[SendReceive]
    class MyException extends Exception
    val expected = mock[HttpResponse]
    when(sendReceive.apply(any[HttpRequest]))
      .thenReturn(Future.failed(new MyException))
      .thenReturn(Future.successful(expected))
    val sendReceiveWithRetry = withRetry(sendReceive, 3)
    val request = mock[HttpRequest]
    val response = Await.result(sendReceiveWithRetry(request), Duration(5, TimeUnit.SECONDS))
    response should equal(expected)
    verify(sendReceive, times(2)).apply(request)
  }


}
