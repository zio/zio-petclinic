package petclinic.services

import petclinic.QuillContext
import petclinic.models.{OwnerId, Pet, PetId, Species}
import zio._
import zio.metrics._
import io.getquill._

import javax.sql.DataSource

/** PetServiceLive is a service which provides the "live" implementation of the
  * PetService. This implementation uses a DataSource, which will concretely be
  * a connection pool.
  */
final case class PetServiceLive(dataSource: DataSource) extends PetService {

  // QuillContext needs to be imported here to expose the methods in the QuillContext object.
  import QuillContext._

  /** `encodeSpecies` and `decodeSpecies` are helper functions used to convert
    * Species to strings and strings to Species respectively.
    */
  implicit val encodeSpecies: MappedEncoding[Species, String] = MappedEncoding[Species, String](_.toString)
  implicit val decodeSpecies: MappedEncoding[String, Species] = MappedEncoding[String, Species](Species.fromString)

  /** `create` uses the Pet model's `make` method to create a new Pet. The Pet
    * is formatted into a query string, then inserted into the database using
    * `provideEnvironment` to provide the datasource to the effect returned by
    * `run`. The created Pet is returned.
    */
  override def create(name: String, birthdate: java.time.LocalDate, species: Species, ownerId: OwnerId): Task[Pet] =
    for {
      pet <- Pet.make(name, birthdate, species, ownerId)
      _   <- run(query[Pet].insertValue(lift(pet))).provideEnvironment(ZEnvironment(dataSource))
      _   <- Metric.counter("pet.created").increment
    } yield pet

  /** `delete` uses `filter` to find a Pet in the database whose ID matches the
    * one provided and deletes it.
    *
    * Unit is returned to indicate that we are running this method for its side
    * effects, a deleted Pet gives us no information. This will either fail or
    * succeed.
    */
  override def delete(id: PetId): Task[Unit] =
    run(query[Pet].filter(_.id == lift(id)).delete)
      .provideEnvironment(ZEnvironment(dataSource))
      .unit

  /** `get` uses `filter` to find a Pet in the database whose ID matches the one
    * provided and returns it.
    */
  override def get(id: PetId): Task[Option[Pet]] =
    run(query[Pet].filter(_.id == lift(id)))
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.headOption)

  /** `getForOwner` uses `filter` to find all Pets in the database whose OwnerId
    * matches the one provided, returning all Pets associated with a given
    * Owner.
    */
  override def getForOwner(ownerId: OwnerId): Task[List[Pet]] =
    run(query[Pet].filter(_.ownerId == lift(ownerId)).sortBy(_.birthdate))
      .provideEnvironment(ZEnvironment(dataSource))

  /** `getAll` uses `query` to find all entries in the database of type Pet and
    * returns them.
    */
  override def getAll: Task[List[Pet]] =
    run(query[Pet].sortBy(_.birthdate))
      .provideEnvironment(ZEnvironment(dataSource))

  /** `update` uses `filter` to find a Pet in the database whose ID matches the
    * one provided and updates it with the provided optional values.
    *
    * Because a user may not provide all optional values, `setOpt` is used to
    * preserve the existing value in the case one is not provided to replace it.
    * Unit is returned to indicate side-effecting code. Note that this
    * `dynamicQuery` is not generated at compile time.
    *
    * For more information on dynamic queries, see:
    * https://getquill.io/#writing-queries-dynamic-queries
    */
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

/** Here in the companion object we define the layer that provides the live
  * implementation of the PetService.
  */
object PetServiceLive {

  val layer: URLayer[DataSource, PetService] = ZLayer.fromFunction(PetServiceLive.apply _)

}
