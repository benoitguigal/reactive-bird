package me.benoitguigal.twitter.api

import me.benoitguigal.twitter.TwitterApi
import me.benoitguigal.twitter.version
import spray.json.JsonParser

trait Users {
  self: TwitterApi =>

  import TwitterApi.exec

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

}
