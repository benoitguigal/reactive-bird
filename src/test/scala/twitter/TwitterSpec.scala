package twitter

import org.scalatest.FlatSpec
import scala.concurrent.Await
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import twitter.oauth.{Token, Consumer}


class TwitterSpec extends FlatSpec {

  val consumer = Consumer(
    "VBL5utOTgBNKc6tCOQEBZjxLf",
    "6fHodhXT9TbCdkFQTtgtLC6EGTiS6ubbuaewxK5Ojl7jAm1bnF")

  "Twitter" should "get a user mentions" in {
    val token = Token("386499416-JpWsuKp4JfNVGtLwZpINcEpdYIWwB8sJdRpRmxHB", "IBdQXGAXRLEaw6gyrqRt6CZv6STBUSVhgzcOpZtks4ndL")
    val twitter = Twitter(consumer)
    val mentions = Await.result(twitter.mentionsTimeline(token = token), Duration(5, TimeUnit.SECONDS))
    println(mentions)
  }



}
