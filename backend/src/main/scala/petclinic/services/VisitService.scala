package petclinic.services

import petclinic.models._
import zio._
import zio.macros._

import java.time.LocalDate

/** VisitService manages the CRUD operations for the Visit type. Services like
  * this are responsible for persisting and modifying saved data. Note that the
  * "@accessible" macro annotation is used to add "accessors" to the companion
  * object for use in the corresponding spec.
  */
@accessible
trait VisitService {

  /** Creates a new Visit */
  def create(petId: PetId, date: LocalDate, description: String): Task[Visit]

  /** Deletes an existing Visit */
  def delete(id: VisitId): Task[Unit]

  /** Retrieves a Visit from the database */
  def get(id: VisitId): Task[Option[Visit]]

  /** Retrieves all Visits from the database */
  def getAll: Task[List[Visit]]

  /** Retrieves all Visits for a given Pet from the database */
  def getForPet(petId: PetId): Task[List[Visit]]

  /** Updates an existing Visit */
  def update(id: VisitId, date: Option[LocalDate], description: Option[String]): Task[Unit]

}
