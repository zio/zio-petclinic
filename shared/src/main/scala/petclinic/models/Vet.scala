package petclinic.models

import zio.json._
import zio._

import java.util.UUID

final case class VetId(id: UUID) extends AnyVal

object VetId {
  implicit val codec: JsonCodec[VetId] = JsonCodec[UUID].transform(VetId(_), _.id)

  def fromString(id: String): Task[VetId] =
    ZIO.attempt(VetId(UUID.fromString(id)))
}

final case class Vet(id: VetId, lastName: String, specialty: String)

object Vet {
  implicit val codec: JsonCodec[Vet] = DeriveJsonCodec.gen[Vet]
}
