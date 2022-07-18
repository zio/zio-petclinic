package petclinic.models.api

import zio.json._

/** Models the parameters of a patch request that the client will send to the
  * server while removing the need for the request to handle generating an
  * OwnerId.
  */
final case class UpdateOwner(
    firstName: Option[String],
    lastName: Option[String],
    address: Option[String],
    phone: Option[String],
    email: Option[String]
)

/** Derives a JSON codec allowing the UpdateOwner request to be (de)serialized.
  */
object UpdateOwner {
  implicit val codec: JsonCodec[UpdateOwner] =
    DeriveJsonCodec.gen[UpdateOwner]
}
