package me.benoitguigal.twitter

import org.scalatest.{Matchers, FlatSpec}
import spray.json.JsonParser
import org.joda.time.DateTime
import me.benoitguigal.twitter.wrappers.defaults._
import me.benoitguigal.twitter.models.defaults.JsonFormats


class DefaultJsonFormatsSpec extends FlatSpec with Matchers {

  import JsonFormats._

  it should "parse Coordinates from json" in {
    val source = "[-97.51087576,35.46500176]"
    val json = JsonParser(source)
    val coordinates = json.convertTo[Coordinates]
    coordinates should equal(Coordinates(-97.51087576d, 35.46500176d))
  }


  it should "parse Hashtag from json" in {
    val source = """{"indices":[32,36],"text":"lol"}"""
    val json = JsonParser(source)
    val hashtag = json.convertTo[Hashtag]
    hashtag should equal(Hashtag(Seq(32, 36), "lol"))
  }


  it should "parse Media from json" in {
    val source =
      """{
        |    "type": "photo",
        |    "sizes": {
        |        "thumb": {
        |            "h": 150,
        |            "resize": "crop",
        |            "w": 150
        |        },
        |        "large": {
        |            "h": 238,
        |            "resize": "fit",
        |            "w": 226
        |        },
        |        "medium": {
        |            "h": 238,
        |            "resize": "fit",
        |            "w": 226
        |        },
        |        "small": {
        |            "h": 238,
        |            "resize": "fit",
        |            "w": 226
        |        }
        |    },
        |    "indices": [
        |        15,
        |        35
        |    ],
        |    "url": "http://t.co/rJC5Pxsu",
        |    "media_url": "http://p.twimg.com/AZVLmp-CIAAbkyy.jpg",
        |    "display_url": "pic.twitter.com/rJC5Pxsu",
        |    "id": 114080493040967680,
        |    "id_str": "114080493040967680",
        |    "expanded_url": "http://twitter.com/yunorno/status/114080493036773378/photo/1",
        |    "media_url_https": "https://p.twimg.com/AZVLmp-CIAAbkyy.jpg"
        |}""".stripMargin
    val json = JsonParser(source)
    val media = json.convertTo[Media]
    media should equal(Media(
        "pic.twitter.com/rJC5Pxsu",
        "http://twitter.com/yunorno/status/114080493036773378/photo/1",
        114080493040967680L,
        "114080493040967680",
        Seq(15, 35),
        "http://p.twimg.com/AZVLmp-CIAAbkyy.jpg",
        "http://t.co/rJC5Pxsu"))
  }

  it should "parse UserMention from json" in {
    val source =
      """{
        |    "screen_name": "TwitterEng",
        |    "name": "Twitter Engineering",
        |    "id": 6844292,
        |    "id_str": "6844292",
        |    "indices": [
        |        81,
        |        92
        |    ]
        |}""".stripMargin
    val json = JsonParser(source)
    val mention = json.convertTo[UserMention]
    mention should equal(UserMention(6844292L, "6844292", Seq(81, 92), "Twitter Engineering", "TwitterEng"))
  }

  it should "parse URL from json" in {
    val source =
      """{
        |    "url": "https://t.co/XdXRudPXH5",
        |    "expanded_url": "https://blog.twitter.com/2013/rich-photo-experience-now-in-embedded-tweets-3",
        |    "display_url": "blog.twitter.com/2013/rich-phot…",
        |    "indices": [
        |        80,
        |        103
        |    ]
        |}""".stripMargin
    val json = JsonParser(source)
    val url = json.convertTo[URL]
    url should equal(URL(
        "blog.twitter.com/2013/rich-phot…",
        "https://blog.twitter.com/2013/rich-photo-experience-now-in-embedded-tweets-3",
        Seq(80, 103),
        "https://t.co/XdXRudPXH5"))
  }

  it should "parse Entities from json" in {
    val source =
      """{
        |    "hashtags": [],
        |    "symbols": [],
        |    "urls": [],
        |    "user_mentions": [],
        |    "media": []
        |}""".stripMargin
    val json = JsonParser(source)
    val entities = json.convertTo[Entities]
    entities should equal(Entities(Some(Seq()), Some(Seq()), Some(Seq()), Some(Seq())))
  }

  it should "parse Place from json" in {
    val source =
      """{
        |    "attributes":{},
        |     "bounding_box":
        |    {
        |        "coordinates":
        |        [[
        |                [-77.119759,38.791645],
        |                [-76.909393,38.791645],
        |                [-76.909393,38.995548],
        |                [-77.119759,38.995548]
        |        ]],
        |        "type":"Polygon"
        |    },
        |     "country":"United States",
        |     "country_code":"US",
        |     "full_name":"Washington, DC",
        |     "id":"01fbe706f872cb32",
        |     "name":"Washington",
        |     "place_type":"city",
        |     "url": "http://api.twitter.com/1/geo/id/01fbe706f872cb32.json"
        |}""".stripMargin
    val json = JsonParser(source)
    val place = json.convertTo[Place]
    place should equal(Place("United States", "US", "Washington, DC", "01fbe706f872cb32", "Washington", "city", "http://api.twitter.com/1/geo/id/01fbe706f872cb32.json"))

  }

