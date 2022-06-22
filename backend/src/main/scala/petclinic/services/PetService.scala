package petclinic.services

import zio._
import petclinic.models._

trait PetService {

  def create(name: String, birthdate: java.time.LocalDate, species: Species, ownerId: OwnerId): Task[Pet]

  def delete(id: PetId): Task[Unit]

  def get(id: PetId): Task[Option[Pet]]

  def getForOwner(ownerId: OwnerId): Task[List[Pet]]

  def getAll: Task[List[Pet]]

  def update(
      id: PetId,
      name: Option[String],
      birthdate: Option[java.time.LocalDate],
      species: Option[Species],
      ownerId: Option[OwnerId]
  ): Task[Unit]

}

object PetService {

  def create(
      name: String,
      birthdate: java.time.LocalDate,
      species: Species,
      ownerId: OwnerId
  ): ZIO[PetService, Throwable, Pet] =
    ZIO.serviceWithZIO[PetService](_.create(name, birthdate, species, ownerId))

  def delete(id: PetId): ZIO[PetService, Throwable, Unit] =
    ZIO.serviceWithZIO[PetService](_.delete(id))

  def get(id: PetId): ZIO[PetService, Throwable, Option[Pet]] =
    ZIO.serviceWithZIO[PetService](_.get(id))

  def getForOwner(ownerId: OwnerId): ZIO[PetService, Throwable, List[Pet]] =
    ZIO.serviceWithZIO[PetService](_.getForOwner(ownerId))

  def getAll: ZIO[PetService, Throwable, List[Pet]] =
    ZIO.serviceWithZIO[PetService](_.getAll)

  def update(
      id: PetId,
      name: Option[String],
      birthdate: Option[java.time.LocalDate],
      species: Option[Species],
      ownerId: Option[OwnerId]
  ): ZIO[PetService, Throwable, Unit] =
    ZIO.serviceWithZIO[PetService](_.update(id, name, birthdate, species, ownerId))

}
