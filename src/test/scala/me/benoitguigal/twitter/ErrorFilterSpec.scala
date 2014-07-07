package me.benoitguigal.twitter

import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, FunSpec}
import spray.http.{HttpEntity, StatusCode, HttpResponse}
import org.mockito.Mockito._


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

     it("should throw error if status code != 200") {
       pending
       val response = mock[HttpResponse]
       val statusCode = mock[StatusCode]
       when(statusCode.intValue).thenReturn(410)
       when(response.status).thenReturn(statusCode)
       val entity = mock[HttpEntity]
       when(entity.asString).thenReturn("""{"errors":[{"message":"Sorry, that page does not exist","code":34}]}""")
       when(response.entity).thenReturn(entity)
       intercept[TwitterErrorDoesNotExist] {
         errorFilter(response)
       }
     }
   }


}
