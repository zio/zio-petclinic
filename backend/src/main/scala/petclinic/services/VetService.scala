package petclinic.services

import zio._
import petclinic.models._

trait VetService {

  def getAll: Task[List[Vet]]

  def get(vetId: VetId): Task[Option[Vet]]

}
