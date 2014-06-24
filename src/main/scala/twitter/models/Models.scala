package twitter.models

import org.joda.time.DateTime

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
    user: User)

case class Coordinates(longitude: Double, latitude: Double)

case class User(
    created_at: DateTime,
    followers_count: Int,
    friends_count: Int,
    id: Long,
    id_str: String,
    name: String,
    screen_name: String,
    statuses_count: Int)

case class Entities(
    hashtags: Option[Seq[Hashtag]],
    media: Option[Seq[Media]],
    urls: Option[Seq[URL]],
    user_mentions: Option[Seq[UserMention]])


case class Hashtag(
    indices: Seq[Int],
    text: String)

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

case class Place(
    country: String,
    country_code: String,
    full_name: String,
    id: String,
    name: String,
    place_type: String,
    url: String)




