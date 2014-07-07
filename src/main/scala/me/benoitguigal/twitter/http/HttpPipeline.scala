package me.benoitguigal.twitter.http


import akka.pattern.ask
import spray.can.Http
import akka.io.IO
import spray.client.pipelining._
import me.benoitguigal.twitter.oauth.{Consumer, Token}
import spray.http._
import me.benoitguigal.twitter.TwitterError.errorFilter
import me.benoitguigal.twitter.oauth.OAuth._
import me.benoitguigal.twitter.TwitterApi
import me.benoitguigal.twitter.host
import scala.concurrent.Future

object HttpPipeline {

  import TwitterApi.{system, exec, timeout}

  def apply(consumer: Consumer, token: Token): Future[SendReceive] = for (
    Http.HostConnectorInfo(connector, _) <-
    IO(Http) ? Http.HostConnectorSetup(host, port = 443, sslEncryption = true)
  ) yield (
      { request: HttpRequest => request.authorize(consumer, token) }
        ~> sendReceive(connector)
        ~> errorFilter)

  def apply(consumer: Consumer, oauthCallback: String): Future[SendReceive] = for (
    Http.HostConnectorInfo(connector, _) <-
    IO(Http) ? Http.HostConnectorSetup(host, port = 443, sslEncryption = true)
  ) yield (
      { request: HttpRequest => request.authorize(consumer, oauthCallback) }
        ~> sendReceive(connector)
        ~> errorFilter)

}
