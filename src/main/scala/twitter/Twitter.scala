package twitter

import twitter.conf.TwitterConfiguration
import twitter.api.TimelinesResources
import spray.http._
import spray.client.pipelining._
import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import auth.OAuth
import akka.util.Timeout
import java.util.concurrent.TimeUnit


class Twitter(config: TwitterConfiguration) {

  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures
  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  private[this] val pipeline = for (
    Http.HostConnectorInfo(connector, _) <-
    IO(Http) ? Http.HostConnectorSetup("api.twitter.com", port = 443, sslEncryption = true)
  ) yield (
      OAuth.oAuthAuthorizer(config)
      ~> sendReceive(connector)
  )

  def getUser = {
    pipeline.flatMap(_(Get("/1.1/followers/ids.json"))) map { response =>
      response.status
    }
  }

}
