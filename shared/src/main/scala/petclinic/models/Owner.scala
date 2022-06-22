package petclinic.models

import zio._
import zio.json._

final case class Owner(
    id: OwnerId,
    firstName: String,
    lastName: String,
    address: String,
    phone: String,
    email: String
) {
  def fullName: String = firstName + " " + lastName
}

object Owner {
  def make(firstName: String, lastName: String, address: String, phone: String, email: String): UIO[Owner] =
    OwnerId.random.map(Owner(_, firstName, lastName, address, phone, email))

  implicit val codec: JsonCodec[Owner] =
    DeriveJsonCodec.gen[Owner]
}
