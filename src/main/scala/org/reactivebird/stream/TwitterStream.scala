package org.reactivebird.stream

import akka.actor.{ActorLogging, Actor}
import org.reactivebird.{Consumer, Token, Akka}
import spray.can.Http
import akka.io.IO
import spray.http._
import spray.http.HttpRequest
import spray.client.pipelining.sendTo
import org.reactivebird.oauth.OAuth._


object TwitterStream {

  case class StatusesFilter(filterQuery: FilterQuery)

}

class TwitterStreamActor(consumer: Consumer, token: Token) extends Actor with ActorLogging {

  import TwitterStream._

  val a =  2  >> 1

  val io = IO(Http)(Akka.system)

  override def receive = {
    case StatusesFilter(query) =>
      val body = HttpEntity(ContentType(MediaTypes.`application/x-www-form-urlencoded`), s"track=${query.track}")
      val req = HttpRequest(HttpMethods.POST, uri = "https://stream.twitter.com/1.1/statuses/filter.json", entity = body)
      sendTo(io).withResponsesReceivedBy(self)(req.authorize(consumer, token))
    case MessageChunk(entity, _) => log.info(entity.asString)
    case _ =>
  }
}

class FilterQuery(val track: String)

