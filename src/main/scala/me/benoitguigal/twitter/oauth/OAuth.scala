package me.benoitguigal.twitter.oauth


import scala.collection.immutable.TreeMap
import me.benoitguigal.twitter._
import spray.http.HttpRequest
import me.benoitguigal.twitter.Consumer
import spray.http.HttpHeaders.RawHeader


object OAuth {

  implicit class CanBeAuthorizedHttpRequest(request: HttpRequest) {

    def authorize(consumer: Consumer, token: Token): HttpRequest = {
      val timestamp = SystemTimestamp()
      val nonce = SystemNonce()

      val requestUriParams = request.uri.query.toMap

      val requestBodyParams = {
        val bodyParameterR = "(.+)=(.+)".r
        (request.entity.asString.split("&") collect {
          case bodyParameterR(key, value) => (key -> value)
        }).toMap
      }

      val oauthParams = Map(
        "oauth_consumer_key" -> consumer.key,
        "oauth_signature_method" -> "HMAC-SHA1",
        "oauth_timestamp" -> timestamp,
        "oauth_nonce" -> nonce,
        "oauth_version" -> "1.0",
        "oauth_token" -> token.key)

      val encodedOrderedParams = {
        val sortedParams = TreeMap.newBuilder[String, String]
        sortedParams ++= requestUriParams map { case (k, v) => %%(k) -> %%(v) }
        sortedParams ++= requestBodyParams map { case (k, v) => %%(k) -> v }
        sortedParams ++= oauthParams map { case (k, v) => %%(k) -> %%(v) }
        sortedParams.result map { case (k, v) => s"$k=$v" } mkString "&"
      }

      val url = scheme + "://" + host + request.uri.path.toString()

      val signatureBase = %%(request.method.value) + "&" +  %%(url) + "&" + %%(encodedOrderedParams)
      val signatureKey = %%(consumer.secret) + "&" + token.secret.map(%%(_)).getOrElse("")
      val signature = HmacSha1Signature(signatureBase, signatureKey)

      val oauthBld = TreeMap.newBuilder[String, String]
      oauthBld ++= oauthParams
      oauthBld += ("oauth_signature" -> %%(signature))
      requestUriParams.get("oauth_callback").foreach { callback => oauthBld += (%%("oauth_callback") -> callback) }
      val oauth = oauthBld.result() map { case (k, v) => k + "=" + "\"" + v + "\"" } mkString ", "

      request.withHeaders(List(RawHeader("Authorization", "OAuth " + oauth)))
    }

    def authorize(consumer: Consumer, oauthCallback: String): HttpRequest = {

      val timestamp = SystemTimestamp()
      val nonce = SystemNonce()

      val oauthParams = Map(
        "oauth_consumer_key" -> consumer.key,
        "oauth_signature_method" -> "HMAC-SHA1",
        "oauth_timestamp" -> timestamp,
        "oauth_nonce" -> nonce,
        "oauth_callback" -> oauthCallback)

      val encodedOrderedParams = {
        val sortedParams = TreeMap.newBuilder[String, String]
        sortedParams ++= oauthParams map { case (k, v) => %%(k) -> %%(v) }
        sortedParams.result map { case (k, v) => s"$k=$v" } mkString "&"
      }

      val url = scheme + "://" + host + request.uri.path.toString()

      val signatureBase = %%(request.method.value) + "&" +  %%(url) + "&" + %%(encodedOrderedParams)
      val signatureKey = %%(consumer.secret) + "&"
      val signature = HmacSha1Signature(signatureBase, signatureKey)

      val oauthBld = TreeMap.newBuilder[String, String]
      oauthBld ++= oauthParams
      oauthBld += ("oauth_signature" -> %%(signature))
      val oauth = oauthBld.result() map { case (k, v) => k + "=" + "\"" + v + "\"" } mkString ", "

      // return the signed request
      request.withHeaders(List(RawHeader("Authorization", "OAuth " + oauth)))
    }

  }

}
