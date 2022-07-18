package petclinic.models.api

import petclinic.models.PetId
import zio.json._

/** Models the parameters of a patch request that the client will send to the
  * server while removing the need for the request to handle generating an
  * VisitId.
  */
final case class UpdateVisit(date: Option[java.time.LocalDate], description: Option[String], petId: PetId)

/** Derives a JSON codec allowing the UpdateVisit request to be (de)serialized.
  */
object UpdateVisit {
  implicit val codec: JsonCodec[UpdateVisit] = DeriveJsonCodec.gen[UpdateVisit]
}
