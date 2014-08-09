package org.reactivebird.api

import org.reactivebird.{Akka, version, TwitterApi}
import spray.json.{JsNumber, JsArray, JsonParser}
import scala.concurrent.Future
import org.reactivebird.models.{UserId, ResultSetWithCursor}


trait FriendsAndFollowers {
  self: TwitterApi =>

  import Akka.exec

  def friendsIds(
      userId: Option[String],
      screenName: Option[String] = None,
      stringifyIds: Option[Boolean] = None)(implicit page: CursorPage = CursorPage(None, None)): Future[ResultSetWithCursor[UserId]] = {

    require(userId.nonEmpty || screenName.nonEmpty, "Either a screen_name or a user_id must be provided.")

    val params = Seq(
      userId map ("user_id" -> _),
      screenName map ("screen_name" -> _),
      page.cursor map ("cursor" -> _.toString),
      stringifyIds map ("stringify_ids" -> _.toString),
      page.count map ("count" -> _.toString)).flatten.toMap

    get(s"/$version/friends/ids.json", params) map { r =>
      val json = JsonParser(r.entity.asString)
      json.asJsObject.getFields("next_cursor","ids") match {
        case Seq(JsNumber(nextCursor), JsArray(ids)) => {
          val friendIds = ids.collect { case JsNumber(id) => UserId(id.toLong) }
          ResultSetWithCursor(friendIds, nextCursor.toLong)
        }
        case _ => throw new Exception("Failed parsing friends list")
      }
    }

  }

  def followersIds(
      userId: Option[String] = None,
      screenName: Option[String] = None,
      stringifyIds: Option[Boolean] = None)(implicit page: CursorPage = CursorPage(None, None)): Future[ResultSetWithCursor[UserId]] = {

    require(userId.nonEmpty || screenName.nonEmpty, "Either a screen_name or a user_id must be provided.")

    val params = Seq(
      userId map ("user_id" -> _),
      screenName map ("screen_name" -> _),
      page.cursor map ("cursor" -> _.toString),
      stringifyIds map ("stringify_ids" -> _.toString),
      page.count map ("count" -> _.toString)).flatten.toMap

    get(s"/$version/followers/ids.json", params) map { r =>
      val json = JsonParser(r.entity.asString)
      json.asJsObject.getFields("next_cursor", "ids") match {
        case Seq(JsNumber(nextCursor), JsArray(ids)) => {
          val followerIds = ids collect { case JsNumber(id) => UserId(id.toLong) }
          ResultSetWithCursor(followerIds, nextCursor.toLong)
        }
        case _ => throw new Exception("Failed parsing followers list")
      }
    }

  }

  def friendshipsIncoming(stringifyIds: Option[Boolean] = None)(implicit page: CursorPage = CursorPage(None, None)): Future[ResultSetWithCursor[UserId]] = {

    val params = Seq(
        page.cursor map ("cursor" -> _.toString),
        stringifyIds map ("stringify_ids" -> _.toString)).flatten.toMap

    get(s"/$version/friendships/incoming.json", params) map { r =>
      val json = JsonParser(r.entity.asString)
      json.asJsObject.getFields("next_cursor", "ids") match {
        case Seq(JsNumber(nextCursor), JsArray(ids)) => {
          val friendIds = ids collect { case JsNumber(id) => UserId(id.toLong) }
          ResultSetWithCursor(friendIds, nextCursor.toLong)
        }
        case _ => throw new Exception("Failed parsing incoming friendships list")
      }
    }
  }

  def friendshipsOutgoing(stringifyIds: Option[Boolean] = None)(implicit page: CursorPage = CursorPage(None, None)): Future[ResultSetWithCursor[UserId]] = {

    val params = Seq(
      page.cursor map ("cursor" -> _.toString),
      stringifyIds map ("stringify_ids" -> _.toString)).flatten.toMap

    get(s"/$version/friendships/outgoing.json", params) map { r =>
      val json = JsonParser(r.entity.asString)
      json.asJsObject.getFields("next_cursor", "ids") match {
        case Seq(JsNumber(nextCursor), JsArray(ids)) => {
          val friendIds = ids collect { case JsNumber(id) => UserId(id.toLong) }
          ResultSetWithCursor(friendIds, nextCursor.toLong)
        }
        case _ => throw new Exception("Failed parsing outgoing friendships list")
      }
    }
  }

  def friendshipsCreate(userId: Option[String], screenName: Option[String] = None, follow: Option[Boolean] = None) = {

    require(userId.nonEmpty || screenName.nonEmpty, "Either a screen_name or a user_id must be provided.")

    val params = Seq(
      userId map ("user_id" -> _),
      screenName map ("screen_name" -> _),
      follow map ("follow" -> _.toString)).flatten.toMap

    val content = params map { case (k, v) => s"$k=$v" } mkString "&"

    post(s"/$version/friendships/create.json", Map(), Some(content)) map { r =>
      val json = JsonParser(r.entity.asString)
      json.asJsObject.convertTo[User]
    }

  }

