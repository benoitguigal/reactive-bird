package org.reactivebird.http

import spray.client.pipelining._
import scala.concurrent.{ExecutionContext, Future}


object Retrying {

  private[this] def retry[T](block: =>Future[T], retryCount: Int)(implicit exec: ExecutionContext): Future[T] = {
    val ns = (1 to retryCount).iterator
    val attempts = ns.map(_ => () => block)
    attempts.reduceLeft { (a, b) =>
      () => a() recoverWith { case _ => b() }
    }()
  }

  def withRetry(pipeline: SendReceive, retryCount: Int)(implicit exec: ExecutionContext): SendReceive = {
    request => {
      retry(pipeline(request), retryCount)
    }
  }

}

trait Retrying extends HttpService {

  import Retrying._

  val retryCount: Int
  implicit val exec: ExecutionContext

  override protected def getPipeline = {
    super.getPipeline map { p =>
      withRetry(p, retryCount)
    }
  }
}
