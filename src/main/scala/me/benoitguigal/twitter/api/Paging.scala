package me.benoitguigal.twitter.api

import scala.concurrent.{Await, Future}
import me.benoitguigal.twitter.{Akka, TwitterErrorRateLimitExceeded}
import me.benoitguigal.twitter.models.{CursoredResultSet, ResultSet, AbstractResultSet, CanBeIdentified}
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import Stream._


trait Page {
  val count: Option[Int]
}
case class MaxIdPage(count: Option[Int], sinceId: Option[String], maxId: Option[String]) extends Page
case class CursorPage(count: Option[Int], cursor: Option[Long])


trait Paging[A] {

  import Akka.exec

  val stream: Stream[Seq[A]]

  def items(n: Int): Future[Seq[A]] = Future(stream.flatten.take(n).toSeq)

  def items: Future[Seq[A]] = Future(stream.flatten.toSeq)

  def pages(n: Int): Future[Seq[Seq[A]]] = Future(stream.take(n).toSeq)
  def pages: Future[Seq[Seq[A]]] = Future(stream.toSeq)

}


case class CursorPaging[A <: CanBeIdentified](f: CursorPage => Future[CursoredResultSet[A]], count: Int = 2000) extends Paging[A] {

  import Akka.exec

  val stream: Stream[Seq[A]] = {
    def loop(cursorPage: CursorPage): Stream[Seq[A]] = {
      cursorPage.cursor match {
        case Some(0) => empty
        case _ => {
          val future = f(cursorPage) recover {
            case e: TwitterErrorRateLimitExceeded => CursoredResultSet(Seq.empty[A], 0)
          }
          val result = Await.result(future, Duration(60, TimeUnit.SECONDS))
          result.items #:: loop(CursorPage(Some(count), Some(result.nextCursor)))
        }
      }
    }
    loop(CursorPage(Some(count), Some(-1)))
  }
}


case class IdPaging[A <: CanBeIdentified](f: MaxIdPage => Future[ResultSet[A]], count: Int = 200, sinceId: Option[String] = None)
  extends Paging[A] {

  import Akka.exec

  val stream: Stream[Seq[A]] = {

    def loop(page: MaxIdPage): Stream[Seq[A]] = {
      page match {
        case MaxIdPage(_, Some(sinceId), Some(maxId)) if (sinceId >= maxId) => empty
        case page => {
          val future = f(page) recover {
            case e: TwitterErrorRateLimitExceeded => ResultSet(Seq.empty[A])
          }
          val result = Await.result(future, Duration(60, TimeUnit.SECONDS))
          if (result.items.isEmpty)
            empty
          else
            result.items #:: loop(MaxIdPage(Some(count), sinceId, Some(result.maxId.toString)))
        }
      }
    }

    loop(MaxIdPage(Some(count), sinceId, None))
  }
}








