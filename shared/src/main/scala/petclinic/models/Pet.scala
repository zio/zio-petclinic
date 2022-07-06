package petclinic.models

import zio._
import zio.json._

/** Pet defines what pieces of data a Pet is comprised of. This data type models
  * what we expect to be defined in the database.
  */
final case class Pet(
    id: PetId,
    name: String,
    birthdate: java.time.LocalDate,
    species: Species,
    ownerId: OwnerId
)

object Pet {

  /** Uses the random method defined on our PetId wrapper to generate a random
    * ID and assign that to the Pet we are creating.
    */
  def make(
      name: String,
      birthdate: java.time.LocalDate,
      species: Species,
      ownerId: OwnerId
  ): UIO[Pet] =
    PetId.random.map(Pet(_, name, birthdate, species, ownerId))

  /** Generates a JSON codec for the Pet type allowing it to be (de)serialized
    */
  implicit val codec: JsonCodec[Pet] = DeriveJsonCodec.gen[Pet]

}
