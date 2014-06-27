package twitter.oauth


object SystemTimestamp {

  def apply() = (System.currentTimeMillis / 1000).toString
}
