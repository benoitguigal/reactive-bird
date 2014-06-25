package twitter.auth

import javax.crypto
import java.nio.charset.Charset
import spray.http.HttpRequest
import spray.http.HttpHeaders.RawHeader
import org.parboiled.common.Base64
import scala.collection.immutable.TreeMap
import java.net.URLEncoder
import twitter.conf.TwitterConfiguration
import twitter.{scheme, host}


/**
 * taken from https://github.com/eigengo/activator-spray-twitter/edit/master/src/main/scala/core/OAuth.scala
 */
object OAuth {

  def oAuthAuthorizer(config: TwitterConfiguration): HttpRequest ⇒ HttpRequest = {
    // construct the key and cryptographic entity
    val SHA1 = "HmacSHA1"
    val keyString = percentEncode(config.consumerSecret :: config.tokenSecret :: Nil)
    val key = new crypto.spec.SecretKeySpec(bytes(keyString), SHA1)
    val mac = crypto.Mac.getInstance(SHA1)

    { httpRequest: HttpRequest =>
      val timestamp = (System.currentTimeMillis / 1000).toString
      // nonce is unique enough for our purposes here
      val nonce = System.nanoTime.toString

      val requestParams = httpRequest.uri.query.toMap

      // prepare the OAuth parameters
      val oauthParams = Map(
        "oauth_consumer_key" -> config.consumerKey,
        "oauth_signature_method" -> "HMAC-SHA1",
        "oauth_timestamp" -> timestamp,
        "oauth_nonce" -> nonce,
        "oauth_token" -> config.token,
        "oauth_version" -> "1.0"
      )

      // construct parts of the signature base string
      val encodedOrderedParams = (TreeMap[String, String]() ++ oauthParams ++ requestParams) map { case (k, v) => k + "=" + v } mkString "&"
      val url = scheme + "://"+ host + httpRequest.uri.toString().split('?')(0)
      // construct the signature base string
      val signatureBaseString = percentEncode(httpRequest.method.toString() :: url :: encodedOrderedParams :: Nil)

      mac.init(key)
      val sig = Base64.rfc2045().encodeToString(mac.doFinal(bytes(signatureBaseString)), false)
      mac.reset()

      val oauth = TreeMap[String, String]() ++ (oauthParams + ("oauth_signature" -> percentEncode(sig))) map { case (k, v) => "%s=\"%s\"" format (k, v) } mkString ", "

      // return the signed request
      httpRequest.withHeaders(List(RawHeader("Authorization", "OAuth " + oauth))).withEntity(httpRequest.entity)
    }
  }

  private def percentEncode(str: String): String = URLEncoder.encode(str, "UTF-8") replace ("+", "%20") replace ("%7E", "~")
  private def percentEncode(s: Seq[String]): String = s map percentEncode mkString "&"
  private def bytes(str: String) = str.getBytes(Charset.forName("UTF-8"))

}
