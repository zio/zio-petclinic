package petclinic.models

import zio._
import zio.json._
import java.util.UUID

final case class VisitId(id: UUID) extends AnyVal

object VisitId {

  def random: UIO[VisitId] = Random.nextUUID.map(VisitId(_))

  def fromString(id: String): Task[VisitId] = ZIO.attempt(VisitId(UUID.fromString(id)))

  implicit val codec: JsonCodec[VisitId] = JsonCodec[UUID].transform(VisitId(_), _.id)

}
