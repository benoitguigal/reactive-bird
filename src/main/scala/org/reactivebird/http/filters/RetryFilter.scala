package org.reactivebird.http.filters

import org.reactivebird.http.ServiceFilter
import scala.concurrent.{Future, ExecutionContext}
import spray.client.pipelining._
import spray.http.HttpRequest


class RetryFilter(retryCount: Int)(implicit val ec: ExecutionContext) extends ServiceFilter {

  private def retry[T](block: =>Future[T], retryCount: Int): Future[T] = {
    val ns = (1 to retryCount + 1).iterator
    val attempts = ns.map(_ => () => block)
    attempts.reduceLeft { (a, b) =>
      () => a() recoverWith { case _ => b() }
    }()
  }

  override def apply(request: HttpRequest, service: SendReceive) = {
    retry(service(request), retryCount)
  }
}
