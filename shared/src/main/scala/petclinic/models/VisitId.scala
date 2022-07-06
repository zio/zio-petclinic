package petclinic.models

import zio._
import zio.json._

import java.util.UUID

/** VisitId is a wrapper for UUID. This is a merely a convenience to prevent us
  * from passing the wrong ID type along
  */
final case class VisitId(id: UUID) extends AnyVal

object VisitId {

  /** Generates a Random UUID and wraps it in the Visit type. */
  def random: UIO[VisitId] = Random.nextUUID.map(VisitId(_))

  /** Allows a UUID to be parsed from a string which is then wrapped in the
    * VisitId type.
    */
  def fromString(id: String): Task[VisitId] = ZIO.attempt(VisitId(UUID.fromString(id)))

  /** Generates a codec allowing a UUID to be (de)serialized as an VisitId. */
  implicit val codec: JsonCodec[VisitId] = JsonCodec[UUID].transform(VisitId(_), _.id)

}
