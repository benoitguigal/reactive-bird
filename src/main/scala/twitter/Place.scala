package twitter

case class Place(
    attributes: Map[String, String],
    boundingBox: BoundingBox,
    country: String,
    countryCode: String,
    fullName: String,
    id: String,
    name: String,
    placeType: String,
    url: String)


case class BoundingBox(coordinates: Seq[Seq[Seq[Double]]], `type`: String)
