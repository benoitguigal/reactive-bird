## Reactive Bird

***
Reactive Bird is a scala library based on Spray and Akka for accessing the Twitter REST APIv1.1. Support for OAuth. The streaming API will be added in the future.

### Features
- Fully asynchronous (built on top of akka). API calls are wrapped in `scala.concurrent.Future`
- Support for OAuth sign-in flow
- Successful calls returns instances of scala classes. Defaults models are provided but you can also provide your own for convenience
- Twitter errors are returns as subtypes of `TwitterError`
- Support for paginating timelines and navigating collections

### Get Reactive Bird

ReactiveBird for scala 2.10.2 is available on Sonatype.

```
libraryDependencies += "org.reactivebird" %% "reactivebird" % "1.1-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("snapshots")
```

No stable version yet

### Usage

```
val consumer = Consumer("your-consumer-key", "your-consumer-secret")
val token = Token("your-token-key", "your-token-secret")
val twitterApi = new TwitterApi(consumer, token)
val timeline: Future[Seq[Status]] = twitterApi.homeTimeline()
```

### Pagination

When working with timelines or large lists of items, you will need to paginate through the result set. The `Paging` trait
uses scala reactive streams (async Iterable) under the hood to iterate through result sets. It takes an instance of a pageable `Page => AbstractResultSet[A]`
as argument. Both pagination with maxId (`MaxIdPage => ResultSet[A]`) and pagination with cursors (`CursorPage => CursoredResultSet[A]`) are supported.

```
/// pagination for timelines, working with sinceId and maxId
val pageable: MaxIdPage => Future[ResultSet[Status]] = twitterApi.userTimeline(screenName = Some("BGuigal"))(_)
val paging = IdPaging(pageable, itemsPerPage = 200, sinceId = Some("sinceId"))
val tweets: Future[Seq[Status]] = paging.items(500) // retrieves 500 most recent tweets 200 tweets at time
val pages: Future[Seq[Seq[Status]] = paging.pages(3) // retrieves the first three pages of 200 tweets
```

```
/// pagination for followers list, working with cursors
val pageable: Future[CursorPage => CursoredResultSet[UserId]] = twitterApi.followersIds(screenName = Some("BGuigal"))(_)
val paging = CursorPaging(pageable, count = 2000)
val followersIds: Future[Seq[UserId]] = paging.items(3000) // retrieves the first 3000 followers 2000 followers at a time
val pages: Future[Seq[Seq[UserId]] = paging.pages(2, 1500) // retrieves 2 pages of 2000 followers
```

May the rate limit be hit in the process, the pagination will stop and return all the items that were
successfully retrieved


### Providing your own models

It is possible to provide your own models for User, Status, Place, etc. Just override the desired fields from the `ModelFactory` trait.

```
class MyStatus
implicit myStatusFormat: JsonFormat[MyStatus]

val twitterApi = new TwitterApi(consumer, token) {
    override type Status = MyStatus
    override implicit val statusFormat = myStatusFormat
}

```


### OAuth flow

```
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
