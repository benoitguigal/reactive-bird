## Twitter-Spray

***
Twitter-Spray is a scala library based on Spray and Akka for accessing the Twitter REST APIv1.1. Support for OAuth. The streaming API will be added in the future.

### Features
- Fully asynchronous (built on top of akka). API calls are wrapped in `scala.concurrent.Future`
- Support for OAuth sign-in flow
- Successful calls returns scala wrapper types. Default wrapper types are provided for Tweets, User, Entities and Places but you can provide your owns if necessary.
- Twitter errors are returns as subtypes of `TwitterError`
- Support for paginating timelines and navigating collections

### Get Twitter-Spray

Twitter-Spray for scala 2.10.2 is available on Sonatype.

```
libraryDependencies += "me.benoitguigal" %% "twitter" % "1.1-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("snapshots")
```

No stable version yet

### Usage

```
val consumer = Consumer("your-consumer-key", "your-consumer-secret")
val token = Token("your-token-key", "your-token-secret")
val twitterApi = TwitterApi(consumer, token)
val timeline: Future[Seq[Status]] = twitterApi.homeTimeline()
```


### Working with timelines
Timeline results are limited to 200 statuses max. It is possible to iterate through timeline results in order to build a more complete list

```
import me.benoitguigal.twitter.api.Timeline.paginate

val paging = new Paging(200, Some("since-id"), Some("max-id"))
paginate(paging) { paging =>
    twitterApi.userTimeline(screenName = Some("BGuigal"), paging = paging)
}
```
The example above retrieves statuses from @BGuigal timeline 200 at a time from "max-id" to "since-id".
May the rate limit be hit in the process, the `paginate` method would recover and return the list of statuses that were
successfully retrieved. With a rate limit window of 15/user, you can retrieve up to 3000 statuses in a single method call.

### Using cursors to navigate collections

The Twitter API utilizes a technique called 'cursoring' to paginate large results set. The example below show how
to retrieve cursored results:

```
import me.benoitguigal.twitter.api.Navigation.cursoring

cursoring { cursor =>
    twitterApi.followersIds(userId = None, screenName = Some("babgi"), cursor = Some(cursor))
}
```

### Providing your own types

It is possible to provide your own wrapper types for Twitter objects.

```
class MyStatus
class MyUser
```

Then define the corresponding JsonFormats

```
import spray.json._
object MyJsonFormats extends DefaultJsonProtocol {
 /// your implicit formats go here
}
```

Then define implementation of the WrapperTypes and mix it in TwitterApi
```
trait MyWrapperTypes extends WrapperTypes {
  type Status = MyStatus
  type User = MyUser
  implicit val statusFormat = MyJsonFormats.statusFormat
  implicit val userFormat = MyJsonFormats.userFormat
}

val twitterApi = new TwitterApi with MyWrapperTypes {
  val consumer = _consumer
  val token = _token
}
```

### OAuth flow

```
import me.benoitguigal.twitter.oauth.OAuthClient

val consumer = Consumer("your-consumer-key", "your-consumer-secret")
val oauthClient = OAuthClient(consumer)

/// Get a request token
val requestToken = oauthClient.requestToken("your-oauth-callback")

/// Redirect the user to s"https://api.twitter.com/oauth/authenticate?oauth_token=${requestToken.oauthToken}"
/// the user sign-in and is redirected to "your-oauth-callback"
/// extract oauth_token and oauth_verifier from the query string and then request an access token

val accessToken = oauthClient.accessToken("oauth_token", "oauth_verifier")
/// Store token information and access protected resources
```


### License
Twitter-spray is free software licensed under the MIT/X11 license. Details provided in the LICENSE file.
