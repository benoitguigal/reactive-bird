package me.benoitguigal.twitter

import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, FunSpec}
import spray.http._
import org.mockito.Mockito._
import spray.http.HttpResponse


class ErrorFilterSpec extends FunSpec with MockitoSugar with Matchers {

   import TwitterError.errorFilter

   describe("errorFilter") {

     it("should return the http response if status code == 200") {
       val response = mock[HttpResponse]
       val statusCode = mock[StatusCode]
       when(statusCode.intValue).thenReturn(200)
       when(response.status).thenReturn(statusCode)
       errorFilter(response) should equal(response)
     }

     it("should throw exception with response body if Content-Type != application/json") {
       val entity = mock[HttpEntity]
       val statusCode = mock[StatusCode]
       val response = mock[HttpResponse]
       when(statusCode.intValue).thenReturn(500)
       when(response.entity).thenReturn(entity)
       when(entity.asString).thenReturn("boom")
       when(response.status).thenReturn(statusCode)
       when(response.headers).thenReturn(List(HttpHeaders.`Content-Type`(ContentTypes.`text/plain`)))
       intercept[TwitterError] {
         errorFilter(response)
       }
     }

     it("should parse Twitter error according to error code") {
       val response = mock[HttpResponse]
       val statusCode = mock[StatusCode]
       when(statusCode.intValue).thenReturn(410)
       when(response.status).thenReturn(statusCode)
       when(response.headers).thenReturn(List(HttpHeaders.`Content-Type`(ContentTypes.`application/json`)))
       val entity = mock[HttpEntity]
       when(entity.asString).thenReturn("""{"errors":[{"message":"Sorry, that page does not exist","code":34}]}""")
       when(response.entity).thenReturn(entity)
       intercept[TwitterErrorDoesNotExist] {
         errorFilter(response)
       }
     }
   }


}
