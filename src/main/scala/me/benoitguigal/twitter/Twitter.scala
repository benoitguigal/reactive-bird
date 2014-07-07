package me.benoitguigal.twitter

import akka.pattern.ask
import me.benoitguigal.twitter.oauth.Token
import me.benoitguigal.twitter.oauth.Consumer
import me.benoitguigal.twitter.http.{HttpPipeline, HttpService}
import akka.actor.{Props, ActorSystem, Actor}
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import me.benoitguigal.twitter.UserToken.{GetToken, SetToken}
import me.benoitguigal.twitter.wrappers.{DefaultWrapperTypes, WrapperTypes}
import me.benoitguigal.twitter.api.{Timeline, Oauth}


object TwitterApi {

  implicit val system = ActorSystem()
  implicit val exec = system.dispatcher // execution context for futures
  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  def apply(_consumer: Consumer, _oauthCallback: Option[String] = None) = new TwitterApi with DefaultWrapperTypes {
    override val oauthCallback: Option[String] = _oauthCallback
    override val consumer: Consumer = _consumer
  }

}

trait TwitterApi extends HttpService with WrapperTypes with Timeline with Oauth {

  import TwitterApi.{system, exec, timeout}

  val consumer: Consumer
  val oauthCallback: Option[String]
  val userToken = system.actorOf(Props[UserToken], "UserToken")

  override def pipeline = ask(userToken, GetToken).mapTo[Option[Token]] flatMap {
    case Some(token) => HttpPipeline(consumer, token)
    case None => HttpPipeline(consumer, oauthCallback.getOrElse(""))
  }

  def setToken(token: Token) = userToken ! SetToken(token)
}


object UserToken {
  case class SetToken(token: Token)
  case object GetToken
}

class UserToken extends Actor {

  var token: Option[Token] = None

  override def receive = {
    case SetToken(t: Token) => token = Some(t)
    case GetToken => sender ! token
  }


}