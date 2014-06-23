package twitter

import org.joda.time.DateTime


case class Status(
    contributors: Seq[User],
    coordinates: Coordinates,
    createdAt: DateTime,
    currentUserRetweet: Option[User],
    entities: Entities,
    favoriteCount: Option[Int],
    favorited: Option[Boolean],
    filterLevel: String,
    id: Long,
    idStr: String,
    inReplyToScreenName: Option[String],
    inReplyToStatusId: Option[String],
    inReplyToStatusIdStr: Option[String],
    inReplyToUserId: Option[Long],
    inReplyToUserIdStr: Option[String],
    lang: Option[String],
    place: Option[Place],
    possiblySensitive: Option[Boolean],
    scopes: Map[String, Boolean],
    retweetCount: Int,
    retweeted: Boolean,
    retweetedStatus: Option[Status],
    source: String,
    text: String,
    truncated: Boolean,
    user: User,
    withheldCopyright: Option[Boolean],
    withheldInCountries: Seq[String],
    withheldScope: Option[String])


case class Coordinates(longitude: Double, latitude: Double)


