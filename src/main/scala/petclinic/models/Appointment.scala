package petclinic.models

import zio.{Random, Task, UIO, ZIO}
import zio.json._

import java.util.UUID

final case class AppointmentId(id: UUID) extends AnyVal

object AppointmentId {

  def random: UIO[AppointmentId] = Random.nextUUID.map(AppointmentId(_))

  def fromString(id: String): Task[AppointmentId] = ZIO.attempt(AppointmentId(UUID.fromString(id)))

  implicit val codec: JsonCodec[AppointmentId] = JsonCodec[UUID].transform(AppointmentId(_), _.id)

}

final case class Appointment(
    id: AppointmentId,
    petId: PetId,
    date: java.time.LocalDateTime,
    description: String,
    vetId: VetId
)

object Appointment {

  def apply(
      petId: PetId,
      date: java.time.LocalDateTime,
      description: String,
      vetId: VetId
  ): UIO[Appointment] =
    AppointmentId.random.map(id => Appointment(id, petId, date, description, vetId))

  implicit val codec: JsonCodec[Appointment] = DeriveJsonCodec.gen[Appointment]

}
