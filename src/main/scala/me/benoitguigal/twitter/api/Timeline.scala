package me.benoitguigal.twitter.api

import spray.json.JsonParser
import scala.concurrent.Future
import me.benoitguigal.twitter.version
import me.benoitguigal.twitter.oauth.Token
import me.benoitguigal.twitter.Twitter

trait Timeline {
  self: Twitter =>

  def mentionsTimeline(
      token: Token,
      count: Option[Int] = None,
      sinceId: Option[String] = None,
      maxId: Option[String] = None,
      trimUser: Option[Boolean] = None,
      contributorDetails: Option[Boolean] = None,
      includeEntities: Option[Boolean] = None): Future[Seq[Status]] = {

    val params = Seq(
        count map ("count" -> _.toString),
        sinceId map ("since_id" -> _),
        maxId map ("max_id" -> _),
        trimUser map ("trim_user" -> _.toString),
        contributorDetails map ("contributor_details" -> _.toString),
        includeEntities map ("include_entities" -> _.toString)
    ).flatten.toMap

    get(token, s"/$version/statuses/mentions_timeline.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[Status]]
    }
  }

  def userTimeline(
      token: Token,
      userId: Option[String] ,
      screeName: Option[String],
      sinceId: Option[String] = None,
      count: Option[Int] = None,
      maxId: Option[String] = None,
      trimUser: Option[Boolean] = None,
      excludeReplies: Option[Boolean] = None,
      contributorDetails: Option[Boolean] = None,
      includeRts: Option[Boolean] = None): Future[Seq[Status]] = {

    require(userId.isDefined || screeName.isDefined)

    val params = Seq(
        userId map ("user_id" -> _),
        screeName map ("screen_name" -> _),
        sinceId map ("since_id" -> _),
        count map ("count" -> _.toString),
        maxId map ("max_id" -> _),
        trimUser map ("trim_user" -> _.toString),
        excludeReplies map ("exclude_replies" -> _.toString),
        contributorDetails map ("contributor_details" -> _.toString),
        includeRts map ("include_rts" -> _.toString)
    ).flatten.toMap

    get(token, s"/$version/statuses/user_timeline.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[Status]]
    }

  }

  def homeTimeline(
      token: Token,
      count: Option[Int] = None,
      sinceId: Option[String] = None,
      maxId: Option[String] = None,
      trimUser: Option[Boolean] = None,
      excludeReplies: Option[Boolean],
      contributorDetails: Option[Boolean] = None,
      includeEntities: Option[Boolean] = None): Future[Seq[Status]] = {

    val params = Seq(
        count map ("count" -> _.toString),
        sinceId map ("since_id" -> _),
        maxId map ("max_id" -> _),
        trimUser map ("trim_user" -> _.toString),
        excludeReplies map ("exclude_replies" -> _.toString),
        contributorDetails map ("contributor_details" -> _.toString),
        includeEntities map ("include_entities" -> _.toString)
    ).flatten.toMap

    get(token, s"/$version/statuses/home_timeline.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[Status]]
    }

  }

  def retweetsOfMe(
      token: Token,
      count: Option[Int] = None,
      sinceId: Option[String] = None,
      maxId: Option[String] = None,
      trimUser: Option[Boolean] = None,
      includeEntities: Option[Boolean],
      includeUserEntities: Option[Boolean]): Future[Seq[Status]] = {

    val params = Seq(
        count map ("count" -> _.toString),
        sinceId map ("since_id" -> _),
        maxId map ("max_id" -> _),
        trimUser map ("trim_user" -> _.toString),
        includeEntities map ("include_entities" -> _.toString),
        includeUserEntities map ("include_user_entities" -> _.toString)
    ).flatten.toMap

    get(token, "/1.1/statuses/retweets_of_me.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[Status]]
    }

  }

}