package me.benoitguigal.twitter.api

import scala.concurrent.Future
import me.benoitguigal.twitter.{TwitterApi, TwitterErrorRateLimitExceeded}
import me.benoitguigal.twitter.models.{CursoredResultSet, ResultSet, CanBeIdentified}


case class Page(count: Option[Int], sinceId: Option[String], maxId: Option[String])
case class Cursor(count: Option[Int], value: Option[Long])


trait Paging[A] {

  def items(n: Int): Future[Seq[A]]
  def items: Future[Seq[A]]

  def pages(n: Int, countPerPage: Int): Future[Seq[Seq[A]]]
  def pages(countPerPage: Int): Future[Seq[Seq[A]]]
}

case class CursorPaging[A <: CanBeIdentified](pageable: Cursor => Future[CursoredResultSet[A]]) extends Paging[A] {

  import TwitterApi.exec

  override def items(n: Int) = {

    def inner(cursor: Cursor, acc: Seq[A]): Future[Seq[A]] = {
      pageable(cursor) flatMap {
        case CursoredResultSet(Nil, _) => Future(acc.take(n))
        case CursoredResultSet(_, 0L) => Future(acc.take(n))
        case CursoredResultSet(items, newCursor) => {
          val newItems = acc ++ items
          if (newItems.size >= n)
            Future(newItems.take(n))
          else
            inner(cursor.copy(value = Some(newCursor)), acc ++ items)
        }
      } recover {
        case e: TwitterErrorRateLimitExceeded => acc
      }
    }

    inner(Cursor(Some(2000), Some(-1)), Seq.empty[A])
  }

  override def items = {
    def inner(cursor: Cursor, acc: Seq[A]): Future[Seq[A]] = {
      pageable(cursor) flatMap {
        case CursoredResultSet(Nil, _) => Future(acc)
        case CursoredResultSet(_, 0L) => Future(acc)
        case CursoredResultSet(items, newCursor) => inner(cursor.copy(value = Some(newCursor)), acc ++ items)
      } recover {
        case e: TwitterErrorRateLimitExceeded => acc
      }
    }
    inner(Cursor(Some(2000), Some(-1)), Seq.empty[A])
  }

  override def pages(n: Int, countPerPage: Int) = {

    require(n >= 1, "The number of pages must be a positive integer")

    def inner(cursor: Cursor, acc: Seq[Seq[A]], page: Int): Future[Seq[Seq[A]]] = {
      pageable(cursor) flatMap {
        case CursoredResultSet(Nil, _) => Future(acc)
        case CursoredResultSet(_, 0L) => Future(acc)
        case CursoredResultSet(items, newCursor) => {
          if (page >= n)
            Future(acc :+ items)
          else
            inner(cursor.copy(value = Some(newCursor)), acc :+items, page + 1)
        }
      } recover {
        case e: TwitterErrorRateLimitExceeded => acc
      }
    }

    inner(Cursor(Some(countPerPage), Some(-1)), Seq.empty[Seq[A]], 1)
  }

  override def pages(countPerPage: Int) = {

    def inner(cursor: Cursor, acc: Seq[Seq[A]], page: Int): Future[Seq[Seq[A]]] = {
      pageable(cursor) flatMap {
        case CursoredResultSet(Nil, _) => Future(acc)
        case CursoredResultSet(_, 0L) => Future(acc)
        case CursoredResultSet(items, newCursor) => inner(cursor.copy(value = Some(newCursor)), acc :+items, page + 1)
      } recover {
        case e: TwitterErrorRateLimitExceeded => acc
      }
    }

    inner(Cursor(Some(countPerPage), Some(-1)), Seq.empty[Seq[A]], 1)
  }

}

case class IdPaging[A <: CanBeIdentified](pageable: Page => Future[ResultSet[A]]) extends Paging[A] {

  import TwitterApi.exec

  def items(n: Int) = {

    def inner(currentPage: Page, acc: Seq[A]): Future[Seq[A]] = {
      pageable(currentPage) flatMap {
        case ResultSet(Nil) => Future(acc.take(n))
        case resultSet => {
          val newPage = currentPage.copy(maxId = Some(resultSet.maxId.toString))
          val newItems = acc ++ resultSet.items
          if (newItems.size >= n)
            Future(newItems.take(n))
          else
            inner(newPage, newItems)
        }
      } recover {
        case e: TwitterErrorRateLimitExceeded => acc
      }
    }

    inner(Page(Some(200), None, None), Seq.empty[A])

  }

  def items = {

    def inner(currentPage: Page, acc: Seq[A]): Future[Seq[A]] = {
      pageable(currentPage) flatMap {
        case ResultSet(Nil) => Future(acc)
        case resultSet => {
          val maxId = resultSet.maxId.toString
          val newPage = currentPage.copy(maxId = Some(maxId))
          inner(newPage, acc ++ resultSet.items)
        }
      } recover {
        case e: TwitterErrorRateLimitExceeded => acc
      }
    }

    inner(Page(Some(200), None, None), Seq.empty[A])
  }

  def pages(n: Int, countPerPage: Int) = {

    require(n >= 1, "The number of pages must be a positive integer")

    def inner(currentPage: Page, acc: Seq[Seq[A]], page: Int): Future[Seq[Seq[A]]] = {
      pageable(currentPage) flatMap {
        case ResultSet(Nil) => Future(acc)
        case resultSet => {
          val maxId = resultSet.maxId.toString
          val newPage = currentPage.copy(maxId = Some(maxId))
          if (page >= n)
            Future(acc :+ resultSet.items)
          else
            inner(newPage, acc :+ resultSet.items, page + 1)
        }
      } recover {
        case e: TwitterErrorRateLimitExceeded => acc
      }
    }

    inner(Page(Some(countPerPage), None, None), Seq.empty[Seq[A]], 1)

  }

  def pages(countPerPage: Int) = {
    def inner(currentPage: Page, acc: Seq[Seq[A]]): Future[Seq[Seq[A]]] = {
      pageable(currentPage) flatMap {
        case ResultSet(Nil) => Future(acc)
        case resultSet => {
          val maxId = resultSet.maxId.toString
          val newPage = currentPage.copy(maxId = Some(maxId))
          inner(newPage, acc :+ resultSet.items)
        }
      } recover {
        case e: TwitterErrorRateLimitExceeded => acc
      }
    }

    inner(Page(Some(countPerPage), None, None), Seq.empty[Seq[A]])
  }

}




