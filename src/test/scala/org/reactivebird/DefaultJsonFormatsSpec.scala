package org.reactivebird

import org.scalatest.{Matchers, FlatSpec}
import spray.json.JsonParser
import org.joda.time.DateTime
import org.reactivebird.models.defaults._


class DefaultJsonFormatsSpec extends FlatSpec with Matchers {


  it should "parse Coordinates from json" in {
    val source =
      """{
        |        "type": "Point",
        |        "coordinates": [
        |          54.48590707,
        |          24.41206586
        |        ]
        |      }""".stripMargin
    val json = JsonParser(source)
    val coordinates = json.convertTo[Coordinates]
    coordinates should equal(Coordinates(54.48590707d, 24.41206586d))
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
    val status = json.convertTo[Status]
    status should equal (Status(
        None,
        new DateTime(1346260378000L),
        Entities(Some(List()),
        None,
        Some(List(URL("dev.twitter.com/blog/twitter-c…","https://dev.twitter.com/blog/twitter-certified-products",List(52, 73),"https://t.co/MjJ8xAnT"))),
        Some(List())),
        None,
        240859602684612600L,
        "240859602684612608",
        121,
        false,
        "<a href=\"http://sites.google.com/site/yorufukurou/\" rel=\"nofollow\">YoruFukurou</a>",
        "Introducing the Twitter Certified Products Program: https://t.co/MjJ8xAnT",
        User(new DateTime(1179900073000L), 1212864, 31, 6253282L, "6253282", "Twitter API", "twitterapi",3333)))
  }

  it should "parse DirectMessage from json" in {
    val source =
      """{
        |    "created_at": "Mon Aug 27 17:21:03 +0000 2012",
        |    "entities": {
        |        "hashtags": [],
        |        "urls": [],
        |        "user_mentions": []
        |    },
        |    "id": 240136858829479936,
        |    "id_str": "240136858829479936",
        |    "recipient": {
        |        "contributors_enabled": false,
        |        "created_at": "Thu Aug 23 19:45:07 +0000 2012",
        |        "default_profile": false,
        |        "default_profile_image": false,
        |        "description": "Keep calm and test",
        |        "favourites_count": 0,
        |        "follow_request_sent": false,
        |        "followers_count": 0,
        |        "following": false,
        |        "friends_count": 10,
        |        "geo_enabled": true,
        |        "id": 776627022,
        |        "id_str": "776627022",
        |        "is_translator": false,
        |        "lang": "en",
        |        "listed_count": 0,
        |        "location": "San Francisco, CA",
        |        "name": "Mick Jagger",
        |        "notifications": false,
        |        "profile_background_color": "000000",
        |        "profile_background_image_url": "http://a0.twimg.com/profile_background_images/644522235/cdjlccey99gy36j3em67.jpeg",
        |        "profile_background_image_url_https": "https://si0.twimg.com/profile_background_images/644522235/cdjlccey99gy36j3em67.jpeg",
        |        "profile_background_tile": true,
        |        "profile_image_url": "http://a0.twimg.com/profile_images/2550226257/y0ef5abcx5yrba8du0sk_normal.jpeg",
        |        "profile_image_url_https": "https://si0.twimg.com/profile_images/2550226257/y0ef5abcx5yrba8du0sk_normal.jpeg",
        |        "profile_link_color": "000000",
        |        "profile_sidebar_border_color": "000000",
        |        "profile_sidebar_fill_color": "000000",
        |        "profile_text_color": "000000",
        |        "profile_use_background_image": false,
        |        "protected": false,
        |        "screen_name": "s0c1alm3dia",
        |        "show_all_inline_media": false,
        |        "statuses_count": 0,
        |        "time_zone": "Pacific Time (US & Canada)",
        |        "url": "http://cnn.com",
        |        "utc_offset": -28800,
        |        "verified": false
        |    },
        |    "recipient_id": 776627022,
        |    "recipient_screen_name": "s0c1alm3dia",
        |    "sender": {
        |        "contributors_enabled": true,
        |        "created_at": "Sat May 09 17:58:22 +0000 2009",
        |        "default_profile": false,
        |        "default_profile_image": false,
        |        "description": "I taught your phone that thing you like.  The Mobile Partner Engineer @Twitter. ",
        |        "favourites_count": 584,
        |        "follow_request_sent": false,
        |        "followers_count": 10621,
        |        "following": false,
        |        "friends_count": 1181,
        |        "geo_enabled": true,
        |        "id": 38895958,
        |        "id_str": "38895958",
        |        "is_translator": false,
        |        "lang": "en",
        |        "listed_count": 190,
        |        "location": "San Francisco",
        |        "name": "Sean Cook",
        |        "notifications": false,
        |        "profile_background_color": "1A1B1F",
        |        "profile_background_image_url": "http://a0.twimg.com/profile_background_images/495742332/purty_wood.png",
        |        "profile_background_image_url_https": "https://si0.twimg.com/profile_background_images/495742332/purty_wood.png",
        |        "profile_background_tile": true,
        |        "profile_image_url": "http://a0.twimg.com/profile_images/1751506047/dead_sexy_normal.JPG",
        |        "profile_image_url_https": "https://si0.twimg.com/profile_images/1751506047/dead_sexy_normal.JPG",
        |        "profile_link_color": "2FC2EF",
        |        "profile_sidebar_border_color": "181A1E",
        |        "profile_sidebar_fill_color": "252429",
        |        "profile_text_color": "666666",
        |        "profile_use_background_image": true,
        |        "protected": false,
        |        "screen_name": "theSeanCook",
        |        "show_all_inline_media": true,
        |        "statuses_count": 2608,
        |        "time_zone": "Pacific Time (US & Canada)",
        |        "url": null,
        |        "utc_offset": -28800,
        |        "verified": false
        |    },
        |    "sender_id": 38895958,
        |    "sender_screen_name": "theSeanCook",
        |    "text": "booyakasha"
        |}""".stripMargin
    val json = JsonParser(source)
    val directMessage = json.convertTo[DirectMessage]
    directMessage should equal(DirectMessage(
        new DateTime(1346088063000L),
        Entities(Some(List()),None,Some(List()),Some(List())),
        240136858829479936L,
        "240136858829479936",
        776627022L,
        "s0c1alm3dia",
        38895958L,
        "theSeanCook",
        "booyakasha")
    )
  }

