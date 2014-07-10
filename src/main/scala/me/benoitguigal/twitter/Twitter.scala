package me.benoitguigal.twitter

import me.benoitguigal.twitter.oauth.Token
import me.benoitguigal.twitter.oauth.Consumer
import me.benoitguigal.twitter.http.{HttpPipeline, HttpService}
import akka.actor.ActorSystem
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import me.benoitguigal.twitter.wrappers.{DefaultWrapperTypes, WrapperTypes}
import me.benoitguigal.twitter.api.{Tweets, Timeline, Oauth}


object TwitterApi {

  implicit val system = ActorSystem()
  implicit val exec = system.dispatcher // execution context for futures
  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  def apply(_consumer: Consumer, _oauthCallback: Option[String] = None) = new TwitterApi with DefaultWrapperTypes {
    override val oauthCallback: Option[String] = _oauthCallback
    override val consumer: Consumer = _consumer
  }

}

trait TwitterApi extends HttpService with WrapperTypes with Timeline with Tweets with Oauth {

  val consumer: Consumer
  val oauthCallback: Option[String]
  var token: Option[Token] = None

  override def pipeline = token match {
    case Some(token) => HttpPipeline(consumer, token)
    case None => HttpPipeline(consumer, oauthCallback.getOrElse(""))
  }

  def setToken(_token: Token) = { token = Some(_token) }

}

