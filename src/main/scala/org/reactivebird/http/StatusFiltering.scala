package org.reactivebird.http

import org.reactivebird.TwitterError
import spray.http.HttpRequest


trait StatusFiltering extends HttpService {

  private val statusFilter = TwitterError.errorFilter

  override protected def getPipeline = {
    super.getPipeline map { p =>
      request: HttpRequest => {
        p(request) map (statusFilter(_))
      }
    }
  }
}
