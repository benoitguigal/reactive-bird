package me.benoitguigal.twitter.models


import org.joda.time.DateTime
import spray.json.{CollectionFormats, JsonFormat}


trait ModelFactory extends CollectionFormats {

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

case class User(
     created_at: DateTime,
     followers_count: Int,
     friends_count: Int,
     id: Long,
     id_str: String,
     name: String,
     screen_name: String,
     statuses_count: Int) extends CanBeIdentified

case class Status(
     coordinates: Option[Coordinates],
     created_at: DateTime,
     entities: Entities,
     favorite_count: Option[Int],
     id: Long,
     id_str: String,
     retweet_count: Int,
     retweeted: Boolean,
     source: String,
     text: String,
     user: User) extends CanBeIdentified

case class Place(
    country: String,
    country_code: String,
    full_name: String,
    id: String,
    name: String,
    place_type: String,
    url: String)

case class Entities(
     hashtags: Option[Seq[Hashtag]],
     media: Option[Seq[Media]],
     urls: Option[Seq[URL]],
     user_mentions: Option[Seq[UserMention]])

case class Coordinates(longitude: Double, latitude: Double)

case class Hashtag(indices: Seq[Int], text: String)

case class Media(
    display_url: String,
    expanded_url: String,
    id: Long,
    id_str: String,
    indices: Seq[Int],
    media_url: String,
    url: String)

case class URL(display_url: String, expanded_url: String, indices: Seq[Int], url: String)

case class UserMention(id: Long, id_str: String, indices: Seq[Int], name: String, screen_name: String)


trait AbstractResultSet[A <: CanBeIdentified] {
  val items: Seq[A]
  def maxId = items.last.id - 1
  def ids = items map (_.id)
}

case class ResultSet[A <: CanBeIdentified](items: Seq[A]) extends AbstractResultSet[A]

case class CursoredResultSet[A <: CanBeIdentified](items: Seq[A], val nextCursor: Long) extends AbstractResultSet[A]
