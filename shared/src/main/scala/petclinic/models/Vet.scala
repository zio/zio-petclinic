package petclinic.models

import zio.json._

/** Vet defines what pieces of data a Vet is comprised of.
  *
  * This data type models what we expect to be defined in the database.
  */
final case class Vet(id: VetId, lastName: String, specialty: String)

object Vet {

  /** Derives a JSON codec for the Vet type allowing it to be (de)serialized.
    */
  implicit val codec: JsonCodec[Vet] = DeriveJsonCodec.gen[Vet]
}
