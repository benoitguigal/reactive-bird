package org.reactivebird.models


trait CanBeIdentified {
  val id: Long
}

case class UserId(id: Long) extends CanBeIdentified

trait AbstractResultSet[A <: CanBeIdentified] {
  val items: Seq[A]
  def maxId = items.last.id - 1
  def ids = items map (_.id)
}

case class ResultSet[A <: CanBeIdentified](items: Seq[A]) extends AbstractResultSet[A]

case class CursoredResultSet[A <: CanBeIdentified](items: Seq[A], val nextCursor: Long) extends AbstractResultSet[A]

