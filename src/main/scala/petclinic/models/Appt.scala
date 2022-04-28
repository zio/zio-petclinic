package petclinic.models

import zio.{Random, ZIO}

import java.util.UUID

final case class ApptId(id: UUID) extends AnyVal

object ApptId {

  def random: ZIO[Random, Nothing, ApptId] = Random.nextUUID.map(ApptId(_))

}

final case class Appt(id: ApptId, date: java.time.LocalDateTime, description: String, vetId: VetId)

object Appt {

  def apply(date: java.time.LocalDateTime, description: String, vetId: VetId): ZIO[Random, Nothing, Appt] =
    ApptId.random.map(id => Appt(id, date, description, vetId))

}
