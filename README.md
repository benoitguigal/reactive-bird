## Twitter-Spray

***
Twitter-Spray is a scala library based on Spray and Akka for accessing the Twitter REST APIv1.1. Support for OAuth. The streaming API will be added in the future.

### Features
- Fully asynchronous (built on top of akka). API calls are wrapped in `scala.concurrent.Future`
- Support for OAuth sign-in flow
- Successful calls returns scala wrapper types. Default wrapper types are provided for Tweets, User, Entities and Places but you can provide your owns if necessary.
- Twitter errors are returns as subtypes of `TwitterError`
- Support for rate limiting, paging and cursoring (yet to come)

### Get Twitter-Spray

Twitter-Spray for scala 2.10.2 is available on Sonatype.

```
libraryDependencies += "me.benoitguigal" %% "twitter" % "1.1-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("snapshots")
```

No stable version yet

### Usage

#### If you already have a token

```
val consumer = Consumer("your-consumer-key", "your-consumer-secret")
val twitterApi = TwitterApi(consumer)
val token = Token("your-token", Some("your-token-secret"))
twitterApi.setToken(token)
val timeline: Future[Seq[Status]] = twitterApi.homeTimeline()
```

#### If you need to get an access token

```
val consumer = Consumer("your-consumer-key", "your-consumer-secret")
val twitterApi = TwitterApi(consumer)
val requestToken = twitterApi.requestToken("your-oauth-callbak")
/// send token to the client and ask the user to sign-in. Retrieve token and verifier
val token = Token("token", None)
twitterApi.setToken(token)
val accessToken = twitterApi.accessToken("your-oauth-verifier")
/// store accessToken and secret
```

#### If you need to provide your own wrapper types around Twitter objects

First define your own wrapper types
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

Then define your own WrapperTypes and mix it in TwitterApi
```
trait MyWrapperTypes {
  type Status = MyStatus
  type User = MyUser
  implicit val statusFormat = MyJsonFormats.statusFormat
  implicit val userFormat = MyJsonFormats.userFormat
}

val twitterApi = new TwitterApi with MyWrapperTypes {
   override val consumer = _consumer
}
```

### License
Twitter-spray is free software licensed under the MIT/X11 license. Details provided in the LICENSE file.
