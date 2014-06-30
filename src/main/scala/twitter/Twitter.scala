package twitter

import spray.http._
import spray.client.pipelining._
import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import twitter.oauth.{Token, Consumer, OAuth}
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import spray.http.Uri.Query
import scala.concurrent.Future
import twitter.api.{ApiOauth, Timeline}
import TwitterError.errorFilter
import twitter.wrappers.{WrapperTypes, DefaultWrapperTypes}


object Twitter {

  def apply(consumer: Consumer) = new Twitter(consumer) with DefaultWrapperTypes
}

abstract class Twitter(val consumer: Consumer) extends WrapperTypes with Timeline with ApiOauth {

  private[this] implicit val system = ActorSystem()
  implicit val exec = system.dispatcher // execution context for futures
  private[this] implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  val requestLogger: (HttpRequest => HttpRequest) = r => { println(r); r }

  private[this] lazy val pipeline = for (
    Http.HostConnectorInfo(connector, _) <-
    IO(Http) ? Http.HostConnectorSetup(host, port = 443, sslEncryption = true)
  ) yield (
      requestLogger
      ~> sendReceive(connector)
      ~> errorFilter
  )


  def get(token: Token, path: String, params: Map[String, String]): Future[HttpResponse] = {
    val uri = Uri.from(path = path, query = Query(params))
    val authorizer = OAuth.oAuthAuthorizer(consumer, Some(token))
    val request = authorizer(Get(uri))
    pipeline.flatMap(_(request))
  }

  def post(token: Token, path: String, params: Map[String, String]): Future[HttpResponse] = {
    val uri = Uri.from(path = path, query = Query(params))
    val authorizer = OAuth.oAuthAuthorizer(consumer, Some(token))
    val request = authorizer(Post(uri))
    pipeline.flatMap(_(request))
  }

  def get(path: String, params: Map[String, String]): Future[HttpResponse] = {
    val uri = Uri.from(path = path, query = Query(params))
    val authorizer = OAuth.oAuthAuthorizer(consumer, None)
    val request = authorizer(Get(uri))
    pipeline.flatMap(_(request))
  }

  def post(path: String, params: Map[String, String]): Future[HttpResponse] = {
    val uri = Uri.from(path = path, query = Query(params))
    val authorizer = OAuth.oAuthAuthorizer(consumer, None)
    val request = authorizer(Post(uri))
    pipeline.flatMap(_(request))
  }

}