  it should "parse Source from json" in {
    val sourceJ =
        """{
          |  "can_dm": true,
          |  "blocking": false,
          |  "id_str": "819797",
          |  "all_replies": false,
          |  "want_retweets": true,
          |  "id": 819797,
          |  "marked_spam": false,
          |  "followed_by": true,
          |  "notifications_enabled": true,
          |  "screen_name": "episod",
          |  "following": true
          |}""".stripMargin
    val json = JsonParser(sourceJ)
    val source = json.convertTo[Source]
    source should equal (Source(819797L,"819797","episod",true,true))
  }

  it should "parse Target from json" in {
    val source =
        """{
          |  "id_str": "1401881",
          |  "id": 1401881,
          |  "followed_by": true,
          |  "screen_name": "dougw",
          |  "following": true
          |}""".stripMargin
    val json = JsonParser(source)
    val target = json.convertTo[Target]
    target should equal(Target(1401881L, "1401881", "dougw", true, true))
  }

  it should "parse Relationship from json" in {
    val source =
        """{
          |    "target": {
          |      "id_str": "1401881",
          |      "id": 1401881,
          |      "followed_by": true,
          |      "screen_name": "dougw",
          |      "following": true
          |    },
          |    "source": {
          |      "can_dm": true,
          |      "blocking": false,
          |      "id_str": "819797",
          |      "all_replies": false,
          |      "want_retweets": true,
          |      "id": 819797,
          |      "marked_spam": false,
          |      "followed_by": true,
          |      "notifications_enabled": true,
          |      "screen_name": "episod",
          |      "following": true
          |    }
          |  }""".stripMargin
    val json = JsonParser(source)
    val relationship = json.convertTo[Relationship]
    relationship should equal(Relationship(
      Source( 819797L, "819797", "episod", true, true), Target( 1401881L, "1401881", "dougw", true, true)))
  }

