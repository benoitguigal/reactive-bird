package org.reactivebird.models


trait CanBeIdentified {
  val id: Long
}

case class UserId(id: Long) extends CanBeIdentified

trait ResultSet[A] {
  val items: Seq[A]
}

case class ResultSetWithMaxId[A <: CanBeIdentified](items: Seq[A]) extends ResultSet[A] {
  def maxId = items.last.id - 1
}

case class ResultSetWithCursor[A](items: Seq[A], nextCursor: Long) extends ResultSet[A]

