package org.reactivebird.api

import scala.concurrent.Future
import org.reactivebird.{Akka, TwitterErrorRateLimitExceeded}
import org.reactivebird.models.{ResultSet, CursoredResultSet, CanBeIdentified}
import rx.lang.scala.Observable
import rx.lang.scala.subjects.ReplaySubject
import scala.util.{Success, Failure}


trait Page {
  val count: Option[Int]
}
case class MaxIdPage(count: Option[Int], sinceId: Option[String], maxId: Option[String]) extends Page
case class CursorPage(count: Option[Int], cursor: Option[Long])


object Paging {
  final val MAX_PAGES = 15
}

trait Paging[A] {

  import Akka.exec
  import Paging._

  val itemsPerPage: Int
  def pagesAsyncStream(nbPages: Int): Observable[Seq[A]]

  def items(n: Int) = {
    val nbPages = if (n % itemsPerPage == 0) { n / itemsPerPage } else { n / itemsPerPage + 1 }
    val stream = pagesAsyncStream(nbPages)
    stream.toSeq.toBlocking.toFuture.map(pages => pages.flatten.take(n))
  }
  def items: Future[Seq[A]] = {
    val stream = pagesAsyncStream(MAX_PAGES)
    stream.toSeq.toBlocking.toFuture.map(pages => pages.flatten)
  }

  def pages(n: Int): Future[Seq[Seq[A]]] = {
    val stream = pagesAsyncStream(n)
    stream.toSeq.toBlocking.toFuture
  }

  def pages: Future[Seq[Seq[A]]] = {
    val stream = pagesAsyncStream(MAX_PAGES)
    stream.toSeq.toBlocking.toFuture
  }

}


case class CursorPaging[A <: CanBeIdentified](f: CursorPage => Future[CursoredResultSet[A]], itemsPerPage: Int = 2000) extends Paging[A] {

  import Akka.exec

  def pagesAsyncStream(nbPages: Int): Observable[Seq[A]] = {

    val subject = ReplaySubject[Seq[A]]()

    def loop(cursor: CursorPage, currentPage: Int): Unit = {
      if (currentPage >= nbPages) {
        subject.onCompleted()
      }
      else {
        f(cursor) onComplete {
          case Failure(e: TwitterErrorRateLimitExceeded) => { subject.onCompleted() }
          case Failure(e) => { subject.onError(e) }
          case Success(c) => {
            subject.onNext(c.items)
            if (c.nextCursor == 0){
              subject.onCompleted()
            }
            else
              loop(CursorPage(Some(itemsPerPage), Some(c.nextCursor)), nbPages + 1)
          }
        }
      }
    }
    loop(CursorPage(Some(itemsPerPage), Some(-1)), 0)
    subject
  }
}


case class IdPaging[A <: CanBeIdentified](f: MaxIdPage => Future[ResultSet[A]], itemsPerPage: Int = 200, sinceId: Option[String] = None)
  extends Paging[A] {

  import Akka.exec

  def pagesAsyncStream(nbPages: Int) = {

  val subject = ReplaySubject[Seq[A]]()

  def loop(page: MaxIdPage): Unit = {
    page match {
      case MaxIdPage(_, Some(sinceId), Some(maxId)) if (sinceId >= maxId) => subject.onCompleted()
      case page => {
        f(page) onComplete {
          case Failure(e: TwitterErrorRateLimitExceeded) => { subject.onCompleted() }
          case Failure(e) => { subject.onError(e) }
          case Success(c) => {
            if (c.items.nonEmpty){
              subject.onNext(c.items)
              loop(MaxIdPage(Some(itemsPerPage), sinceId, Some(c.maxId.toString)))
            } else {
             subject.onCompleted()
            }
          }
        }
      }
    }
  }

  loop(MaxIdPage(Some(itemsPerPage), sinceId, None))
  subject
  }
}










