package org.reactivebird.oauth


object SystemTimestamp {

  def apply() = (System.currentTimeMillis / 1000).toString
}
