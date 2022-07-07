package petclinic.models.api

import petclinic.models.{OwnerId, Species}
import zio.json._

/** Models the parameters of a post request that the client will send to the
  * server while removing the need for the request to handle generating an
  * PetId.
  */
final case class CreatePet(name: String, birthdate: java.time.LocalDate, species: Species, ownerId: OwnerId)

/** Derives a JSON codec allowing the CreatePet request to be (de)serialized.
  */
object CreatePet {
  implicit val codec: JsonCodec[CreatePet] = DeriveJsonCodec.gen[CreatePet]
}
