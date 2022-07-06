package petclinic.services

import petclinic.QuillContext
import petclinic.models.{Vet, VetId}
import zio._

import javax.sql.DataSource

/** VetServiceLive is a service which provides the "live" implementation of the
  * VetService. This implementation uses a DataSource, which will concretely be
  * a connection pool.
  */
final case class VetServiceLive(dataSource: DataSource) extends VetService {

  // QuillContext needs to be imported here to expose the methods in the QuillContext object.
  import QuillContext._

  /** get uses the filter method to find a Vet in the database whose ID matches
    * the one provided and returns it.
    */
  override def get(vetId: VetId): Task[Option[Vet]] =
    run(query[Vet].filter(_.id == lift(vetId)))
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.headOption)

  /** getAll uses the query method to find all entries in the database of type
    * Vet and returns them.
    */
  override def getAll: Task[List[Vet]] =
    run(query[Vet])
      .provideEnvironment(ZEnvironment(dataSource))

}

/** Here in the companion object we define the layer that provides the live
  * implementation of the VetService.
  */
object VetServiceLive {

  val layer: URLayer[DataSource, VetService] =
    ZLayer.fromFunction(VetServiceLive.apply _)

}
