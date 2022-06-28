package petclinic.models.api

import zio.json._

final case class CreateVisit(
    date: java.time.LocalDate,
    description: String
)

object CreateVisit {
  implicit val codec: JsonCodec[CreateVisit] = DeriveJsonCodec.gen[CreateVisit]
}
