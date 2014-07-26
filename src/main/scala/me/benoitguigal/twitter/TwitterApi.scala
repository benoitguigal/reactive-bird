package me.benoitguigal.twitter

import me.benoitguigal.twitter.http.{Authorizer, HttpService}
import me.benoitguigal.twitter.api._
import me.benoitguigal.twitter.models.ModelFactory


class TwitterApi(val consumer: Consumer, val token: Token)
  extends HttpService
  with ModelFactory
  with Timeline
  with Tweets
  with FriendsAndFollowers
  with Users {

  override def authorizer = Authorizer(consumer, token)
}

