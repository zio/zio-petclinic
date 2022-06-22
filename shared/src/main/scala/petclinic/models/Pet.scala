package petclinic.models

import zio._
import zio.json._

final case class Pet(
    id: PetId,
    name: String,
    birthdate: java.time.LocalDate,
    species: Species,
    ownerId: OwnerId
)

object Pet {

  def make(
      name: String,
      birthdate: java.time.LocalDate,
      species: Species,
      ownerId: OwnerId
  ): UIO[Pet] =
    PetId.random.map(Pet(_, name, birthdate, species, ownerId))

  implicit val codec: JsonCodec[Pet] = DeriveJsonCodec.gen[Pet]

}
