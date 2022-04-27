package petclinic.models

import io.getquill.MappedEncoding
import zio.{Random, ZIO}
import zio.json._

import java.util.UUID

// wraps UUID as a specific PetId so that we cannot use an incorrect UUID
final case class PetId(id: UUID) extends AnyVal

object PetId {
  def random: ZIO[Random, Nothing, PetId] = Random.nextUUID.map(PetId(_))

  implicit val codec: JsonCodec[PetId] = JsonCodec[UUID].transform(PetId(_), _.id)
}

// extracts species to limit user selection
sealed trait Species {
  def name: String
}

object Species {

  case object Feline extends Species {
    override def name: String = "Feline"
  }

  case object Canine extends Species {
    override def name: String = "Canine"
  }

  case object Avia extends Species {
    override def name: String = "Avia"
  }

  case object Reptile extends Species {
    override def name: String = "Reptile"
  }

  implicit val encodeSpecies: MappedEncoding[Species, String] = MappedEncoding[Species, String](_.toString)
  implicit val decodeSpecies: MappedEncoding[String, Species] = MappedEncoding[String, Species](Species.fromString)

  def fromString(s: String): Species = s match {
    case "Feline"  => Feline
    case "Canine"  => Canine
    case "Avia"    => Avia
    case "Reptile" => Reptile
  }

  implicit val codec: JsonCodec[Species] = DeriveJsonCodec.gen[Species]

}

// represents a pet
final case class Pet(id: PetId, name: String, birthdate: java.time.LocalDate, species: Species, ownerId: OwnerId)

object Pet {

  def apply(
      name: String,
      birthdate: java.time.LocalDate,
      species: Species,
      ownerId: OwnerId
  ): ZIO[Random, Nothing, Pet] =
    PetId.random.map(Pet(_, name, birthdate, species, ownerId))

  implicit val codec: JsonCodec[Pet] = DeriveJsonCodec.gen[Pet]

}
