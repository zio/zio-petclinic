package petclinic.services

import petclinic.models.Vet
import zio._

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
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.toList)
}

object VetServiceLive {
  val layer: URLayer[DataSource, VetService] =
    ZLayer.fromFunction(VetServiceLive.apply _)
}
