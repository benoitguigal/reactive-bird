package me.benoitguigal.twitter.wrappers.defaults

import org.joda.time.DateTime
import me.benoitguigal.twitter.models.CanBeIdentified


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