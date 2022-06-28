package petclinic.models

import zio._
import zio.json._
import java.util.UUID

final case class PetId(id: UUID) extends AnyVal

object PetId {
  def random: UIO[PetId] = Random.nextUUID.map(PetId(_))

  def fromString(id: String): Task[PetId] =
    ZIO.attempt {
      PetId(UUID.fromString(id))
    }

  implicit val codec: JsonCodec[PetId] = JsonCodec[UUID].transform(PetId(_), _.id)
}
