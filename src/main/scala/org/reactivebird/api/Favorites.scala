package org.reactivebird.api

import org.reactivebird.{Akka, TwitterApi, version}
import spray.json.JsonParser


trait Favorites {
  self: TwitterApi =>

  import Akka.exec

  def favoritesList(
      userId: Option[String] = None,
      screenName: Option[String] = None,
      includeEntities: Option[Boolean] = None)(implicit page: MaxIdPage = MaxIdPage(None, None, None)) = {

    val params = Seq(
      userId map ("user_id" -> _),
      screenName map ("screen_name" -> _),
      page.count map ("count" -> _.toString),
      page.maxId map ("max_id" -> _),
      page.sinceId map ("since_id" -> _),
      includeEntities map ("include_entities" -> _.toString)).flatten.toMap

    get(s"/$version/favorites/list.json", params) map { r =>
      JsonParser(r.entity.asString).convertTo[Seq[Status]]
    }

  }

  def favoritesDestroy(id: String, includeEntities: Option[Boolean] = None) = {

    val params = Seq(Some("id" -> id), includeEntities map ("include_entities" -> _.toString)).flatten.toMap

    val content = params map { case (k, v) => s"$k=$v" } mkString "&"

    post(s"/$version/favorites/destroy.json", Map(), Some(content)) map { r =>
      JsonParser(r.entity.asString).convertTo[Status]
    }
  }

  def favoritesCreate(id: String, includeEntities: Option[Boolean] = None) = {

    val params = Seq(Some("id" -> id), includeEntities map ("include_entities" -> _.toString)).flatten.toMap

    val content = params map { case (k, v) => s"$k=$v" } mkString "&"

    post(s"/$version/favorites/create.json", Map(), Some(content)) map { r =>
      JsonParser(r.entity.asString).convertTo[Status]
    }

  }


}
