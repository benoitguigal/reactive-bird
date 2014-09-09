package org.reactivebird.http


import spray.client.pipelining._
import scala.concurrent.{ExecutionContext, Future}
import spray.http.{HttpResponse, HttpRequest}


object ServiceFilter {

  def RequestIdentity(request: HttpRequest) = request
  def ResponseIdentity(response: HttpResponse) = response

  def apply(
      requestTransformer: RequestTransformer,
      responseTransformer: ResponseTransformer)(_ec: ExecutionContext): ServiceFilter = {

    new ServiceFilter {
      implicit val ec = _ec
      def apply(request: HttpRequest, service: SendReceive) =  {
        service(requestTransformer(request)) map { responseTransformer(_) }
      }
    }
  }

}

trait ServiceFilter { self =>

  implicit val ec: ExecutionContext

  def apply(request: HttpRequest, service: SendReceive): Future[HttpResponse]

  def andThen(service: SendReceive): SendReceive = {
    request => apply(request, service)
  }

  def andThen(filter: ServiceFilter): ServiceFilter = {
    new ServiceFilter {
      implicit val ec = self.ec
      def apply(request: HttpRequest, pipeline: SendReceive) = {
        self.apply(request, filter andThen pipeline)
      }
    }
  }

}
