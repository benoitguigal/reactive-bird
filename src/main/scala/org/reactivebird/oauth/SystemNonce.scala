package org.reactivebird.oauth


object SystemNonce {
  def apply() = System.nanoTime.toString
}
