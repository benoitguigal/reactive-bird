package org


import java.net.URLEncoder


package object reactivebird {

  val host = "api.twitter.com"
  val scheme = "https"
  val version = "1.1"

  def %%(str: String): String = URLEncoder.encode(str, "UTF-8") replace ("+", "%20") replace ("%7E", "~")

}
