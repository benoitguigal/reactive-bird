package twitter.models

import spray.json._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.Locale


object JsonFormats extends DefaultJsonProtocol {


  implicit object CoordinatesJsonFormat extends JsonFormat[Coordinates] {

    override def read(coordinates: JsValue) = coordinates match {
      case JsArray(Seq(JsNumber(longitude), JsNumber(latitude))) => Coordinates(longitude.toDouble, latitude.toDouble)
      case _ => throw new Exception
    }

    override def write(coordinates: Coordinates) = JsArray(JsNumber(coordinates.longitude), JsNumber(coordinates.latitude))
  }

  implicit object DateTimeJsonFormat extends JsonFormat[DateTime] {

    private[this] val dtf =  DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy").withLocale(Locale.ENGLISH)
    override def read(date: JsValue) = date match {
      case JsString(d) => dtf.parseDateTime(d)
      case _ => throw new Exception
    }

    override def write(date: DateTime) = JsString(dtf.print(date))
  }


  implicit val urlFormat = jsonFormat4(URL)

  implicit val userMentionFormat = jsonFormat5(UserMention)

  implicit val hashtagFormat = jsonFormat2(Hashtag)

  implicit val mediaFormat = jsonFormat7(Media)

  implicit val entitiesFormat = jsonFormat4(Entities)

  implicit val userFormat = jsonFormat8(User)

  implicit val statusFormat = jsonFormat11(Status)

  implicit val placeFormat = jsonFormat7(Place)

}






