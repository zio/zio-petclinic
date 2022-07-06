package petclinic.models

import zio._
import zio.json._

import java.util.UUID

/** OwnerId is a wrapper for UUID. This is a merely a convenience to prevent us
  * from passing the wrong ID type along
  */
final case class OwnerId(id: UUID) extends AnyVal

object OwnerId {

  /** Generates a Random UUID and wraps it in the OwnerId type. */
  def random: UIO[OwnerId] = Random.nextUUID.map(OwnerId(_))

  /** Allows a UUID to be parsed from a string which is then wrapped in the
    * OwnerId type.
    */
  def fromString(id: String): Task[OwnerId] =
    ZIO.attempt {
      OwnerId(UUID.fromString(id))
    }

  /** Generates a codec allowing a UUID to be (de)serialized as an OwnerId. */
  implicit val codec: JsonCodec[OwnerId] =
    JsonCodec[UUID].transform(OwnerId(_), _.id)
}
