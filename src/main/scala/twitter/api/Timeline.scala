package twitter.api

import twitter.Twitter
import spray.json.JsonParser
import scala.concurrent.Future
import twitter.version

trait Timeline {
  self: Twitter =>

  def mentionsTimeline(
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

    get(s"/$version/statuses/mentions_timeline.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[Status]]
    }
  }

  def userTimeline(
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

    get(s"/$version/statuses/user_timeline.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[Status]]
    }

  }

  def homeTimeline(
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

    get(s"/$version/statuses/home_timeline.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[Status]]
    }

  }

  def retweetsOfMe(
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

    get("/1.1/statuses/retweets_of_me.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[Status]]
    }

  }

}
