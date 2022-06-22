package petclinic.services

import zio._
import petclinic.QuillContext
import petclinic.models.{OwnerId, Pet, PetId, Species}

import javax.sql.DataSource

final case class PetServiceLive(dataSource: DataSource) extends PetService {

  import QuillContext._

  implicit val encodeSpecies: MappedEncoding[Species, String] = MappedEncoding[Species, String](_.toString)
  implicit val decodeSpecies: MappedEncoding[String, Species] = MappedEncoding[String, Species](Species.fromString)

  override def create(name: String, birthdate: java.time.LocalDate, species: Species, ownerId: OwnerId): Task[Pet] =
    for {
      pet <- Pet.make(name, birthdate, species, ownerId)
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
