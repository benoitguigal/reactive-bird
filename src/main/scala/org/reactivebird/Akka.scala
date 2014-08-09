package org.reactivebird

import akka.actor.ActorSystem
import akka.util.Timeout
import java.util.concurrent.TimeUnit


object Akka {

  implicit val system = ActorSystem()
  implicit val exec = system.dispatcher // execution context for futures
  implicit val timeout = Timeout(60, TimeUnit.SECONDS)

}
