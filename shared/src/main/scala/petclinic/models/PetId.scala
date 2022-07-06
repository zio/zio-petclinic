package petclinic.models

import zio._
import zio.json._

import java.util.UUID

/** PetId is a wrapper for UUID. This is a merely a convenience to prevent us
  * from passing the wrong ID type along
  */
final case class PetId(id: UUID) extends AnyVal

object PetId {

  /** Generates a Random UUID and wraps it in the PetId type. */
  def random: UIO[PetId] = Random.nextUUID.map(PetId(_))

  /** Allows a UUID to be parsed from a string which is then wrapped in the
    * PetId type.
    */
  def fromString(id: String): Task[PetId] =
    ZIO.attempt {
      PetId(UUID.fromString(id))
    }

  /** Generates a codec allowing a UUID to be (de)serialized as an PetId. */
  implicit val codec: JsonCodec[PetId] = JsonCodec[UUID].transform(PetId(_), _.id)
}
