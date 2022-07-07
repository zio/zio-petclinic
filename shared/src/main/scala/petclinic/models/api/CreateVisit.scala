package petclinic.models.api

import zio.json._

/** Models the parameters of a post request that the client will send to the
  * server while removing the need for the request to handle generating an
  * VisitId.
  */
final case class CreateVisit(
    date: java.time.LocalDate,
    description: String
)

/** Derives a JSON codec allowing the CreateVisit request to be (de)serialized.
  */
object CreateVisit {
  implicit val codec: JsonCodec[CreateVisit] = DeriveJsonCodec.gen[CreateVisit]
}
