package me.benoitguigal.twitter.oauth


object SystemNonce {
  def apply() = System.nanoTime.toString
}
