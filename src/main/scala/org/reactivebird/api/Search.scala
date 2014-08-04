package org.reactivebird.api

import org.reactivebird.{Akka, TwitterApi, version}
import org.joda.time.DateTime
import spray.json.JsonParser


trait Search {
  self: TwitterApi =>

  import Akka.exec

  def search(
      q: String,
      geocode: Option[String] = None,
      lang: Option[String] = None,
      locale: Option[String] = None,
      until: Option[DateTime] = None,
      resultType: Option[String] = None,
      includeEntities: Option[Boolean] = None)(implicit page: MaxIdPage = MaxIdPage(None, None, None)) = {

    val params = Seq(
      Some("q" -> q),
      geocode map ("geocode" -> _),
      lang map ("lang" -> _),
      locale map ("locale" -> _),
      resultType map ("result_type" -> _),
      includeEntities map ("include_entities" -> _.toString),
      page.count map ("count" -> _.toString),
      page.sinceId map ("since_id" -> _),
      page.maxId map ("max_id" -> _)).flatten.toMap

    get(s"/$version/search/tweets.json", params) map { r =>
      JsonParser(r.entity.toString).convertTo[Seq[Status]]
    }

  }

}
