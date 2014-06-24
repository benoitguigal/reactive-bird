package twitter

import twitter.conf.TwitterConfiguration
import spray.http._
import spray.client.pipelining._
import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import auth.OAuth
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import spray.http.Uri.Query
import spray.json.{JsValue, JsonParser}
import scala.concurrent.Future
import twitter.api.Timeline


class Twitter(config: TwitterConfiguration) extends Timeline {

  private[this] implicit val system = ActorSystem()
  implicit val exec = system.dispatcher // execution context for futures
  private[this] implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  private[this] def toJson: (HttpResponse => JsValue) = (r => JsonParser(r.entity.asString))

  private[this] val pipeline = for (
    Http.HostConnectorInfo(connector, _) <-
    IO(Http) ? Http.HostConnectorSetup("api.twitter.com", port = 443, sslEncryption = true)
  ) yield (
      OAuth.oAuthAuthorizer(config)
      ~> sendReceive(connector)
      ~> toJson
  )

  def apiget(path: String, params: Map[String, String]): Future[JsValue] = {
    val uri = Uri.from(path = path, query = Query(params))
    pipeline.flatMap(_(Get(uri)))
  }


}
