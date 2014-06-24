package twitter.api

import twitter.Twitter
import spray.json.JsonFormat
import scala.concurrent.Future

trait Timeline {
  this: Twitter =>


  def mentionsTimeline[Status](
      count: Option[Int] = None,
      sinceId: Option[String] = None,
      maxId: Option[String] = None,
      trimUser: Option[Boolean] = None,
      contributorDetails: Option[Boolean] = None,
      includeEntities: Option[Boolean] = None)(implicit format: JsonFormat[Seq[Status]]): Future[Seq[Status]] = {

    val params = Seq(
        count map ("count" -> _.toString),
        sinceId map ("since_id" -> _),
        maxId map ("max_id" -> _),
        trimUser map ("trim_user" -> _.toString),
        contributorDetails map ("contributor_details" -> _.toString),
        includeEntities map ("include_entities" -> _.toString)
    ).flatten.toMap

    apiget("/1.1/statuses/mentions_timeline.json", params) map (_.convertTo[Seq[Status]])
  }

}
