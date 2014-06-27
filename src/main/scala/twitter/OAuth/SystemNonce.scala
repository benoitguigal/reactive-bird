package twitter.oauth


object SystemNonce {
  def apply() = System.nanoTime.toString
}
