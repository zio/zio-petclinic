package petclinic.services

import zio._
import zio.macros._
import petclinic.models._
import java.time.LocalDate

@accessible
trait VisitService {

  def create(petId: PetId, date: LocalDate, description: String): Task[Visit]

  def delete(id: VisitId): Task[Unit]

  def get(id: VisitId): Task[Option[Visit]]

  def getAll: Task[List[Visit]]

  def getForPet(petId: PetId): Task[List[Visit]]

  def update(id: VisitId, date: Option[LocalDate], description: Option[String]): Task[Unit]

}
