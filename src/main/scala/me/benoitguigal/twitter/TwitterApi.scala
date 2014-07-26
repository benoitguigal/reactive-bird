package me.benoitguigal.twitter

import me.benoitguigal.twitter.http.{Authorizer, HttpService}
import akka.actor.ActorSystem
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import me.benoitguigal.twitter.api._
import me.benoitguigal.twitter.models.ModelFactory

object TwitterApi {

  implicit val system = ActorSystem()
  implicit val exec = system.dispatcher // execution context for futures
  implicit val timeout = Timeout(10, TimeUnit.SECONDS)

/*  def apply(_consumer: Consumer, _token: Token) = new TwitterApi with DefaultWrapperTypes {
    val consumer: Consumer = _consumer
    val token: Token = _token
  }*/

}

trait TwitterApi
  extends HttpService
  with ModelFactory
  with Timeline
  with Tweets
  with FriendsAndFollowers
  with Users {

  val consumer: Consumer
  val token: Token

  override def authorizer = Authorizer(consumer, token)
}

