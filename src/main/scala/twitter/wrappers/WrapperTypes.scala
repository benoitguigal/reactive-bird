package twitter.wrappers

import spray.json.{CollectionFormats, JsonFormat}

trait WrapperTypes extends CollectionFormats {
  type Status
  type User
  implicit val statusFormat: JsonFormat[Status]
  implicit val userFormat: JsonFormat[User]
}

trait DefaultWrapperTypes extends WrapperTypes {
  type Status = twitter.wrappers.defaults.Status
  type User = twitter.wrappers.defaults.User
  implicit val statusFormat = defaults.JsonFormats.statusFormat
  implicit val userFormat = defaults.JsonFormats.userFormat
}
