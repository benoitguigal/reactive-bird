package me.benoitguigal.twitter.oauth


object RequestToken {

  val requestTokenR = "oauth_token=(.+)&oauth_token_secret=(.+)&oauth_callback_confirmed=(.+)".r

  def fromResponseBody(body: String): RequestToken = {
    body match {
      case requestTokenR(oauthToken, oauthTokenSecret, oauthCallbackConfirmed) =>
        RequestToken(oauthToken, oauthTokenSecret, oauthCallbackConfirmed.toBoolean)
      case _ => throw new Exception("Error while parsing request token")
    }
  }
}

case class RequestToken(oauthToken: String, oauthTokenSecret: String, oauthCallbackConfirmed: Boolean)
