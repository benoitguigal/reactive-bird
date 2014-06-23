package twitter


case class Entities(
    hashtags: Seq[Hashtag],
    media: Seq[Media],
    urls: Seq[URL],
    userMentions: Seq[UserMention])


case class Hashtag(
    indices: Seq[Int],
    text: String)

class Media(
    displayUrl: String,
    expandedUrl: String,
    id: Long,
    idStr: String,
    indices: Seq[Int],
    mediaUrl: String,
    mediaUrlHttps: String,
    sizes: Map[String, Size],
    sourceStatusId: Long,
    sourceStatusIdStr: String,
    `type`: String,
    url: String)

case class URL(displayUrl: String, expandedUrl: String, indices: Seq[Int], url: String)

case class UserMention(id: Long, idStr: String, indices: Seq[Int], name: String, screenName: String)

case class Size(h: Int, resize: String, w: Int)
