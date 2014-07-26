package me.benoitguigal.twitter.models

import spray.json.JsonFormat


trait ModelFactory {

  type Status <: CanBeIdentified
  type User <: CanBeIdentified
  type DirectMessage
  type FriendShip
  type SavedSearch
  type SearchResults
  type Category
  type Relation
  type Relationship
  type Place
  type BoundingBox

  implicit val statusFormat: JsonFormat[Status]
  implicit val userFormat: JsonFormat[User]
  implicit val directMessageFormat: JsonFormat[DirectMessage]
  implicit val savedSearchFormat: JsonFormat[SavedSearch]
  implicit val searchResultsFormat: JsonFormat[SearchResults]
  implicit val categoryFormat: JsonFormat[Category]
  implicit val relationFormat: JsonFormat[Relation]
  implicit val relationshipFormat: JsonFormat[Relationship]
  implicit val placeFormat: JsonFormat[Place]
  implicit val boundingBoxFormat: JsonFormat[BoundingBox]

}

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
