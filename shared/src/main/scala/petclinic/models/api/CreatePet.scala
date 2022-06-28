package petclinic.models.api

import petclinic.models.{OwnerId, Species}
import zio.json._

final case class CreatePet(
    name: String,
    birthdate: java.time.LocalDate,
    species: Species,
    ownerId: OwnerId
)

object CreatePet {
  implicit val codec: JsonCodec[CreatePet] = DeriveJsonCodec.gen[CreatePet]
}