  it should "parse Friendship from json" in {
    val source =
        """{
          |  "relationship": {
          |    "target": {
          |      "id_str": "1401881",
          |      "id": 1401881,
          |      "followed_by": true,
          |      "screen_name": "dougw",
          |      "following": true
          |    },
          |    "source": {
          |      "can_dm": true,
          |      "blocking": false,
          |      "id_str": "819797",
          |      "all_replies": false,
          |      "want_retweets": true,
          |      "id": 819797,
          |      "marked_spam": false,
          |      "followed_by": true,
          |      "notifications_enabled": true,
          |      "screen_name": "episod",
          |      "following": true
          |    }
          |  }
          |}""".stripMargin
    val json = JsonParser(source)
    val friendship = json.convertTo[Friendship]
    friendship should equal(Friendship(
      Relationship(Source(819797L, "819797", "episod", true, true), Target(1401881L, "1401881", "dougw", true, true))))
  }

  it should "parse SavedSearch from json" in {

    val source =
        """{
          |  "created_at": "Fri Nov 04 18:46:41 +0000 2011",
          |  "id": 62353170,
          |  "id_str": "62353170",
          |  "name": "@anywhere",
          |  "position": null,
          |  "query": "@anywhere"
          |}""".stripMargin
    val json = JsonParser(source)
    val savedSearch = json.convertTo[SavedSearch]
    savedSearch should equal(SavedSearch(new DateTime(1320432401000L), 62353170L, "62353170", "@anywhere", "@anywhere"))
  }

  it should "parse SearchMetaData" in {

    val source =
        """{
          |  "max_id": 250126199840518145,
          |  "since_id": 24012619984051000,
          |  "refresh_url": "?since_id=250126199840518145&q=%23freebandnames&result_type=mixed&include_entities=1",
          |  "next_results": "?max_id=249279667666817023&q=%23freebandnames&count=4&include_entities=1&result_type=mixed",
          |  "count": 4,
          |  "completed_in": 0.035,
          |  "since_id_str": "24012619984051000",
          |  "query": "%23freebandnames",
          |  "max_id_str": "250126199840518145"
          |}""".stripMargin

    val json = JsonParser(source)
    val searchMetaData = json.convertTo[SearchMetaData]
    searchMetaData should equal(SearchMetaData(
        250126199840518145L,
        "?since_id=250126199840518145&q=%23freebandnames&result_type=mixed&include_entities=1",
        "?max_id=249279667666817023&q=%23freebandnames&count=4&include_entities=1&result_type=mixed",
        4,
        0.035,
        "24012619984051000",
        "%23freebandnames",
        "250126199840518145"))
  }

