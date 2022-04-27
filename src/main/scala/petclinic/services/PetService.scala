package petclinic.services

import petclinic.QuillContext
import petclinic.models.{OwnerId, Pet, PetId, Species}
import zio._

import javax.sql.DataSource

trait PetService {

  def create(name: String, birthdate: java.time.LocalDate, species: Species, ownerId: OwnerId): Task[Pet]
  def delete(id: PetId): Task[Unit]
  def get(id: PetId): Task[Option[Pet]]
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

final case class PetServiceLive(random: Random, dataSource: DataSource) extends PetService {

  import QuillContext._

  override def create(name: String, birthdate: java.time.LocalDate, species: Species, ownerId: OwnerId): Task[Pet] =
    for {
      pet <- Pet.apply(name, birthdate, species, ownerId).provideService(random)
      _   <- run(query[Pet].insertValue(lift(pet))).provideService(dataSource)
    } yield pet

  override def delete(id: PetId): Task[Unit] =
    run(query[Pet].filter(_.id == lift(id)).delete)
      .provideService(dataSource)
      .unit

  override def get(id: PetId): Task[Option[Pet]] =
    run(query[Pet].filter(_.id == lift(id)))
      .provideService(dataSource)
      .map(_.headOption)

  override def getAll: Task[List[Pet]] =
    run(query[Pet])
      .provideService(dataSource)
      .map(_.toList)

  override def update(
      id: PetId,
      name: Option[String],
      birthdate: Option[java.time.LocalDate],
      species: Option[Species],
      ownerId: Option[OwnerId]
  ): Task[Unit] =
    run(
      dynamicQuery[Pet]
        .filter(_.id == lift(id))
        .update(
          setOpt(_.name, name),
          setOpt(_.birthdate, birthdate),
          setOpt(_.species, species),
          setOpt(_.ownerId, ownerId)
        )
    )
      .provideService(dataSource)
      .unit

}
