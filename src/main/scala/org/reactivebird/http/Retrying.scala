package org.reactivebird.http

import spray.client.pipelining._
import scala.concurrent.Future
import org.reactivebird.Akka.exec


object Retrying {

  private[this] def retry[T](noTimes: Int)(block: =>Future[T]): Future[T] = {
    val ns = (1 to noTimes).iterator
    val attempts = ns.map(_ => () => block)
    attempts.reduceLeft { (a, b) =>
      () => a() recoverWith { case _ => b() }
    }()
  }

  def withRetry(noTimes: Int)(pipeline: SendReceive): SendReceive = {
    request => {
      retry(noTimes)(pipeline(request))
    }
  }

}

trait Retrying extends HttpService {

  import Retrying._

  val retryCount: Int

  override protected def getPipeline = {
    super.getPipeline map { p =>
      withRetry(retryCount)(p)
    }
  }
}
