package twitter

import org.scalatest.FlatSpec
import scala.concurrent.Await
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import twitter.oauth.{Token, Consumer}


class TwitterSpec extends FlatSpec {

/*  val consumer = Consumer(
    "cChZNFj6T5R0TigYB9yd1w",
    "L8qq9PZyRg6ieKGEKhZolGC0vJWLw8iEJ88DRdyOg"
  )*/

  val consumer = Consumer(
    "VBL5utOTgBNKc6tCOQEBZjxLf",
    "6fHodhXT9TbCdkFQTtgtLC6EGTiS6ubbuaewxK5Ojl7jAm1bnF")

  "Twitter" should "get a user mentions" in {
    pending
    val token = Token("386499416-JpWsuKp4JfNVGtLwZpINcEpdYIWwB8sJdRpRmxHB", "IBdQXGAXRLEaw6gyrqRt6CZv6STBUSVhgzcOpZtks4ndL")
    val twitter = Twitter(consumer)
    val mentions = Await.result(twitter.mentionsTimeline(token = token), Duration(5, TimeUnit.SECONDS))
    println(mentions)
  }

  "Twitter" should "get requestToken" in {
    val twitter = Twitter(consumer)
    val token = Await.result(twitter.requestToken("http%3A%2F%2Fbenoitguigal.me%2Foauth%2Fcallback"), Duration(5, TimeUnit.SECONDS))
    println(token)
  }



}
