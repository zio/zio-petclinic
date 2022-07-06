package petclinic.services

import petclinic.QuillContext
import petclinic.models.{PetId, Visit, VisitId}
import zio._

import javax.sql.DataSource

/** VisitServiceLive is a service which provides the "live" implementation of
  * the VisitService. This implementation uses a DataSource, which will
  * concretely be a connection pool.
  */
final case class VisitServiceLive(dataSource: DataSource, vetService: VetService) extends VisitService {

  // QuillContext needs to be imported here to expose the methods in the QuillContext object.
  import QuillContext._

  /** create uses the getAll method defined in the VetService to retrieve a list
    * of all the vets in the database and randomly selects one. It then uses the
    * make method defined in the Visit model to create a new Visit. The Visit is
    * formatted into a query string, then inserted into the database using
    * provideEnvironment to provide the datasource to the effect returned by
    * run. The created Visit is returned.
    */
  override def create(
      petId: PetId,
      date: java.time.LocalDate,
      description: String
  ): Task[Visit] =
    for {
      vets  <- vetService.getAll
      id     = vets(scala.util.Random.nextInt(vets.length)).id
      visit <- Visit.make(petId, date, description, id)
      _     <- run(query[Visit].insertValue(lift(visit))).provideEnvironment(ZEnvironment(dataSource))
    } yield visit

  /** delete uses the filter method to find a Visit in the database with an ID
    * that matches the one provided and deletes it. Unit is returned to indicate
    * that we are running this method for its side effects, a deleted Visit
    * gives us no information. This will either fail or succeed.
    */
  override def delete(id: VisitId): Task[Unit] =
    run(query[Visit].filter(_.id == lift(id)).delete)
      .provideEnvironment(ZEnvironment(dataSource))
      .unit

  /** get uses the filter method to find a Visit in the database with an ID tbat
    * matches the one provided and returns it.
    */
  override def get(id: VisitId): Task[Option[Visit]] =
    run(query[Visit].filter(_.id == lift(id)))
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.headOption)

  /** getForPet uses the filter method to find all Visits in the database with a
    * PetId that matches the one provided, returning all Visits associated with
    * a given Pet.
    */
  override def getForPet(petId: PetId): Task[List[Visit]] =
    run(query[Visit].filter(_.petId == lift(petId)).sortBy(_.date))
      .provideEnvironment(ZEnvironment(dataSource))

  /** getAll uses the query method to find all entries in the database of type
    * Visit and returns them.
    */
  override def getAll: Task[List[Visit]] =
    run(query[Visit])
      .provideEnvironment(ZEnvironment(dataSource))

  /** update uses the filter method to find a Visit in the database with an ID
    * that matches the one provided and updates it with the provided optional
    * values. Because a user may not provide all optional values, setOpt is used
    * to preserve the existing value in the case one is not provided to replace
    * it. Unit is returned to indicate side-effecting code. Note that this
    * dynamicQuery is not generated at compile time.
    * https://getquill.io/#writing-queries-dynamic-queries
    */
  override def update(
      id: VisitId,
      date: Option[java.time.LocalDate],
      description: Option[String]
  ): Task[Unit] =
    run(
      dynamicQuery[Visit]
        .filter(_.id == lift(id))
        .update(setOpt(_.date, date), setOpt(_.description, description))
    )
      .provideEnvironment(ZEnvironment(dataSource))
      .unit

}

/** Here in the companion object we define the layer that provides the live
  * implementation of the VisitService.
  */
object VisitServiceLive {

  val layer: URLayer[DataSource with VetService, VisitService] =
    ZLayer.fromFunction(VisitServiceLive.apply _)

}
