package petclinic.models.api

import petclinic.models.{OwnerId, Species}
import zio.json._

/** Models the parameters of a patch request that the client will send to the
  * server while removing the need for the request to handle generating an
  * PetId.
  */
final case class UpdatePet(
    name: Option[String],
    birthdate: Option[java.time.LocalDate],
    species: Option[Species],
    ownerId: Option[OwnerId]
)

/** Derives a JSON codec allowing the UpdatePet request to be (de)serialized.
  */
object UpdatePet {
  implicit val codec: JsonCodec[UpdatePet] = DeriveJsonCodec.gen[UpdatePet]
}
