package org.reactivebird.api

import play.api.libs.iteratee._
import scala.concurrent.{ExecutionContext, Future}
import org.reactivebird.models.{ResultSetWithMaxId, ResultSetWithCursor, CanBeIdentified}
import org.reactivebird.TwitterErrorRateLimitExceeded
import akka.actor.ActorSystem


trait Page {
  val count: Option[Int]
}
case class MaxIdPage(count: Option[Int], sinceId: Option[Long], maxId: Option[Long]) extends Page
case class CursorPage(count: Option[Int], cursor: Option[Long])



trait Paging[A] {

  implicit val system: ActorSystem
  implicit val exec = system.dispatcher

  val enumerator: Enumerator[Seq[A]]

  def items: Future[Seq[A]] = {
    val iterator = Iteratee.fold[Seq[A], Seq[A]](Seq.empty[A]){ (acc, elt) => acc ++ elt }
    enumerator run iterator
  }

  def items(n: Int): Future[Seq[A]] = {
    enumerator run concateneN(n)
  }

  def pages: Future[Seq[Seq[A]]] = {
    val iterator = Iteratee.fold[Seq[A], Seq[Seq[A]]](Seq.empty[Seq[A]]){ (acc, elt) => acc :+ elt }
    enumerator run iterator
  }

  def pages(n: Int): Future[Seq[Seq[A]]] = {
    enumerator run takeN(n)
  }


  private[this] def concateneN(n: Int): Iteratee[Seq[A], Seq[A]] = {
    def step(idx: Int, acc: Seq[A])(i: Input[Seq[A]]): Iteratee[Seq[A], Seq[A]] = {
      i match {
        case Input.EOF | Input.Empty => Done(acc, Input.EOF)
        case Input.El(e) =>
          if (idx < n)
            Cont[Seq[A], Seq[A]](i => step(idx + e.size, acc ++ e)(i))
          else
            Done(acc.take(n), Input.EOF)
      }
    }
    Cont[Seq[A], Seq[A]](i => step(0, Seq.empty[A])(i))
  }


  private[this] def takeN(n: Int): Iteratee[Seq[A], Seq[Seq[A]]] = {
    def step(idx: Int, acc: Seq[Seq[A]])(i: Input[Seq[A]]): Iteratee[Seq[A], Seq[Seq[A]]] = i match {
      case Input.EOF | Input.Empty => Done(acc, Input.EOF)
      case Input.El(e) =>
        if (idx < n)
          Cont[Seq[A], Seq[Seq[A]]](i => step(idx + 1, acc :+ e)(i))
        else
          Done(acc, Input.EOF)
    }
    Cont[Seq[A], Seq[Seq[A]]](i => step(0, Seq.empty[Seq[A]])(i))
  }


}

case class CursorPaging[A](pageable: CursorPage => Future[ResultSetWithCursor[A]], itemsPerPage: Int = 2000)(implicit val system: ActorSystem)
  extends Paging[A] {

  private val seedPage = CursorPage(Some(itemsPerPage), Some(-1))
  val enumerator: Enumerator[Seq[A]] = Enumerator.unfoldM[CursorPage, Seq[A]](seedPage){ currentPage =>
    if (currentPage.cursor.get == 0)
      Future.successful[Option[(CursorPage, Seq[A])]]{ None }
    else {
      pageable(currentPage) map {
        case ResultSetWithCursor(items, nextCursor) => Some(CursorPage(Some(itemsPerPage), Some(nextCursor)) -> items)
      } recoverWith {
        case e: TwitterErrorRateLimitExceeded => Future.successful[Option[(CursorPage, Seq[A])]]{ None }
      }
    }

  }

}

case class IdPaging[A <: CanBeIdentified](
    pageable: MaxIdPage => Future[ResultSetWithMaxId[A]],
    itemsPerPage: Int = 200,
    sinceId: Option[Long] = None)(
    implicit val system: ActorSystem)
  extends Paging[A] {

  private val seedPage = MaxIdPage(Some(itemsPerPage), sinceId, None)
  override val enumerator: Enumerator[Seq[A]] = Enumerator.unfoldM[MaxIdPage, Seq[A]](seedPage){ currentPage =>
    pageable(currentPage) map { r =>
      if (r.items.nonEmpty)
        Some((MaxIdPage(Some(itemsPerPage), sinceId, Some(r.maxId)), r.items))
      else
        Option.empty[(MaxIdPage, Seq[A])]
    } recoverWith {
      case e: TwitterErrorRateLimitExceeded => Future.successful[Option[(MaxIdPage, Seq[A])]]{ None }
    }

  }
}











