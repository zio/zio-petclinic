package petclinic.services

import petclinic.models._
import zio._

/** VetService manages the CRUD operations for the Vet type. Services like this
  * are responsible for persisting and modifying saved data. In this case, the
  * VetService has notably fewer methods since we do not allow users to interact
  * with Vet data and are, instead just displaying a list of predetermined Vets
  * and randomly assigning a Vet to a created Visit.
  */
trait VetService {

  /** Retrieves a Vet from the database. */
  def get(vetId: VetId): Task[Option[Vet]]

  /** Retrieves all Vets from the database. */
  def getAll: Task[List[Vet]]

}
