package org.reactivebird.oauth

import javax.crypto
import java.nio.charset.Charset
import org.parboiled.common.Base64

object HmacSha1Signature {

  private val algorithm = "HmacSHA1"

  def apply(signatureBase: String, signatureKey: String): String = {
    val key = new crypto.spec.SecretKeySpec(bytes(signatureKey), algorithm)
    val mac =  crypto.Mac.getInstance(algorithm)
    mac.init(key)
    val sig = Base64.rfc2045().encodeToString(mac.doFinal(bytes(signatureBase)), false)
    mac.reset()
    sig
  }

  private def bytes(str: String) = str.getBytes(Charset.forName("UTF-8"))
}
