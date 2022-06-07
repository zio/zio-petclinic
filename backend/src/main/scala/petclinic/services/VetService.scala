package petclinic.services

import zio._

import javax.sql.DataSource
import petclinic.QuillContext
import petclinic.models._

trait VetService {
  def getAll: Task[List[Vet]]
  def get(vetId: VetId): Task[Option[Vet]]
}

object VetService {
  def getAll: ZIO[VetService, Throwable, List[Vet]] =
    ZIO.serviceWithZIO[VetService](_.getAll)

  def get(vetId: VetId): ZIO[VetService, Throwable, Option[Vet]] =
    ZIO.serviceWithZIO[VetService](_.get(vetId))
}

final case class VetServiceLive(dataSource: DataSource) extends VetService {

  import QuillContext._

  override def getAll: Task[List[Vet]] =
    run(query[Vet])
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.toList)

  override def get(vetId: VetId): Task[Option[Vet]] =
    run(query[Vet].filter(_.id == lift(vetId)))
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.headOption)
}

object VetServiceLive {
  val layer: URLayer[DataSource, VetService] =
    ZLayer.fromFunction(VetServiceLive.apply _)
}