  def friendshipsDestroy(userId: Option[String], screenName: Option[String] = None) = {

    require(userId.nonEmpty || screenName.nonEmpty, "Either a screen_name or a user_id must be provided.")

    val params = Seq(
      userId map ("user_id" -> _),
      screenName map ("screen_name" -> _)).flatten.toMap

    val content = params map { case (k, v) => s"$k=$v" } mkString "&"

    post(s"/$version/friendships/destroy.json", Map(), Some(content)) map { r =>
      val json = JsonParser(r.entity.asString)
      json.asJsObject.convertTo[User]
    }
  }

  def friendshipsUpdate(
      userId: Option[String],
      screenName: Option[String] = None,
      device: Option[Boolean] = None,
      retweets: Option[Boolean] = None) = {

    require(userId.nonEmpty || screenName.nonEmpty, "Either a screen_name or a user_id must be provided.")

    val params = Seq(
      userId map ("user_id" -> _),
      screenName map ("screen_name" -> _),
      device map ("device" -> _.toString),
      retweets map ("retweets" -> _.toString)).flatten.toMap

    val content = params map { case (k, v) => s"$k=$v" } mkString "&"

    post(s"/$version/friendships/update.json", Map(), Some(content)) map { r =>
      JsonParser(r.entity.asString)
    }

  }

  def friendshipsShow(
      sourceId: Option[String],
      sourceScreenName: Option[String] = None,
      targetId: Option[String],
      targetScreeName: Option[String] = None) = {

    require((sourceId.nonEmpty || sourceScreenName.nonEmpty) && (targetId.nonEmpty || targetScreeName.nonEmpty),
      "At least one source and one target, whether specified by IDs or screen_names, should be provided to this method")

    val params = Seq(
      sourceId map ("source_id" -> _),
      sourceScreenName map ("source_screen_name" -> _),
      targetId map ("target_id" -> _),
      targetScreeName map ("target_screen_name" -> _)).flatten.toMap

    get(s"/$version/friendships/show.json", params) map { r =>
      JsonParser(r.entity.asString)
    }
  }

  def friendsList(
      userId: Option[String],
      screenName: Option[String] = None,
      skipStatus: Option[Boolean] = None,
      includeUserEntities: Option[Boolean] = None)(implicit page: CursorPage = CursorPage(None, None)): Future[ResultSetWithCursor[User]] = {

    require(userId.nonEmpty || screenName.nonEmpty, "Either a screen_name or a user_id must be provided.")

    val params = Seq(
      userId map ("user_id" -> _),
      screenName map ("screen_name" -> _),
      page.cursor map ("cursor" -> _.toString),
      page.count map ("count" -> _.toString),
      skipStatus map ("skip_status" -> _.toString),
      includeUserEntities map ("include_user_entities" -> _.toString)).flatten.toMap

      get(s"/$version/friends/list.json", params) map { r =>
        val json = JsonParser(r.entity.asString)
        json.asJsObject.getFields("next_cursor", "users") match {
          case Seq(JsNumber(nextCursor), users) => ResultSetWithCursor(users.convertTo[Seq[User]], nextCursor.toLong)
          case _ => throw new Exception("Failed parsing friends list")
        }
      }
  }

  def followersList(
       userId: Option[String],
       screenName: Option[String] = None,
       skipStatus: Option[Boolean] = None,
       includeUserEntities: Option[Boolean] = None)(implicit page: CursorPage = CursorPage(None, None)): Future[ResultSetWithCursor[User]] = {

    require(userId.nonEmpty || screenName.nonEmpty, "Either a screen_name or a user_id must be provided.")


    val params = Seq(
      userId map ("user_id" -> _),
      screenName map ("screen_name" -> _),
      page.cursor map ("curosr" -> _.toString),
      page.count map ("count" -> _.toString),
      skipStatus map ("skip_status" -> _.toString),
      includeUserEntities map ("include_user_entities" -> _.toString)).flatten.toMap

    get(s"/$version/followers/list.json", params) map { r =>
      val json = JsonParser(r.entity.asString)
      json.asJsObject.getFields("next_cursor", "users") match {
        case Seq(JsNumber(nextCursor), users) => ResultSetWithCursor(users.convertTo[Seq[User]], nextCursor.toLong)
        case _ => throw new Exception("Failed parsing followers list")
      }
    }
  }

  def friendshipsLookup(userIds: Seq[String], screenNames: Seq[String] = Seq()) = {

    val params = Seq(
      "user_id" -> userIds.mkString(","),
      "screen_name" -> screenNames.mkString(",")).toMap

    get(s"/$version/friendships/lookup.json", params) map { r =>
      JsonParser(r.entity.asString)
    }
  }

}
