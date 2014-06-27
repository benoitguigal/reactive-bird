package twitter.api

import twitter.Twitter
import scala.concurrent.Future


trait OAuth {
  self: Twitter =>


  val requestTokenR = "oauth_token=(.+)&=oauth_token=(.+)".r
  case class RequestToken(oauthToken: String, oauthTokenSecret: String)

  def requestToken(callbackUrl: String): Future[RequestToken] = {

    post("/oauth/request_token", Map("callback_url" -> callbackUrl)) map { response =>
      response.entity.asString match {
        case requestTokenR(token, secret) => RequestToken(token, secret)
        case _ => throw new Exception("Failed parsing request token")
      }

    }
  }
}
