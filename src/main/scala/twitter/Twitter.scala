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
import scala.concurrent.Future
import twitter.api.Timeline
import TwitterError.errorFilter
import twitter.wrappers.{WrapperTypes, DefaultWrapperTypes}


object Twitter {

  def apply(config: TwitterConfiguration) = new Twitter(config) with DefaultWrapperTypes
}

abstract class Twitter(val config: TwitterConfiguration) extends WrapperTypes with Timeline {

  private[this] implicit val system = ActorSystem()
  implicit val exec = system.dispatcher // execution context for futures
  private[this] implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  private[this] lazy val pipeline = for (
    Http.HostConnectorInfo(connector, _) <-
    IO(Http) ? Http.HostConnectorSetup(host, port = 443, sslEncryption = true)
  ) yield (
      OAuth.oAuthAuthorizer(config)
      ~> sendReceive(connector)
      ~> errorFilter
  )


  def get(path: String, params: Map[String, String]): Future[HttpResponse] = {
    val uri = Uri.from(path = path, query = Query(params))
    pipeline.flatMap(_(Get(uri)))
  }


}
