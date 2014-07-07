package me.benoitguigal.twitter.oauth


object RequestToken {

  private val requestTokenR = "oauth_token=(.+)&oauth_token_secret=(.+)&oauth_callback_confirmed=(.+)".r

  def fromResponseBody(body: String): RequestToken = {
    body match {
      case requestTokenR(oauthToken, oauthTokenSecret, oauthCallbackConfirmed) =>
        RequestToken(oauthToken, oauthTokenSecret, oauthCallbackConfirmed.toBoolean)
      case _ => throw new Exception("Error while parsing request token")
    }
  }
}

case class RequestToken(oauthToken: String, oauthTokenSecret: String, oauthCallbackConfirmed: Boolean)


object AccessToken {

  private val accessTokenR = "oauth_token=(.+)&oauth_token_secret=(.+)&user_id=(.+)&screen_name=(.+)".r

  def fromResponseBody(body: String): AccessToken = {
    body match {
      case accessTokenR(oauthToken, oauthTokenSecret, userId, screenName) =>
        AccessToken(oauthToken, oauthTokenSecret, userId, screenName)
      case _ => throw new Exception("Error while parsing request token")

    }
  }

}

case class AccessToken(oauthToken: String, oauthTokenSecret: String, userId: String, screenName: String)
