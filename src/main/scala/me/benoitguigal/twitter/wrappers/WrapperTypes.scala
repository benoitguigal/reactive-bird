package me.benoitguigal.twitter.wrappers

import spray.json.{CollectionFormats, JsonFormat}
import me.benoitguigal.twitter.models.CanBeIdentified

trait WrapperTypes extends CollectionFormats {
  type Status <: CanBeIdentified
  type User <: CanBeIdentified
  implicit val statusFormat: JsonFormat[Status]
  implicit val userFormat: JsonFormat[User]

}

trait DefaultWrapperTypes extends WrapperTypes {
  type Status = me.benoitguigal.twitter.wrappers.defaults.Status
  type User = me.benoitguigal.twitter.wrappers.defaults.User
  implicit val statusFormat = defaults.JsonFormats.statusFormat
  implicit val userFormat = defaults.JsonFormats.userFormat
}
