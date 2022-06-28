package petclinic.models.api

import zio.json._

final case class CreateOwner(firstName: String, lastName: String, email: String, phone: String, address: String)

object CreateOwner {
  implicit val codec: JsonCodec[CreateOwner] =
    DeriveJsonCodec.gen[CreateOwner]
}
