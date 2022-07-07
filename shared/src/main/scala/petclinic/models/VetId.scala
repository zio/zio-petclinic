package petclinic.models

import zio._
import zio.json._

import java.util.UUID

/** VetId is a wrapper for UUID.
  *
  * This is a merely a convenience to prevent us from passing the wrong ID type
  * along
  */
final case class VetId(id: UUID) extends AnyVal

object VetId {

  /** Allows a UUID to be parsed from a string which is then wrapped in the
    * VetId type.
    */
  def fromString(id: String): Task[VetId] =
    ZIO.attempt(VetId(UUID.fromString(id)))

  /** Derives a codec allowing a UUID to be (de)serialized as an VetId. */
  implicit val codec: JsonCodec[VetId] = JsonCodec[UUID].transform(VetId(_), _.id)
}
