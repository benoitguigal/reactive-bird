package me.benoitguigal.twitter.api

import scala.concurrent.Future
import me.benoitguigal.twitter.{TwitterErrorRateLimitExceeded, TwitterApi}


object Navigation {

  import TwitterApi.exec

  def cursoring[A](f: Long => Future[(Long, Seq[A])]): Future[Seq[A]] = {

    def inner(cursor: Long, acc: Seq[A]): Future[Seq[A]] = {
      if (cursor == 0)
        Future(acc)
      else {
        f(cursor) flatMap { case (nextCursor, list) =>
          inner(nextCursor, acc ++ list)
        } recover {
          case e: TwitterErrorRateLimitExceeded => acc
        }
      }
    }
    inner(-1, Seq.empty[A])
  }

}
