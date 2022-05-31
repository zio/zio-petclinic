package petclinic.models

import zio.json._
import zio.{Random, Task, UIO, ZIO}

import java.util.UUID

final case class VisitId(id: UUID) extends AnyVal

object VisitId {

  def random: UIO[VisitId] = Random.nextUUID.map(VisitId(_))

  def fromString(id: String): Task[VisitId] = ZIO.attempt(VisitId(UUID.fromString(id)))

  implicit val codec: JsonCodec[VisitId] = JsonCodec[UUID].transform(VisitId(_), _.id)

}

final case class Visit(
    id: VisitId,
    petId: PetId,
    date: java.time.LocalDate,
    description: String,
    vetId: VetId
)

object Visit {

  def apply(
      petId: PetId,
      date: java.time.LocalDate,
      description: String,
      vetId: VetId
  ): UIO[Visit] =
    VisitId.random.map(id => Visit(id, petId, date, description, vetId))

  implicit val codec: JsonCodec[Visit] = DeriveJsonCodec.gen[Visit]

}
