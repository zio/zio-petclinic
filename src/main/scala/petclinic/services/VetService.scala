package petclinic.services

import petclinic.models.Vet
import zio.{Function1ToLayerOps, Task, URLayer, ZIO}

import javax.sql.DataSource
import petclinic.QuillContext

trait VetService {
  def getAll: Task[List[Vet]]
}

object VetService {
  def getAll: ZIO[VetService, Throwable, List[Vet]] =
    ZIO.serviceWithZIO[VetService](_.getAll)
}

final case class VetServiceLive(dataSource: DataSource) extends VetService {

  import QuillContext._

  override def getAll: Task[List[Vet]] =
    run(query[Vet])
      .provideService(dataSource)
      .map(_.toList)
}

object VetServiceLive {
  val layer: URLayer[DataSource, VetService] =
    (VetServiceLive.apply _).toLayer[VetService]
}
