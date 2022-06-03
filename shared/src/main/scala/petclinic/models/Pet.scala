package petclinic.models

import zio.json._
import zio.{Random, Task, UIO, ZIO}

import java.util.UUID

final case class PetId(id: UUID) extends AnyVal

object PetId {
  def random: UIO[PetId] = Random.nextUUID.map(PetId(_))

  def fromString(id: String): Task[PetId] =
    ZIO.attempt {
      PetId(UUID.fromString(id))
    }

  implicit val codec: JsonCodec[PetId] = JsonCodec[UUID].transform(PetId(_), _.id)
}

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

  case object Suidae extends Species {
    override def name: String = "Suidae"
  }

  def fromString(s: String): Species = s match {
    case "Feline"  => Feline
    case "Canine"  => Canine
    case "Avia"    => Avia
    case "Reptile" => Reptile
    case "Suidae"  => Suidae
  }

  val all: List[Species] = List(Feline, Canine, Avia, Reptile, Suidae)

  implicit val codec: JsonCodec[Species] = DeriveJsonCodec.gen[Species]

}

final case class Pet(
    id: PetId,
    name: String,
    birthdate: java.time.LocalDate,
    species: Species,
    ownerId: OwnerId
)

object Pet {

  def apply(
      name: String,
      birthdate: java.time.LocalDate,
      species: Species,
      ownerId: OwnerId
  ): UIO[Pet] =
    PetId.random.map(Pet(_, name, birthdate, species, ownerId))

  implicit val codec: JsonCodec[Pet] = DeriveJsonCodec.gen[Pet]

}

// API Models

final case class CreatePet(
    name: String,
    birthdate: java.time.LocalDate,
    species: Species,
    ownerId: OwnerId
)

object CreatePet {
  implicit val codec: JsonCodec[CreatePet] = DeriveJsonCodec.gen[CreatePet]
}

final case class UpdatePet(
    name: Option[String],
    birthdate: Option[java.time.LocalDate],
    species: Option[Species],
    ownerId: Option[OwnerId]
)

object UpdatePet {
  implicit val codec: JsonCodec[UpdatePet] = DeriveJsonCodec.gen[UpdatePet]
}

final case class CreateVisit(
    date: java.time.LocalDate,
    description: String
)

object CreateVisit {
  implicit val codec: JsonCodec[CreateVisit] = DeriveJsonCodec.gen[CreateVisit]
}

final case class UpdateVisit(date: Option[java.time.LocalDate], description: Option[String], petId: PetId)

object UpdateVisit {
  implicit val codec: JsonCodec[UpdateVisit] = DeriveJsonCodec.gen[UpdateVisit]
}
