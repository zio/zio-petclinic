package petclinic.services

import zio.{Task, URLayer, ZEnvironment, ZIO, ZLayer}
import petclinic.QuillContext
import petclinic.models._

import javax.sql.DataSource

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

final case class PetServiceLive(dataSource: DataSource) extends PetService {

  import QuillContext._

  implicit val encodeSpecies: MappedEncoding[Species, String] = MappedEncoding[Species, String](_.toString)
  implicit val decodeSpecies: MappedEncoding[String, Species] = MappedEncoding[String, Species](Species.fromString)

  override def create(name: String, birthdate: java.time.LocalDate, species: Species, ownerId: OwnerId): Task[Pet] =
    for {
      pet <- Pet.apply(name, birthdate, species, ownerId)
      _   <- run(query[Pet].insertValue(lift(pet))).provideEnvironment(ZEnvironment(dataSource))
    } yield pet

  override def delete(id: PetId): Task[Unit] =
    run(query[Pet].filter(_.id == lift(id)).delete)
      .provideEnvironment(ZEnvironment(dataSource))
      .unit

  override def get(id: PetId): Task[Option[Pet]] =
    run(query[Pet].filter(_.id == lift(id)))
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.headOption)

  override def getForOwner(ownerId: OwnerId): Task[List[Pet]] =
    run(query[Pet].filter(_.ownerId == lift(ownerId)).sortBy(_.birthdate))
      .provideEnvironment(ZEnvironment(dataSource))

  override def getAll: Task[List[Pet]] =
    run(query[Pet].sortBy(_.birthdate))
      .provideEnvironment(ZEnvironment(dataSource))
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
      .provideEnvironment(ZEnvironment(dataSource))
      .unit

}

object PetServiceLive {

  val layer: URLayer[DataSource, PetService] = ZLayer.fromFunction(PetServiceLive.apply _)

}
