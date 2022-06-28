package petclinic.models

import zio._
import zio.json._
import java.util.UUID

final case class VetId(id: UUID) extends AnyVal

object VetId {
  implicit val codec: JsonCodec[VetId] = JsonCodec[UUID].transform(VetId(_), _.id)

  def fromString(id: String): Task[VetId] =
    ZIO.attempt(VetId(UUID.fromString(id)))
}
