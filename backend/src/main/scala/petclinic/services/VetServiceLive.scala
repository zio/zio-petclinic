package petclinic.services

import zio._
import petclinic.QuillContext
import petclinic.models.{Vet, VetId}

import javax.sql.DataSource

final case class VetServiceLive(dataSource: DataSource) extends VetService {

  import QuillContext._

  override def getAll: Task[List[Vet]] =
    run(query[Vet])
      .provideEnvironment(ZEnvironment(dataSource))

  override def get(vetId: VetId): Task[Option[Vet]] =
    run(query[Vet].filter(_.id == lift(vetId)))
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.headOption)
}

object VetServiceLive {

  val layer: URLayer[DataSource, VetService] =
    ZLayer.fromFunction(VetServiceLive.apply _)

}
