package twitter

import org.scalatest.FlatSpec
import twitter.conf.TwitterConfiguration
import scala.concurrent.Await
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import twitter.models.MyJsonProtocol.statusFormat

class TwitterSpec extends FlatSpec {



  "Twitter" should "get a user mentions" in {
    pending
    val twitterConfig = new TwitterConfiguration(
      "VBL5utOTgBNKc6tCOQEBZjxLf",
      "6fHodhXT9TbCdkFQTtgtLC6EGTiS6ubbuaewxK5Ojl7jAm1bnF",
      "386499416-JpWsuKp4JfNVGtLwZpINcEpdYIWwB8sJdRpRmxHB",
      "IBdQXGAXRLEaw6gyrqRt6CZv6STBUSVhgzcOpZtks4ndL")
    val twitter = new Twitter(twitterConfig)
    val mentions = Await.result(twitter.mentionsTimeline(), Duration(5, TimeUnit.SECONDS))
    println(mentions)
  }



}