  it should "parse SearchResults" in {

    val source =
        """{
          |  "statuses": [
          |    {
          |      "coordinates": null,
          |      "favorited": false,
          |      "truncated": false,
          |      "created_at": "Fri Sep 21 22:51:18 +0000 2012",
          |      "id_str": "249279667666817024",
          |      "entities": {
          |        "urls": [],
          |        "hashtags": [
          |          {
          |            "text": "freebandnames",
          |            "indices": [
          |              20,
          |              34
          |            ]
          |          }
          |        ],
          |        "user_mentions": []
          |      },
          |      "in_reply_to_user_id_str": null,
          |      "contributors": null,
          |      "text": "The Foolish Mortals #freebandnames",
          |      "metadata": {
          |        "iso_language_code": "en",
          |        "result_type": "recent"
          |      },
          |      "retweet_count": 0,
          |      "in_reply_to_status_id_str": null,
          |      "id": 249279667666817024,
          |      "geo": null,
          |      "retweeted": false,
          |      "in_reply_to_user_id": null,
          |      "place": null,
          |      "user": {
          |        "profile_sidebar_fill_color": "BFAC83",
          |        "profile_sidebar_border_color": "615A44",
          |        "profile_background_tile": true,
          |        "name": "Marty Elmer",
          |        "profile_image_url": "http://a0.twimg.com/profile_images/1629790393/shrinker_2000_trans_normal.png",
          |        "created_at": "Mon May 04 00:05:00 +0000 2009",
          |        "location": "Wisconsin, USA",
          |        "follow_request_sent": null,
          |        "profile_link_color": "3B2A26",
          |        "is_translator": false,
          |        "id_str": "37539828",
          |        "entities": {
          |          "url": {
          |            "urls": [
          |              {
          |                "expanded_url": null,
          |                "url": "http://www.omnitarian.me",
          |                "indices": [
          |                  0,
          |                  24
          |                ]
          |              }
          |            ]
          |          },
          |          "description": {
          |            "urls": []
          |          }
          |        },
          |        "default_profile": false,
          |        "contributors_enabled": false,
          |        "favourites_count": 647,
          |        "url": "http://www.omnitarian.me",
          |        "profile_image_url_https": "https://si0.twimg.com/profile_images/1629790393/shrinker_2000_trans_normal.png",
          |        "utc_offset": -21600,
          |        "id": 37539828,
          |        "profile_use_background_image": true,
          |        "listed_count": 52,
          |        "profile_text_color": "000000",
          |        "lang": "en",
          |        "followers_count": 608,
          |        "protected": false,
          |        "notifications": null,
          |        "profile_background_image_url_https": "https://si0.twimg.com/profile_background_images/106455659/rect6056-9.png",
          |        "profile_background_color": "EEE3C4",
          |        "verified": false,
          |        "geo_enabled": false,
          |        "time_zone": "Central Time (US & Canada)",
          |        "description": "Cartoonist, Illustrator, and T-Shirt connoisseur",
          |        "default_profile_image": false,
          |        "profile_background_image_url": "http://a0.twimg.com/profile_background_images/106455659/rect6056-9.png",
          |        "statuses_count": 3575,
          |        "friends_count": 249,
          |        "following": null,
          |        "show_all_inline_media": true,
          |        "screen_name": "Omnitarian"
          |      },
          |      "in_reply_to_screen_name": null,
          |      "source": "<a href=\"http://twitter.com/download/iphone\" rel=\"nofollow\">Twitter for iPhone</a>",
          |      "in_reply_to_status_id": null
          |    }
          |  ],
          |  "search_metadata": {
          |    "max_id": 250126199840518145,
          |    "since_id": 24012619984051000,
          |    "refresh_url": "?since_id=250126199840518145&q=%23freebandnames&result_type=mixed&include_entities=1",
          |    "next_results": "?max_id=249279667666817023&q=%23freebandnames&count=4&include_entities=1&result_type=mixed",
          |    "count": 4,
          |    "completed_in": 0.035,
          |    "since_id_str": "24012619984051000",
          |    "query": "%23freebandnames",
          |    "max_id_str": "250126199840518145"
          |  }
          |}""".stripMargin

    val json = JsonParser(source)
    json.convertTo[SearchResults]
  }

  it should "parse Settings from json" in {
    val source =
      """{
        |  "always_use_https": true,
        |  "discoverable_by_email": true,
        |  "geo_enabled": true,
        |  "language": "en",
        |  "protected": false,
        |  "screen_name": "theSeanCook",
        |  "show_all_inline_media": false,
        |  "sleep_time": {
        |    "enabled": false,
        |    "end_time": null,
        |    "start_time": null
        |  },
        |  "time_zone": {
        |    "name": "Pacific Time (US & Canada)",
        |    "tzinfo_name": "America/Los_Angeles",
        |    "utc_offset": -28800
        |  },
        |  "trend_location": [
        |    {
        |      "country": "United States",
        |      "countryCode": "US",
        |      "name": "Atlanta",
        |      "parentid": 23424977,
        |      "placeType": {
        |        "code": 7,
        |        "name": "Town"
        |      },
        |      "url": "http://where.yahooapis.com/v1/place/2357024",
        |      "woeid": 2357024
        |    }
        |  ],
        |  "use_cookie_personalization": true
        |}""".stripMargin

    val json = JsonParser(source)
    val settings = json.convertTo[Settings]
  }

}
