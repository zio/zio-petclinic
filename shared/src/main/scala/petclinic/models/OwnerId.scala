package petclinic.models

import zio._
import zio.json._

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
