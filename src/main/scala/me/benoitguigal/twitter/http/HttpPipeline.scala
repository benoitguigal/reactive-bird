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
import spray.http.HttpRequest
import me.benoitguigal.twitter.oauth.Consumer
import me.benoitguigal.twitter.oauth.Token

object HttpPipeline {

  import TwitterApi.{system, exec, timeout}

  lazy private val sendReceiveFut = for (
    Http.HostConnectorInfo(connector, _) <-
    IO(Http) ? Http.HostConnectorSetup(host, port = 443, sslEncryption = true)
  ) yield (sendReceive(connector))

  def apply(consumer: Consumer, token: Token): Future[SendReceive] = sendReceiveFut map { sendReceive =>
    { request: HttpRequest => request.authorize(consumer, token) } ~> sendReceive ~> errorFilter
  }

  def apply(consumer: Consumer, oauthCallback: String) = sendReceiveFut map { sendReceive =>
    { request: HttpRequest => request.authorize(consumer, oauthCallback) } ~> sendReceive ~> errorFilter
  }

}
