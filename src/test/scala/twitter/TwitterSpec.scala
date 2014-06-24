package twitter

import org.scalatest.FlatSpec
import twitter.conf.TwitterConfiguration
import scala.concurrent.Await
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

class TwitterSpec extends FlatSpec {

  "Twitter" should "get a user timeline" in {
    val twitterConfig = new TwitterConfiguration(
        "VBL5utOTgBNKc6tCOQEBZjxLf",
        "6fHodhXT9TbCdkFQTtgtLC6EGTiS6ubbuaewxK5Ojl7jAm1bnF",
        "386499416-JpWsuKp4JfNVGtLwZpINcEpdYIWwB8sJdRpRmxHB",
        "IBdQXGAXRLEaw6gyrqRt6CZv6STBUSVhgzcOpZtks4ndL")
    val twitter = new Twitter(twitterConfig)
    val status = Await.result(twitter.getUser, Duration(5, TimeUnit.SECONDS))
    println(status)
  }

}
