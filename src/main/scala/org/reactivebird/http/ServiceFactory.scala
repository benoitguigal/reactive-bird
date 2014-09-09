package org.reactivebird.http

import spray.can.Http
import spray.client.pipelining._
import scala.concurrent.Future
import spray.can.Http.HostConnectorSetup
import akka.io.IO
import org.reactivebird._
import akka.pattern.ask
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import akka.actor.ActorSystem

trait ServiceFactory extends (HostConnectorSetup => Future[SendReceive]) {

  implicit val system: ActorSystem
  implicit private val timeout = Timeout(60, TimeUnit.SECONDS)

  def Service(connexion: HostConnectorSetup) = {
    for (
      Http.HostConnectorInfo(connector, _) <-
      IO(Http) ? Http.HostConnectorSetup(host, port = 443, sslEncryption = true)
    ) yield (sendReceive(connector))
  }
}

