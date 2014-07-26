package me.benoitguigal.twitter.wrappers.defaults

import org.joda.time.DateTime
import me.benoitguigal.twitter.models.CanBeIdentified


case class User(
     created_at: DateTime,
     followers_count: Int,
     friends_count: Int,
     id: Long,
     id_str: String,
     name: String,
     screen_name: String,
     statuses_count: Int) extends CanBeIdentified
