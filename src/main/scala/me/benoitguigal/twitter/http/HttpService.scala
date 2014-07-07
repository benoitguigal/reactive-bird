package me.benoitguigal.twitter.http


import spray.client.pipelining._
import scala.concurrent.Future
import spray.http._
import spray.http.Uri.Query
import spray.http.HttpResponse


trait HttpService {

  import me.benoitguigal.twitter.TwitterApi.exec

  def pipeline: Future[SendReceive]

  def get(path: String, params: Map[String, String]): Future[HttpResponse] = {
    val uri = Uri.from(path = path, query = Query(params))
    val request = Get(uri)
    pipeline.flatMap(_(request))
  }

  def post(path: String, params: Map[String, String]): Future[HttpResponse] = {
    val uri = Uri.from(path = path, query = Query(params))
    val request = {
      val baseRequest = Post(uri)
      if (params.nonEmpty) {
        val urlEncodedParams = params map { case (k, v) => k + "=" + v } mkString "&"
        val r = baseRequest.withHeadersAndEntity(
          List(HttpHeaders.`Content-Type`(MediaTypes.`application/x-www-form-urlencoded`)),
          HttpEntity(ContentType(MediaTypes.`application/x-www-form-urlencoded`), urlEncodedParams)
        )
        r.withHeaders(HttpHeaders.`Content-Length`(r.entity.data.length))
      } else {
        baseRequest
      }
    }
    pipeline.flatMap(_(request))
  }

}
