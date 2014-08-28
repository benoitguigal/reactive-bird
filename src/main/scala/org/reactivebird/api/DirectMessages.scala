package org.reactivebird.api

import org.reactivebird.{Akka, TwitterApi}
import org.reactivebird.{version, %%}
import spray.json.JsonParser


trait DirectMessages {
 self: TwitterApi =>

  import Akka.exec

  def directMessages(
      includeEntities: Option[Boolean] = None,
      skipStatus: Option[Boolean] = None)(implicit page: MaxIdPage = MaxIdPage(None, None, None)) = {

    val params = Seq(
      page.count map ("count" -> _.toString),
      page.maxId map ("max_id" -> _.toString),
      page.sinceId map ("since_id" -> _.toString),
      includeEntities map ("include_entities" -> _.toString),
      skipStatus map ("skip_status" -> _.toString)).flatten.toMap

    get(s"/$version/direct_messages.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[DirectMessage]]
    }
  }

  def directMessagesSent(
      includeEntities: Option[Boolean] = None,
      skipStatus: Option[Boolean] = None)(implicit page: MaxIdPage = MaxIdPage(None, None, None)) = {

    val params = Seq(
      page.count map ("count" -> _.toString),
      page.maxId map ("max_id" -> _.toString),
      page.sinceId map ("since_id" -> _.toString),
      includeEntities map ("include_entities" -> _.toString),
      skipStatus map ("skip_status" -> _.toString)).flatten.toMap

    get(s"/$version/direct_messages/sent.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[DirectMessage]]
    }
  }

  def directMessagesShow(id: String) = {
    get(s"/$version/direct_messages/show.json", Map("id" -> id)) map { r =>
      JsonParser(r.entity.asString).convertTo[DirectMessage]
    }
  }

  def directMessagesDestroy(id: String, includeEntities: Option[Boolean] = None) = {
    val params = Seq(
      Some("id" -> id),
      includeEntities map ("include_entities" -> _)).flatten.toMap
    val content = params map { case (k, v) => s"$k=$v" } mkString "&"
    post(s"/$version/direct_messages/destroy.json", Map(), Some(content)) map { r =>
      JsonParser(r.entity.asString).convertTo[DirectMessage]
    }
  }

  def directMessagesNew(text: String, userId: Option[String] = None, screenName: Option[String] = None) = {

    require(userId.isDefined || screenName.isDefined, "One of user_id or screen_name are required")

    val params = Seq(
      userId map ("user_id" -> _),
      screenName map ("screen_name" -> _),
      Some("text" -> %%(text))).flatten.toMap

    val content = params map { case (k, v) => s"$k=$v" } mkString "&"

    post(s"/$version/direct_messages/new.json", Map(), Some(content)) map { r =>
      JsonParser(r.entity.asString).convertTo[DirectMessage]
    }

  }

}
