package org.reactivebird.oauth

import org.reactivebird.Token


object RequestToken {

  private val requestTokenR = "oauth_token=(.+)&oauth_token_secret=(.+)&oauth_callback_confirmed=(.+)".r

  def fromResponseBody(body: String): RequestToken = {
    body match {
      case requestTokenR(oauthToken, oauthTokenSecret, oauthCallbackConfirmed) =>
        RequestToken(Token(oauthToken, Some(oauthTokenSecret)), oauthCallbackConfirmed.toBoolean)
      case _ => throw new Exception("Error while parsing request token")
    }
  }
}

case class RequestToken(token: Token, oauthCallbackConfirmed: Boolean)


object AccessToken {

  private val accessTokenR = "oauth_token=(.+)&oauth_token_secret=(.+)&user_id=(.+)&screen_name=(.+)".r

  def fromResponseBody(body: String): AccessToken = {
    body match {
      case accessTokenR(oauthToken, oauthTokenSecret, userId, screenName) =>
        AccessToken(Token(oauthToken, Some(oauthTokenSecret)), userId, screenName)
      case _ => throw new Exception("Error while parsing request token")

    }
  }

}

case class AccessToken(token: Token, userId: String, screenName: String)
