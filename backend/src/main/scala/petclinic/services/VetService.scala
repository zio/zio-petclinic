package petclinic.services

import zio._
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
