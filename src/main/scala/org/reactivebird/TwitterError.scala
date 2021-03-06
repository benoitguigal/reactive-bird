package org.reactivebird

import scala.annotation.switch
import spray.json._
import spray.http.{HttpHeaders, ContentTypes}


abstract class TwitterError extends Exception {
  val message: String
  override def getMessage = message
}

object TwitterError {

  import spray.client.pipelining._

  def errorFilter: ResponseTransformer = {
    response => {
      if (response.status.intValue == 200) {
        response
      }
      else {
        val contentType = response.headers.collect { case HttpHeaders.`Content-Type`(ct) => ct }.head
        if (contentType == ContentTypes.`application/json`) {
          val json = JsonParser(response.entity.asString)
          json.asJsObject.getFields("errors") match {
            case Seq(JsArray(errors)) if (errors.size >=1) =>
              val fields = errors(0).asJsObject.fields
              val JsString(message) = fields.get("message").get
              val JsNumber(code) = fields.get("code").get
              throw TwitterError(code.toInt, message)
            case _ => throw TwitterError(response.entity.asString)
          }
        }
        else {
          throw TwitterError(response.entity.asString)
        }
      }
    }
  }

  private def apply(_message: String) = new TwitterError {
    override val message: String = _message
  }

  private def apply(code: Int, message: String): TwitterError = (code: @switch) match {
    case 32     => new TwitterErrorCouldNotAuthenticate(message)
    case 34     => new TwitterErrorDoesNotExist(message)
    case 64     => new TwitterErrorAccountSuspended(message)
    case 88     => new TwitterErrorRateLimitExceeded(message)
    case 89     => new TwitterErrorInvalidToken(message)
    case 130    => new TwitterErrorOverCapacity(message)
    case 131    => new TwitterErrorInternalError(message)
    case 135    => new TwitterErrorCouldNotAuthenticateYou(message)
    case 187    => new TwitterErrorStatusIsADuplicate(message)
    case 215    => new TwitterErrorBadAuthenticationData(message)
    case 231    => new TwitterErrorUserMustVerifyLogin(message)
    case _      => TwitterError(message)
  }


}

class TwitterErrorCouldNotAuthenticate(val message: String) extends TwitterError

class TwitterErrorDoesNotExist(val message: String) extends TwitterError

class TwitterErrorAccountSuspended(val message: String) extends TwitterError

class TwitterErrorRateLimitExceeded(val message: String) extends TwitterError

class TwitterErrorInvalidToken(val message: String) extends TwitterError

class TwitterErrorOverCapacity(val message: String) extends TwitterError

class TwitterErrorInternalError(val message: String) extends TwitterError

class TwitterErrorCouldNotAuthenticateYou(val message: String) extends TwitterError

class TwitterErrorStatusIsADuplicate(val message: String) extends TwitterError

class TwitterErrorBadAuthenticationData(val message: String) extends TwitterError

class TwitterErrorUserMustVerifyLogin(val message: String) extends TwitterError




