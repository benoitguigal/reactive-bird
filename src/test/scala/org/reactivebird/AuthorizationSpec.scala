package org.reactivebird

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import spray.http._
import spray.http.HttpRequest
import org.reactivebird.oauth.OAuth.CanBeAuthorizedHttpRequest


class AuthorizationSpec extends FlatSpec with Matchers with MockitoSugar {

  it should "add authorization header" in {

    /*
     * request constructed after Twitter documentation example
     * https://dev.twitter.com/docs/auth/authorizing-request
     */

    val request = HttpRequest(
        HttpMethods.POST,
        Uri("/1/statuses/update.json?include_entities=true"),
        List(),
        HttpEntity("status=Hello%20Ladies%20%2b%20Gentlemen%2c%20a%20signed%20OAuth%20request%21"))

    val richRequest = new CanBeAuthorizedHttpRequest(request) {
      override def nonceProvider = "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg"
      override def timestampProvider = "1318622958"
    }
    val consumer = Consumer("xvz1evFS4wEEPTGEFPHBog", "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw")
    val token = Token("370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb", Some("LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE"))
    val authorized = richRequest.authorize(consumer, token)
    val authorization = authorized.headers.find(_.name == "Authorization").get
    authorization.value should equal(
      "OAuth oauth_consumer_key=\"xvz1evFS4wEEPTGEFPHBog\", oauth_nonce=\"kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg\", oauth_signature=\"sdG6iRMesiYxCrqH8iCtx05eqX0%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1318622958\", oauth_token=\"370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb\", oauth_version=\"1.0\"")
  }

}
