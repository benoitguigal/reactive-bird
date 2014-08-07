package org.reactivebird.models

import spray.json.CollectionFormats



trait ModelFactory extends CollectionFormats {

  protected type Status = defaults.Status
  protected type User = defaults.User
  protected type DirectMessage = defaults.DirectMessage
  protected type Frienship = defaults.Friendship
  protected type SavedSearch = defaults.SavedSearch
  protected type Relationship = defaults.Relationship
  protected type Place = defaults.Place
  protected type Settings = defaults.Settings
  protected type Timezone = defaults.Timezone
  protected type Location = defaults.Location

  implicit protected val statusFormat = defaults.JsonFormats.statusFormat
  implicit protected val userFormat = defaults.JsonFormats.userFormat
  implicit protected val directMessageFormat = defaults.JsonFormats.directMessageFormat
  implicit protected val friendshipFormat = defaults.JsonFormats.friendshipFormat
  implicit protected val savedSearchFormat = defaults.JsonFormats.savedSearchFormat
  implicit protected val searchResultsFormat = defaults.JsonFormats.searchResultFormat
  implicit protected val relationshipFormat = defaults.JsonFormats.relationshipFormat
  implicit protected val placeFormat = defaults.JsonFormats.placeFormat
  implicit protected val timezone = defaults.JsonFormats.timezoneFormat
  implicit protected val locationFormat = defaults.JsonFormats.locationFormat
  implicit protected val settingsFormat = defaults.JsonFormats.settingsFormat

}


