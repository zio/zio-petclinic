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

  implicit val codec: JsonCodec[OwnerId] = JsonCodec[UUID].transform(OwnerId(_), _.id)
}

final case class Owner(id: OwnerId, firstName: String, lastName: String, address: String, phone: String)

object Owner {

  def apply(firstName: String, lastName: String, address: String, phone: String): UIO[Owner] =
    OwnerId.random.map(Owner(_, firstName, lastName, address, phone))

  implicit val codec: JsonCodec[Owner] = DeriveJsonCodec.gen[Owner]
}
