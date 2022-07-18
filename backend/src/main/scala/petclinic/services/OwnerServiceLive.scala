package petclinic.services

import petclinic.QuillContext
import petclinic.models.{Owner, OwnerId}
import zio._

import javax.sql.DataSource

/** OwnerServiceLive is a service which provides the "live" implementation of
  * the OwnerService. This implementation uses a DataSource, which will
  * concretely be a connection pool.
  */
final case class OwnerServiceLive(dataSource: DataSource) extends OwnerService {

  /** QuillContext needs to be imported here to expose the methods in the
    * QuillContext object.
    */
  import QuillContext._

  /** `create` uses the Owner model's `make` method to create a new Owner. The
    * Owner is formatted into a query string, then inserted into the database
    * using `provideEnvironment` to provide the datasource to the effect
    * returned by `run`. The created Owner is returned.
    */
  override def create(firstName: String, lastName: String, address: String, phone: String, email: String): Task[Owner] =
    for {
      owner <- Owner.make(firstName, lastName, address, phone, email)
      _     <- run(query[Owner].insertValue(lift(owner))).provideEnvironment(ZEnvironment(dataSource))
    } yield owner

  /** `delete` uses `filter` to find an Owner in the database whose ID matches
    * the one provided and deletes it.
    *
    * Unit is returned to indicate that we are running this method for its side
    * effects, a deleted Owner gives us no information. This will either fail or
    * succeed.
    */
  override def delete(id: OwnerId): Task[Unit] =
    run(query[Owner].filter(_.id == lift(id)).delete)
      .provideEnvironment(ZEnvironment(dataSource))
      .unit

  /** `get` uses `filter` to find an Owner in the database whose ID matches the
    * one provided and returns it.
    */
  override def get(id: OwnerId): Task[Option[Owner]] =
    run(query[Owner].filter(_.id == lift(id)))
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.headOption)

  /** `getAll` uses `query` to find all entries in the database of type Owner
    * and returns them.
    */
  override def getAll: Task[List[Owner]] =
    run(query[Owner])
      .provideEnvironment(ZEnvironment(dataSource))

  /** `update` uses `filter` to find an Owner in the database whose ID matches
    * the one provided and updates it with the provided optional values.
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
      id: OwnerId,
      firstName: Option[String],
      lastName: Option[String],
      address: Option[String],
      phone: Option[String],
      email: Option[String]
  ): Task[Unit] =
    run(
      dynamicQuery[Owner]
        .filter(_.id == lift(id))
        .update(
          setOpt(_.firstName, firstName),
          setOpt(_.lastName, lastName),
          setOpt(_.address, address),
          setOpt(_.phone, phone),
          setOpt(_.email, email)
        )
    )
      .provideEnvironment(ZEnvironment(dataSource))
      .unit

}

/** Here in the companion object we define the layer that provides the live
  * implementation of the OwnerService.
  */
object OwnerServiceLive {

  val layer: URLayer[DataSource, OwnerService] = ZLayer.fromFunction(OwnerServiceLive.apply _)

}