  it should "parse User from json" in {
    val source =
      """{
        |    "statuses_count": 3080,
        |    "favourites_count": 22,
        |    "protected": false,
        |    "profile_text_color": "437792",
        |    "profile_image_url": "...",
        |    "name": "Twitter API",
        |    "profile_sidebar_fill_color": "a9d9f1",
        |    "listed_count": 9252,
        |    "following": true,
        |    "profile_background_tile": false,
        |    "utc_offset": -28800,
        |    "description": "The Real Twitter API. I tweet about API changes, service issues and happily answer questions about Twitter and our API. Don't get an answer? It's on my website.",
        |    "location": "San Francisco, CA",
        |    "contributors_enabled": true,
        |    "verified": true,
        |    "profile_link_color": "0094C2",
        |    "followers_count": 665829,
        |    "url": "http://dev.twitter.com",
        |    "default_profile": false,
        |    "profile_sidebar_border_color": "0094C2",
        |    "screen_name": "twitterapi",
        |    "default_profile_image": false,
        |    "notifications": false,
        |    "display_url": null,
        |    "show_all_inline_media": false,
        |    "geo_enabled": true,
        |    "profile_use_background_image": true,
        |    "friends_count": 32,
        |    "id_str": "6253282",
        |    "entities": {
        |        "hashtags": [],
        |        "urls": [],
        |        "user_mentions": []
        |    },
        |    "expanded_url": null,
        |    "is_translator": false,
        |    "lang": "en",
        |    "time_zone": "Pacific Time (US &amp; Canada)",
        |    "created_at": "Wed May 23 06:01:13 +0000 2007",
        |    "profile_background_color": "e8f2f7",
        |    "id": 6253282,
        |    "follow_request_sent": false,
        |    "profile_background_image_url_https": "...",
        |    "profile_background_image_url": "...",
        |    "profile_image_url_https": "..."
        |}""".stripMargin
    val json = JsonParser(source)
    val user = json.convertTo[User]
    user should equal(User(
        new DateTime(1179900073000L),
        665829,
        32,
        6253282L,
        "6253282",
        "Twitter API",
        "twitterapi",
        3080
    ))
  }

  it should "parse Status from json" in {
    val source =
      """{
        |    "coordinates": null,
        |    "favorited": false,
        |    "truncated": false,
        |    "created_at": "Wed Aug 29 17:12:58 +0000 2012",
        |    "id_str": "240859602684612608",
        |    "entities": {
        |        "urls": [
        |            {
        |                "expanded_url": "https://dev.twitter.com/blog/twitter-certified-products",
        |                "url": "https://t.co/MjJ8xAnT",
        |                "indices": [
        |                    52,
        |                    73
        |                ],
        |                "display_url": "dev.twitter.com/blog/twitter-c…"
        |            }
        |        ],
        |        "hashtags": [],
        |        "user_mentions": []
        |    },
        |    "in_reply_to_user_id_str": null,
        |    "contributors": null,
        |    "text": "Introducing the Twitter Certified Products Program: https://t.co/MjJ8xAnT",
        |    "retweet_count": 121,
        |    "in_reply_to_status_id_str": null,
        |    "id": 240859602684612600,
        |    "geo": null,
        |    "retweeted": false,
        |    "possibly_sensitive": false,
        |    "in_reply_to_user_id": null,
        |    "place": null,
        |    "user": {
        |        "profile_sidebar_fill_color": "DDEEF6",
        |        "profile_sidebar_border_color": "C0DEED",
        |        "profile_background_tile": false,
        |        "name": "Twitter API",
        |        "profile_image_url": "http://a0.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_normal.png",
        |        "created_at": "Wed May 23 06:01:13 +0000 2007",
        |        "location": "San Francisco, CA",
        |        "follow_request_sent": false,
        |        "profile_link_color": "0084B4",
        |        "is_translator": false,
        |        "id_str": "6253282",
        |        "entities": {
        |            "url": {
        |                "urls": [
        |                    {
        |                        "expanded_url": null,
        |                        "url": "http://dev.twitter.com",
        |                        "indices": [
        |                            0,
        |                            22
        |                        ]
        |                    }
        |                ]
        |            },
        |            "description": {
        |                "urls": []
        |            }
        |        },
        |        "default_profile": true,
        |        "contributors_enabled": true,
        |        "favourites_count": 24,
        |        "url": "http://dev.twitter.com",
        |        "profile_image_url_https": "https://si0.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_normal.png",
        |        "utc_offset": -28800,
        |        "id": 6253282,
        |        "profile_use_background_image": true,
        |        "listed_count": 10775,
        |        "profile_text_color": "333333",
        |        "lang": "en",
        |        "followers_count": 1212864,
        |        "protected": false,
        |        "notifications": null,
        |        "profile_background_image_url_https": "https://si0.twimg.com/images/themes/theme1/bg.png",
        |        "profile_background_color": "C0DEED",
        |        "verified": true,
        |        "geo_enabled": true,
        |        "time_zone": "Pacific Time (US & Canada)",
        |        "description": "The Real Twitter API. I tweet about API changes, service issues and happily answer questions about Twitter and our API. Don't get an answer? It's on my website.",
        |        "default_profile_image": false,
        |        "profile_background_image_url": "http://a0.twimg.com/images/themes/theme1/bg.png",
        |        "statuses_count": 3333,
        |        "friends_count": 31,
        |        "following": null,
        |        "show_all_inline_media": false,
        |        "screen_name": "twitterapi"
        |    },
        |    "in_reply_to_screen_name": null,
        |    "source": "<a href=\"http://sites.google.com/site/yorufukurou/\" rel=\"nofollow\">YoruFukurou</a>",
        |    "in_reply_to_status_id": null
        |}""".stripMargin
    val json = JsonParser(source)
    json.convertTo[Status]
  }

}
