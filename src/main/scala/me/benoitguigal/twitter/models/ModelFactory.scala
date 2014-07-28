package me.benoitguigal.twitter.models

import spray.json.CollectionFormats



trait ModelFactory extends CollectionFormats {

  type Status = defaults.Status
  type User = defaults.User
  type DirectMessage = defaults.DirectMessage
  type Frienship = defaults.Friendship
  type SavedSearch = defaults.SavedSearch
  type Relationship = defaults.Relationship
  type Place = defaults.Place
  type Settings = defaults.Settings
  type Timezone = defaults.Timezone
  type Location = defaults.Location

  implicit val statusFormat = defaults.JsonFormats.statusFormat
  implicit val userFormat = defaults.JsonFormats.userFormat
  implicit val directMessageFormat = defaults.JsonFormats.directMessageFormat
  implicit val friendshipFormat = defaults.JsonFormats.friendshipFormat
  implicit val savedSearchFormat = defaults.JsonFormats.savedSearchFormat
  implicit val searchResultsFormat = defaults.JsonFormats.searchResultFormat
  implicit val relationshipFormat = defaults.JsonFormats.relationshipFormat
  implicit val placeFormat = defaults.JsonFormats.placeFormat
  implicit val timezone = defaults.JsonFormats.timezoneFormat
  implicit val locationFormat = defaults.JsonFormats.locationFormat
  implicit val settingsFormat = defaults.JsonFormats.settingsFormat

}


