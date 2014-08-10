package org.reactivebird.stream

import org.scalatest.{Matchers, FlatSpec}
import org.reactivebird.{Token, Consumer, Akka}
import akka.actor.Props


class TwitterStreamActorSpec extends FlatSpec with Matchers {

  "TwitterStream" should "filter message and stream" in {


    val consumer = Consumer("REIUd9YchQhFNeqg38KnWaLeb", "QS8nwZxVw1Ke1vELwPj0nWsIB9qmpj36NdZMDFjDWD5Rjpvfhv")

    val token = Token("386499416-4ThmFDf5bZU1f1j5qivW0l3nEg6H5FGopfBczIcl", Some("lKVyOPQkLKlwZ0bcWRu9Ff2q45NnNHsCSG8mI7W2avM9n"))
    val stream = Akka.system.actorOf(Props(new TwitterStreamActor(consumer, token)))
    import TwitterStream._
    stream ! StatusesFilter(new FilterQuery("twitter"))
    Thread.sleep(60000)
  }

}
