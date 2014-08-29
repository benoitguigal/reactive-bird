package org.reactivebird.api

import org.reactivebird.TwitterApi
import org.reactivebird.version
import spray.json.{JsNumber, JsArray, JsonParser}

trait Users {
  self: TwitterApi =>

  def accountSettings = {
    get(s"/$version/account/settings.json", Map()) map { r =>
      JsonParser(r.entity.asString).convertTo[Settings]
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

    require(trendLocationWoeid.nonEmpty || sleepTimeEnabled.nonEmpty || startSleepTime.nonEmpty || endSleepTime.nonEmpty || timeZone.nonEmpty || lang.nonEmpty,
      "While no specific parameter is required, at least one of these parameters should be provided when executing this method.")

    val params = Seq(
      trendLocationWoeid map ("trend_location_woeid" -> _.toString),
      sleepTimeEnabled map ("sleep_time_enabled" -> _.toString),
      startSleepTime map ("start_sleep_time" -> _),
      endSleepTime map ("end_sleep_time" -> _),
      timeZone map ("time_zone" -> _),
      lang map ("lang" -> _)).flatten

    val content = params map { case (k, v) => s"$k=$v" } mkString "&"

    post(s"/$version/account/settings.json", Map(), Some(content)) map { r =>
      JsonParser(r.entity.asString).convertTo[Settings]
    }
  }


  def accountUpdateProfile(
      name: Option[String] = None,
      url: Option[String] = None,
      location: Option[String] = None,
      description: Option[String] = None,
      includeEntities: Option[String] = None,
      skipStatus: Option[String] = None) = {

    require(name.nonEmpty || url.nonEmpty || location.nonEmpty || description.nonEmpty || includeEntities.nonEmpty || skipStatus.nonEmpty,
      "While no specific parameter is required, at least one of these parameters should be provided when executing this method.")

    val params = Seq(
      name map ("name" -> _),
      url map ("url" -> _),
      location map ("location" -> _),
      description map ("description" -> _),
      includeEntities map ("include_entities" -> _),
      skipStatus map ("skip_status" -> _)).flatten.toMap

    val content = params map { case (k, v) => s"$k=$v" } mkString "&"

    post(s"/$version/account/update_profile.json", Map(), Some(content)) map { r =>
      JsonParser(r.entity.asString).convertTo[User]
    }
  }


  def accountUpdateProfileColors(
      profileBackgroundColor: Option[String] = None,
      profileLinkColor: Option[String] = None,
      profileSidebarBorderColor: Option[String] = None,
      profileSidebarFillColor: Option[String] = None,
      profileTextColor: Option[String] = None,
      includeEntities: Option[Boolean] = None,
      skipStatus: Option[Boolean] = None) = {

    val params = Seq(
      profileBackgroundColor map ("profile_background_color" -> _),
      profileLinkColor map ("profile_link_color" -> _),
      profileSidebarBorderColor map ("profile_sidebar_border_color" -> _),
      profileSidebarFillColor map ("profile_sidebar_fill_color" -> _),
      profileTextColor map ("profile_text_color" -> _),
      includeEntities map ("include_entities" -> _.toString),
      skipStatus map ("skip_status" -> _.toString)).flatten.toMap

      val content = params map { case (k, v) => s"$k=$v" } mkString "&"

      post(s"/$version/account/update_profile_colors.json", Map(), Some(content)) map { r =>
        JsonParser(r.entity.asString).convertTo[User]
      }

  }

  def blocksList(
      includeEntities: Option[Boolean] = None,
      skipStatus: Option[Boolean] = None) = {

    val params = Seq(
      includeEntities map ("include_entities" -> _.toString),
      skipStatus map ("skip_status" -> _.toString)).flatten.toMap

    get(s"/$version/blocks/list.json", params) map { r =>
      JsonParser(r.entity.asString).asJsObject.getFields("users") match {
        case Seq(users) => users.convertTo[Seq[User]]
      }
    }
  }

  def blocksIds(stringifyIds: Option[Boolean] = None) = {

    val params = Seq(stringifyIds map ("stringify_ids" -> _.toString)).flatten.toMap

    get(s"/$version/blocks/ids.json", params) map { r =>
      JsonParser(r.entity.asString).asJsObject.getFields("ids") match {
        case Seq(JsArray(ids)) => ids collect { case JsNumber(id) => id }
      }
    }
  }

  def blocksCreate(
      userId: Option[String] = None,
      screenName: Option[String] = None,
      includeEntities: Option[Boolean] = None,
      skipStatus: Option[Boolean] = None) = {

    require(userId.isDefined || screenName.isDefined, "Either screen_name or user_id must be provided.")

    val params = Seq(
      userId map ("user_id" -> _),
      screenName map ("screen_name" -> _),
      includeEntities map ("include_entities" -> _.toString),
      skipStatus map ("skip_status" -> _.toString)).flatten.toMap

    val content = params map { case (k, v) => s"$k=$v" } mkString "&"

    post(s"/$version/blocks/create.json", Map(), Some(content)) map { r =>
      JsonParser(r.entity.asString).convertTo[User]
    }

  }

  def blocksDestroy(
     userId: Option[String] = None,
     screenName: Option[String] = None,
     includeEntities: Option[Boolean] = None,
     skipStatus: Option[Boolean] = None) = {

    require(userId.isDefined || screenName.isDefined, "Either screen_name or user_id must be provided.")

    val params = Seq(
      userId map ("user_id" -> _),
      screenName map ("screen_name" -> _),
      includeEntities map ("include_entities" -> _.toString),
      skipStatus map ("skip_status" -> _.toString)).flatten.toMap

    val content = params map { case (k, v) => s"$k=$v" } mkString "&"

    post(s"/$version/blocks/destroy.json", Map(), Some(content)) map { r =>
      JsonParser(r.entity.asString).convertTo[User]
    }
  }

  def usersLookup(
      screenNames: Seq[String] = Seq.empty[String],
      user_ids: Seq[String] = Seq.empty[String],
      includeEntities: Option[Boolean] = None) = {

    require(screenNames.size <= 100 && user_ids.size <= 100, "Max 100 screen_names or user_ids are allowed")

    val params = Seq(
      Some("screen_name" -> screenNames.mkString(",")),
      Some("user_id" -> user_ids.mkString(",")),
      includeEntities map ("include_entities" -> _.toString)).flatten.toMap

    get(s"/$version/users/lookup.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[User]]
    }
  }

  def usersShow(
      userId: Option[String] = None,
      screenName: Option[String] = None,
      includeEntities: Option[Boolean] = None) = {

    require(userId.isDefined || screenName.isDefined, "Either screen_name or user_id must be provided.")

    val params = Seq(
      userId map ("user_id" -> _),
      screenName map ("screen_name" -> _),
      includeEntities map ("include_entities" -> _.toString)).flatten.toMap

    get(s"/$version/users/show.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[User]
    }

  }

  def usersSearch(
      q: String,
      page: Option[Int] = None,
      count: Option[Int] = None,
      includeEntities: Option[Boolean] = None) = {

    val params = Seq(
      Some("q" -> q),
      page map ("page" -> _.toString),
      count map ("count" -> _.toString),
      includeEntities map ("include_entities" -> _.toString)).flatten.toMap

    get(s"/$version/users/search.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[User]]
    }
  }

}
