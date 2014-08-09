# Reactive Bird : Asynchronous Scala client for Twitter REST API v1.1 

Reactive Bird is an asynchronous, actor-based and fast Scala library for accessing the Twitter REST API v1.1. It is built on top of [Spray 1.3.1](http://spray.io/) and [Akka 2.3.0](https://typesafe.com/platform/runtime/akka) 

## Get Reactive Bird

ReactiveBird for Scala 2.10.3 is available on Sonatype.

```scala
libraryDependencies += "org.reactivebird" %% "reactivebird" % "1.1"
```
or for the latest development version
```scala
libraryDependencies += "org.reactivebird" %% "reactivebird" % "1.2-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("snapshots")
```

WARNING: Reactive Bird 1.1 has been reasonably tested but is not ready for production yet. This library is however actively maintained and issues will be treated rapidly.

## Features 
* Fully asynchronous.  
* Results are wrapped around Scala models. 
* Support for pagination 
* OAuth sign-in flow 

## Usage

```scala
val consumer = Consumer("your-consumer-key", "your-consumer-secret")
val token = Token("your-token-key", "your-token-secret")
val twitterApi = new TwitterApi(consumer, token)
val timeline: Future[Seq[Status]] = twitterApi.homeTimeline()
```
## Endpoints 
ReactiveBird implements most of the endpoints defined in the [Twitter API documentation](https://dev.twitter.com/docs/api/1.1). In most cases, the name of the method is simply the full path to the resource. 

## Pagination

When working with timelines or large lists of items, you will need to paginate through the result set. The `Paging` trait
uses Play Enumerator under the hood to iterate through result sets. It takes a pageable `Page => ResultSet[A]`
as argument. Both pagination with maxId (`MaxIdPage => ResultSetWithMaxId[A]`) and pagination with cursors (`CursorPage => ResultSetWithCursor[A]`) are supported.

```scala
/// pagination for timelines, working with sinceId and maxId
val pageable: MaxIdPage => Future[ResultSetWithMaxId[Status]] = twitterApi.userTimeline(screenName = Some("BGuigal"))(_)
val paging = IdPaging(pageable, itemsPerPage = 200, sinceId = Some("sinceId"))
val tweets: Future[Seq[Status]] = paging.items(500) // retrieves 500 most recent tweets 200 tweets at time
val pages: Future[Seq[Seq[Status]]] = paging.pages(3) // retrieves the first three pages of 200 tweets
```

```scala
/// pagination for followers list, working with cursors
val pageable: Future[CursorPage => ResultSetWithCursor[UserId]] = twitterApi.followersIds(screenName = Some("BGuigal"))(_)
val paging = CursorPaging(pageable, count = 2000)
val followersIds: Future[Seq[UserId]] = paging.items(3000) // retrieves the first 3000 followers 2000 followers at a time
val pages: Future[Seq[Seq[UserId]]] = paging.pages(2, 1500) // retrieves 2 pages of 2000 followers
```

May the rate limit be hit in the process, the pagination will stop and return all the items that were
successfully retrieved

## Providing your own models

It is possible to provide your own models for User, Status, Place, etc. Just override the desired fields from the `ModelFactory` trait.

```scala
class MyStatus
implicit myStatusFormat: JsonFormat[MyStatus]

val twitterApi = new TwitterApi(consumer, token) {
    override type Status = MyStatus
    override implicit val statusFormat = myStatusFormat
}

```


## OAuth flow

```scala
import org.reactivebird.oauth.OAuthHandler

val consumer = Consumer("your-consumer-key", "your-consumer-secret")
val auth = OAuthHandler(consumer)

/// Get a request token
val authorizationUrl = auth.authorizationUrl("your-oauth-callback")

/// Redirect the user to authorizationUrl for sign-in
/// Once signed in the user is redirected to "your-oauth-callback"
/// extract oauth_token and oauth_verifier from the query string and then request an access token

val accessToken = auth.accessToken("oauth_token", "oauth_verifier")
/// Store token information and access protected resources
```


### License
Reactive Bird is free software licensed under the MIT/X11 license. Details provided in the LICENSE file.