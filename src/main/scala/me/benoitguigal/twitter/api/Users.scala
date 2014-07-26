package me.benoitguigal.twitter.api

import me.benoitguigal.twitter.{TwitterApi, Akka}
import me.benoitguigal.twitter.version
import spray.json.JsonParser

trait Users {
  self: TwitterApi =>

  import Akka.exec

  def accountSettings = {
    get(s"/$version/account/settings.json", Map()) map { r =>
      JsonParser(r.entity.asString)
    }
  }

  def verifyCredentials = {
    get(s"/$version/account/verify_credentials.json", Map()) map { r =>
      JsonParser(r.entity.asString).convertTo[User]
    }
  }

  def accountSettings(
      trendLocationWoeid: Option[Int] = None,
      sleepTimeEnabled: Option[Boolean] = None,
      startSleepTime: Option[String] = None,
      endSleepTime: Option[String] = None,
      timeZone: Option[String] = None,
      lang: Option[String] = None) = {

    val params = Seq(
      trendLocationWoeid map ("trend_location_woeid" -> _.toString),
      sleepTimeEnabled map ("sleep_time_enabled" -> _.toString),
      startSleepTime map ("start_sleep_time" -> _),
      endSleepTime map ("end_sleep_time" -> _),
      timeZone map ("time_zone" -> _),
      lang map ("lang" -> _)).flatten

    val content = params map { case (k, v) => s"$k=$v" } mkString "&"

    post(s"/$version/account/settings.json", Map(), Some(content)) map { r =>
      JsonParser(r.entity.asString)
    }
  }

  def accountUpdateDeliveryDevice(device: String, includeEntities: Option[Boolean] = None) = {
    throw new NotImplementedError()
  }

  def accountUpdateProfile(
      name: Option[String] = None,
      url: Option[String] = None,
      location: Option[String] = None,
      description: Option[String] = None,
      includeEntities: Option[String] = None,
      skipStatus: Option[String] = None) = {
    throw new NotImplementedError()
  }

  def accountUpdateProfileBackgroundImage(
      image: Option[Array[Byte]] = None,
      tile: Option[Boolean] = None,
      includeEntities: Option[Boolean] = None,
      skipStatus: Option[Boolean] = None,
      use: Option[Boolean] = None) = {
    throw new NotImplementedError()
  }

  def accountUpdateProfileColors(
      profileBackgroundColor: Option[String] = None,
      profileLinkColor: Option[String] = None,
      profileSidebarBorderColor: Option[String] = None,
      profileSidebarFillColor: Option[String] = None,
      profileTextColor: Option[String] = None,
      includeEntities: Option[Boolean] = None,
      skipStatus: Option[Boolean] = None) = {
    throw new NotImplementedError()
  }

  def accountUpdateProfileImage(
      image: Array[Byte],
      includeEntities: Option[Boolean] = None,
      skipStatus: Option[Boolean] = None) = {
    throw new NotImplementedError()
  }

  def blocksList(
      includeEntities: Option[Boolean] = None,
      skipStatus: Option[Boolean] = None,
      cursor: Option[Int] = None) = {
    throw new NotImplementedError()
  }

  def blockIds(
      stringifyIds: Option[Boolean] = None,
      cursor: Option[Int] = None) = {
    throw new NotImplementedError()
  }

  def blocksCreate(
      userId: Option[String],
      screenName: Option[String] = None,
      includeEntities: Option[Boolean] = None,
      skipStatus: Option[Boolean] = None) = {
    throw new NotImplementedError()
  }

  def blocksDestroy(
     userId: Option[String],
     screenName: Option[String] = None,
     includeEntities: Option[Boolean] = None,
     skipStatus: Option[Boolean] = None) = {
    throw new NotImplementedError()
  }

  def usersLookup(
      screeNames: Seq[String],
      ids: Seq[String],
      includeEntities: Option[Boolean] = None) = {
    throw new NotImplementedError()
  }

  def usersShow(
      userId: String,
      screeName: String,
      includeEntities: Option[Boolean] = None) = {
    throw new NotImplementedError()
  }

  def usersSearch(
      q: String,
      page: Option[Int] = None,
      count: Option[Int] = None,
      includeEntities: Option[Boolean] = None) = {
    throw new NotImplementedError()
  }

  def usersContributees(
      userId: Option[String],
      screenName: Option[String] = None,
      includeEntities: Option[Boolean] = None,
      skipStatus: Option[Boolean] = None) = {
    throw new NotImplementedError()
  }

  def usersContributors(
      userId: Option[String],
      screenName: Option[String] = None,
      includeEntities: Option[Boolean] = None,
      skipStatus: Option[Boolean] = None) = {
    throw new NotImplementedError()
  }

  def acccountRemoveProfileBanner() = {
    throw new NotImplementedError()
  }

  def accountUpdateProfileBanner(
      banner: Array[Byte],
      width: Option[Int] = None,
      height: Option[Int] = None,
      offsetLeft: Option[Int] = None,
      offsetTop: Option[Int] = None) = {
    throw new NotImplementedError()
  }

  def usersProfileBanners(
      userId: Option[String],
      screenName: Option[String]) = {
    throw new NotImplementedError()
  }


}
