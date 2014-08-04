package org.reactivebird

case class Consumer(key: String, secret: String)

case class Token(key: String, secret: Option[String])
