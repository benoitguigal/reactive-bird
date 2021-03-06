package org.reactivebird.api

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.mock.MockitoSugar
import org.reactivebird.models.{CanBeIdentified, ResultSetWithMaxId, ResultSetWithCursor}
import scala.concurrent.{Await, Future}
import org.mockito.Matchers._
import org.mockito.Mockito._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import akka.actor.ActorSystem


class CursorPagingSpec extends FlatSpec with Matchers with MockitoSugar {

  implicit val system = ActorSystem()

  it should "get all items from the pages in the enumerator" in {
    val pageable = mock[CursorPage => Future[ResultSetWithCursor[Int]]]
    when(pageable.apply(any[CursorPage]))
      .thenReturn(Future(ResultSetWithCursor(Seq(1, 2, 3, 4, 5), 1L)))
      .thenReturn(Future(ResultSetWithCursor(Seq(6, 7, 8, 9, 10), 2L)))
      .thenReturn(Future(ResultSetWithCursor(Seq(11, 12, 13, 14, 15), 0)))
    val paging = CursorPaging(pageable, 5)
    val items = Await.result(paging.items, Duration(10, TimeUnit.SECONDS))
    items should equal (Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15))
    verify(pageable, times(3)).apply(any[CursorPage])
  }

  it should "get a specific number of items from the pages and don't evaluate other pages in the enumerator" in {
    val pageable = mock[CursorPage => Future[ResultSetWithCursor[Int]]]
    when(pageable.apply(any[CursorPage]))
      .thenReturn(Future(ResultSetWithCursor(Seq(1, 2, 3, 4, 5), 1L)))
      .thenReturn(Future(ResultSetWithCursor(Seq(6, 7, 8, 9, 10), 2L)))
      .thenReturn(Future(ResultSetWithCursor(Seq(11, 12, 13, 14, 15), 0)))
    val paging = CursorPaging(pageable, 5)
    val items = Await.result(paging.items(3), Duration(10, TimeUnit.SECONDS))
    items should have size(3)
    items should equal(Seq(1, 2, 3))
    verify(pageable, times(2)).apply(any[CursorPage])
  }

  it should "get all pages in the enumerator" in  {
    val pageable = mock[CursorPage => Future[ResultSetWithCursor[Int]]]
    when(pageable.apply(any[CursorPage]))
      .thenReturn(Future(ResultSetWithCursor(Seq(1, 2, 3, 4, 5), 1L)))
      .thenReturn(Future(ResultSetWithCursor(Seq(6, 7, 8, 9, 10), 2L)))
      .thenReturn(Future(ResultSetWithCursor(Seq(11, 12, 13, 14, 15), 0)))
    val paging = CursorPaging(pageable, 5)
    val pages = Await.result(paging.pages, Duration(10, TimeUnit.SECONDS))
    pages should have size(3)
    pages should equal(Seq(Seq(1, 2, 3, 4, 5), Seq(6, 7, 8, 9, 10), Seq(11, 12, 13, 14, 15)))
    verify(pageable, times(3)).apply(any[CursorPage])
  }

  it should "get a specific number of pages from the enumerator and don't evaluate other pages" in {
    val pageable = mock[CursorPage => Future[ResultSetWithCursor[Int]]]
    when(pageable.apply(any[CursorPage]))
      .thenReturn(Future(ResultSetWithCursor(Seq(1, 2, 3, 4, 5), 1L)))
      .thenReturn(Future(ResultSetWithCursor(Seq(6, 7, 8, 9, 10), 2L)))
      .thenReturn(Future(ResultSetWithCursor(Seq(11, 12, 13, 14, 15), 0)))
    val paging = CursorPaging(pageable, 5)
    val pages = Await.result(paging.pages(1), Duration(10, TimeUnit.SECONDS))
    pages should have size(1)
    pages should equal(Seq(Seq(1, 2, 3, 4, 5)))
    verify(pageable, times(2)).apply(any[CursorPage])
  }


}


class IdPagingSpec extends FlatSpec with Matchers with MockitoSugar {

  implicit val system = ActorSystem()

  case class CanBeIdentifiedInt(n: Long) extends CanBeIdentified {
    override val id: Long = n
  }

  it should "get all items from the pages in the enumerator" in {
    val pageable = mock[MaxIdPage => Future[ResultSetWithMaxId[CanBeIdentifiedInt]]]
    when(pageable.apply(any[MaxIdPage]))
      .thenReturn(Future(ResultSetWithMaxId(Seq(CanBeIdentifiedInt(4), CanBeIdentifiedInt(3)))))
      .thenReturn(Future(ResultSetWithMaxId(Seq(CanBeIdentifiedInt(2), CanBeIdentifiedInt(1)))))
      .thenReturn(Future(ResultSetWithMaxId(Seq.empty[CanBeIdentifiedInt])))
    val paging = IdPaging(pageable, 2)
    val items = Await.result(paging.items, Duration(10, TimeUnit.SECONDS))
    items should equal (Seq(CanBeIdentifiedInt(4), CanBeIdentifiedInt(3), CanBeIdentifiedInt(2), CanBeIdentifiedInt(1)))
    verify(pageable, times(3)).apply(any[MaxIdPage])
  }

  it should "get all pages in the enumerator" in {
    val pageable = mock[MaxIdPage => Future[ResultSetWithMaxId[CanBeIdentifiedInt]]]
    when(pageable.apply(any[MaxIdPage]))
      .thenReturn(Future(ResultSetWithMaxId(Seq(CanBeIdentifiedInt(4), CanBeIdentifiedInt(3)))))
      .thenReturn(Future(ResultSetWithMaxId(Seq(CanBeIdentifiedInt(2), CanBeIdentifiedInt(1)))))
      .thenReturn(Future(ResultSetWithMaxId(Seq.empty[CanBeIdentifiedInt])))
    val paging = IdPaging(pageable, 2)
    val pages =  Await.result(paging.pages, Duration(10, TimeUnit.SECONDS))
    pages should equal(Seq(Seq(CanBeIdentifiedInt(4), CanBeIdentifiedInt(3)), Seq(CanBeIdentifiedInt(2), CanBeIdentifiedInt(1))))
    verify(pageable, times(3)).apply(any[MaxIdPage])
  }


}
