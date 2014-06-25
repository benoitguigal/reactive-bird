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


class Twitter(config: TwitterConfiguration) extends Timeline {

  private[this] implicit val system = ActorSystem()
  implicit val exec = system.dispatcher // execution context for futures
  private[this] implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  private[this] val pipeline = for (
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
