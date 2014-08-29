package org.reactivebird.http

import spray.client.pipelining._
import scala.concurrent.ExecutionContext
import spray.http.HttpRequest


object Authorizing {

  def withAuthorizer(pipeline: SendReceive, authorizer: RequestTransformer): SendReceive = {
    request: HttpRequest => {
      pipeline(authorizer(request))
    }
  }
}

trait Authorizing extends HttpService {

  implicit val exec: ExecutionContext

  import Authorizing._

  protected def authorizer: RequestTransformer

  override protected def getPipeline = {
    super.getPipeline map { p =>
      withAuthorizer(p, authorizer)
    }
  }

}
