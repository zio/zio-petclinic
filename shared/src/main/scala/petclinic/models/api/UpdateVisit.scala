package petclinic.models.api

import petclinic.models.PetId
import zio.json._

final case class UpdateVisit(date: Option[java.time.LocalDate], description: Option[String], petId: PetId)

object UpdateVisit {
  implicit val codec: JsonCodec[UpdateVisit] = DeriveJsonCodec.gen[UpdateVisit]
}
