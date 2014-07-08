package me.benoitguigal.twitter

import org.scalatest.FlatSpec
import scala.concurrent.Await
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import me.benoitguigal.twitter.oauth.{Token, Consumer}

class TwitterSpec extends FlatSpec {

  val consumer = Consumer(
    "VBL5utOTgBNKc6tCOQEBZjxLf",
    "secret")

  "Twitter" should "get a user mentions" in {
    val token = Token("386499416-JpWsuKp4JfNVGtLwZpINcEpdYIWwB8sJdRpRmxHB", Some("secret"))
    val twitter = TwitterApi(consumer)
    twitter.setToken(token)
    val mentions = Await.result(twitter.mentionsTimeline(), Duration(5, TimeUnit.SECONDS))
    println(mentions)
  }

  "Twitter" should "get requestToken" in {
    pending
    val twitter = TwitterApi(consumer)
    val token = Await.result(twitter.requestToken("http://benoitguigal.me/oauth/callback"), Duration(5, TimeUnit.SECONDS))
    println(token)
  }

  "Twitter" should "get access token" in {
    pending
    val twitter = TwitterApi(consumer)
    twitter.setToken(Token("emH9p0chQl2vF0e1fQpYTZnEFgWMWZShgW85omEwoA", None))
    val token = Await.result(twitter.accessToken("secret"), Duration(5, TimeUnit.SECONDS))
    println(token)
  }



}
