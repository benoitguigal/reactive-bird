package org.reactivebird.api

import org.reactivebird.{TwitterApi, Akka}
import scala.concurrent.Future
import spray.json.JsonParser
import org.reactivebird.version
import org.reactivebird.%%


trait Tweets {
  self: TwitterApi =>

  import Akka.exec

  def retweets(id: String, count: Option[Int] = None, trimUser: Option[Boolean] = None): Future[Seq[Status]] = {
    val path = s"/$version/statuses/retweets/$id.json"
    val params = Seq(
      count map ("count" -> _.toString),
      trimUser map ("trim_user" -> _.toString)).flatten.toMap
    get(path, params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[Status]]
    }
  }

  def show(
      id: String,
      trimUser: Option[Boolean] = None,
      includeMyRetweet: Option[Boolean] = None,
      includeEntitties: Option[Boolean] = None): Future[Status] = {

    val path = s"/$version/statuses/show.json"
    val params = Seq(
      Some("id" -> id),
      trimUser map ("trim_user" -> _.toString),
      includeMyRetweet map ("include_my_retweet" -> _.toString),
      includeEntitties map ("include_entities" -> _.toString)).flatten.toMap
    get(path, params) map { r =>
      JsonParser(r.entity.asString).convertTo[Status]
    }
  }

  def destroy(id: String, trimUser: Option[Boolean] = None): Future[Status] =  {
    val path = s"/$version/statuses/destroy/$id.json"
    val params = Seq(
        trimUser map ("trim_user" -> _.toString)).flatten.toMap
    post(path, params) map { r =>
      JsonParser(r.entity.asString).convertTo[Status]
    }
  }

  def retweet(id: String, trimUser: Option[Boolean] = None): Future[Status] = {
    val path = s"/$version/statuses/retweet/$id.json"
    val params = Seq(
      trimUser map ("trim_user" -> _.toString)).flatten.toMap
    post(path, params) map { r =>
      JsonParser(r.entity.asString).convertTo[Status]
    }
  }

  def update(
      status: String,
      inReplyToStatus: Option[String] = None,
      possiblySensitive: Option[String] = None,
      lat: Option[Double] = None,
      long: Option[Double] = None,
      placeId: Option[String] = None,
      displayCoordinates: Option[Boolean] = None,
      trimUser: Option[Boolean] = None): Future[Status] = {

    val content = s"status=${%%(status)}"
    val path = s"/$version/statuses/update.json"
    post(path, Map(), Some(content)) map { r =>
      JsonParser(r.entity.asString).convertTo[Status]
    }
  }

  def oembed(
      id: Option[String],
      url: Option[String] = None,
      maxWidth: Option[Int] = None,
      hideMedia: Option[Boolean] = None,
      hideThread: Option[Boolean] = None,
      omitScript: Option[Boolean] = None,
      align: Option[String] = None,
      related: Option[String] = None,
      lang: Option[String] = None) = {

    require(id.nonEmpty || url.nonEmpty)

    val params = Seq(
      id map ("id" -> _),
      url map ("url" -> _),
      maxWidth map ("maxwidth" -> _.toString),
      hideMedia map ("hide_media" -> _.toString),
      hideThread map ("hide_thread" -> _.toString),
      omitScript map ("omit_script" -> _.toString),
      align map ("align" -> _),
      related map ("related" -> _),
      lang map ("lang" -> _)).flatten.toMap

    val path = s"/$version/statuses/oembed.json"
    get(path, params) map { r =>
      JsonParser(r.entity.asString)
    }
  }

  def retweeters(id: String, cursor: Option[String] = None, stringifyIds: Option[Boolean] = None) = {
    val params = Seq(
      Some("id" -> id),
      cursor map ("cursor" -> _),
      stringifyIds map ("stringify_ids" -> _.toString)).flatten.toMap

    val path = s"/$version/statuses/retweeters/ids.json"
    get(path, params) map { r =>
      JsonParser(r.entity.asString)
    }
  }


}
