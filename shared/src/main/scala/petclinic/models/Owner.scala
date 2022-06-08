package petclinic.models

import zio.json._
import zio.{Random, Task, UIO, ZIO}

import java.util.UUID

final case class OwnerId(id: UUID) extends AnyVal

object OwnerId {
  def random: UIO[OwnerId] = Random.nextUUID.map(OwnerId(_))

  def fromString(id: String): Task[OwnerId] =
    ZIO.attempt {
      OwnerId(UUID.fromString(id))
    }

  implicit val codec: JsonCodec[OwnerId] =
    JsonCodec[UUID].transform(OwnerId(_), _.id)
}

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
  def apply(firstName: String, lastName: String, address: String, phone: String, email: String): UIO[Owner] =
    OwnerId.random.map(Owner(_, firstName, lastName, address, phone, email))

  implicit val codec: JsonCodec[Owner] =
    DeriveJsonCodec.gen[Owner]
}

final case class CreateOwner(firstName: String, lastName: String, email: String, phone: String, address: String)

object CreateOwner {
  implicit val codec: JsonCodec[CreateOwner] =
    DeriveJsonCodec.gen[CreateOwner]
}

final case class UpdateOwner(
    firstName: Option[String],
    lastName: Option[String],
    address: Option[String],
    phone: Option[String],
    email: Option[String]
)

object UpdateOwner {
  implicit val codec: JsonCodec[UpdateOwner] =
    DeriveJsonCodec.gen[UpdateOwner]
}
