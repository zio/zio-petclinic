package petclinic.models

import zio._
import zio.json._

import java.util.UUID

// wraps UUID as a specific OwnerId so that we cannot use an incorrect UUID
final case class OwnerId(id: UUID) extends AnyVal

object OwnerId {

  def random: ZIO[Random, Nothing, OwnerId] = Random.nextUUID.map(OwnerId(_))

  implicit val codec: JsonCodec[OwnerId] = JsonCodec[UUID].transform(OwnerId(_), _.id)

  def fromString(id: String): Task[OwnerId] =
    ZIO.attempt {
      OwnerId(UUID.fromString(id))
    }
}

// represents an owner of a pet
final case class Owner(id: OwnerId, firstName: String, lastName: String, address: String, phone: String)

object Owner {

  def apply(firstName: String, lastName: String, address: String, phone: String): ZIO[Random, Nothing, Owner] =
    OwnerId.random.map(Owner(_, firstName, lastName, address, phone))

  implicit val codec: JsonCodec[Owner] = DeriveJsonCodec.gen[Owner]
}
