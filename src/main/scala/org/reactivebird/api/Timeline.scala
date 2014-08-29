package org.reactivebird.api

import spray.json.JsonParser
import scala.concurrent.Future
import org.reactivebird.{TwitterApi, version}
import org.reactivebird.models.{ResultSetWithMaxId}



trait Timeline {
  self: TwitterApi =>

  def mentionsTimeline(
      trimUser: Option[Boolean] = None,
      contributorDetails: Option[Boolean] = None,
      includeEntities: Option[Boolean] = None)(implicit page: MaxIdPage = MaxIdPage(None, None, None)): Future[ResultSetWithMaxId[Status]] = {

    val params = Seq(
        page.count map ("count" -> _.toString),
        page.sinceId map ("since_id" -> _.toString),
        page.maxId map ("max_id" -> _.toString),
        trimUser map ("trim_user" -> _.toString),
        contributorDetails map ("contributor_details" -> _.toString),
        includeEntities map ("include_entities" -> _.toString)
    ).flatten.toMap

    get(s"/$version/statuses/mentions_timeline.json", params) map { r =>
      ResultSetWithMaxId(JsonParser(r.entity.asString).convertTo[Seq[Status]])
    }
  }


  def userTimeline(
      userId: Option[String] = None,
      screenName: Option[String] = None,
      trimUser: Option[Boolean] = None,
      excludeReplies: Option[Boolean] = None,
      contributorDetails: Option[Boolean] = None,
      includeRts: Option[Boolean] = None)(implicit page : MaxIdPage = MaxIdPage(None, None, None)): Future[ResultSetWithMaxId[Status]] = {

    require(userId.isDefined || screenName.isDefined)

    val params = Seq(
        userId map ("user_id" -> _),
        screenName map ("screen_name" -> _),
        page.sinceId map ("since_id" -> _.toString),
        page.maxId map ("max_id" -> _.toString),
        page.count map ("count" -> _.toString),
        trimUser map ("trim_user" -> _.toString),
        excludeReplies map ("exclude_replies" -> _.toString),
        contributorDetails map ("contributor_details" -> _.toString),
        includeRts map ("include_rts" -> _.toString)
    ).flatten.toMap

    get(s"/$version/statuses/user_timeline.json", params) map { r =>
      ResultSetWithMaxId(JsonParser(r.entity.asString).convertTo[Seq[Status]])
    }

  }

  def homeTimeline(
      trimUser: Option[Boolean] = None,
      excludeReplies: Option[Boolean] = None,
      contributorDetails: Option[Boolean] = None,
      includeEntities: Option[Boolean] = None)(implicit page: MaxIdPage = MaxIdPage(None, None, None)): Future[ResultSetWithMaxId[Status]] = {

    val params = Seq(
        page.count map ("count" -> _.toString),
        page.sinceId map ("since_id" -> _.toString),
        page.maxId map ("max_id" -> _.toString),
        trimUser map ("trim_user" -> _.toString),
        excludeReplies map ("exclude_replies" -> _.toString),
        contributorDetails map ("contributor_details" -> _.toString),
        includeEntities map ("include_entities" -> _.toString)
    ).flatten.toMap

    get(s"/$version/statuses/home_timeline.json", params) map { r =>
      ResultSetWithMaxId(JsonParser(r.entity.asString).convertTo[Seq[Status]])
    }

  }

  def retweetsOfMe(
      trimUser: Option[Boolean] = None,
      includeEntities: Option[Boolean] = None,
      includeUserEntities: Option[Boolean] = None)(implicit page: MaxIdPage = MaxIdPage(None, None, None)): Future[ResultSetWithMaxId[Status]] = {

    val params = Seq(
        page.count map ("count" -> _.toString),
        page.sinceId map ("since_id" -> _.toString),
        page.maxId map ("max_id" -> _.toString),
        trimUser map ("trim_user" -> _.toString),
        includeEntities map ("include_entities" -> _.toString),
        includeUserEntities map ("include_user_entities" -> _.toString)
    ).flatten.toMap

    get(s"/$version/statuses/retweets_of_me.json", params) map { r =>
      ResultSetWithMaxId(JsonParser(r.entity.asString).convertTo[Seq[Status]])
    }

  }

}


