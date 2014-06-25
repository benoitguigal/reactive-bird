package twitter.wrappers.defaults

case class Entities(
    hashtags: Option[Seq[Hashtag]],
    media: Option[Seq[Media]],
    urls: Option[Seq[URL]],
    user_mentions: Option[Seq[UserMention]])