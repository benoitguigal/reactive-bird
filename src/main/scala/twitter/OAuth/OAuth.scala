package twitter.oauth

import spray.http.HttpRequest
import spray.http.HttpHeaders.RawHeader
import scala.collection.immutable.TreeMap
import java.net.URLEncoder
import twitter.{scheme, host}


/**
 * taken from https://github.com/eigengo/activator-spray-twitter/edit/master/src/main/scala/core/OAuth.scala
 */
object OAuth {

  def oAuthAuthorizer(consumer: Consumer, token: Option[Token]): HttpRequest ⇒ HttpRequest = {

    { httpRequest: HttpRequest =>

      val timestamp = SystemTimestamp()
      val nonce = SystemNonce()

      // val requestParams = httpRequest.uri.query.toMap
      val requestParams = Map("oauth_callback" -> "http%3A%2F%2Fbenoitguigal.me%2Foauth%2Fcallback")

      val oauthParams = {
        val params = Map.newBuilder[String, String]
        params += (
          "oauth_consumer_key" -> consumer.key,
          "oauth_signature_method" -> "HMAC-SHA1",
          "oauth_timestamp" -> timestamp,
          "oauth_nonce" -> nonce,
          "oauth_version" -> "1.0"
        )
        if (token.nonEmpty) { params += ("oauth_token" -> token.get.key) }
        params.result()
      }

      val encodedOrderedParams = {
        val sortedParams = TreeMap.newBuilder[String, String]
        sortedParams ++= requestParams map { case (k, v) => %%(k) -> v }
        sortedParams ++= oauthParams map { case (k, v) => %%(k) -> %%(v) }
        sortedParams.result map { case (k, v) => s"$k=$v" } mkString "&"
      }

      val url = scheme + "://" + host + httpRequest.uri.path.toString()

      val signatureBase = %%(httpRequest.method.value) + "&" +  %%(url) + "&" + %%(encodedOrderedParams)
      println("signature base is " + signatureBase)
      val signatureKey = %%(consumer.secret) + "&" + %%(token.map(_.secret).getOrElse(""))
      val signature = HmacSha1Signature(signatureBase, signatureKey)

      val oauthBld = TreeMap.newBuilder[String, String]
      oauthBld ++= oauthParams
      oauthBld += ("oauth_signature" -> %%(signature))
      requestParams.get("oauth_callback").foreach { callback => oauthBld += (%%("oauth_callback") -> callback) }
      val oauth = oauthBld.result() map { case (k, v) => k + "=" + "\"" + v + "\"" } mkString ", "

      // return the signed request
      httpRequest.withHeaders(List(RawHeader("Authorization", "OAuth " + oauth)))
    }
  }

  private def %%(str: String): String = URLEncoder.encode(str, "UTF-8") replace ("+", "%20") replace ("%7E", "~")

}
