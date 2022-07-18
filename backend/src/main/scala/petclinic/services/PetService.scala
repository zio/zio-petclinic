package petclinic.services

import petclinic.models._
import zio._
import zio.macros._

import java.time.LocalDate

/** PetService manages the CRUD operations for the Pet type.
  *
  * Services like this are responsible for persisting and modifying saved data.
  * Note that the `@accessible` macro annotation is used to add "accessors" to
  * the companion object for use in the corresponding spec.
  */
@accessible
trait PetService {

  /** Creates a new Pet. */
  def create(name: String, birthdate: LocalDate, species: Species, ownerId: OwnerId): Task[Pet]

  /** Deletes an existing Pet. */
  def delete(id: PetId): Task[Unit]

  /** Retrieves a Pet from the database. */
  def get(id: PetId): Task[Option[Pet]]

  /** Retrieves all Pets for a given Owner from the database. */
  def getForOwner(ownerId: OwnerId): Task[List[Pet]]

  /** Retrieves all Pets from the database. */
  def getAll: Task[List[Pet]]

  /** Updates an existing Pet. */
  def update(
      id: PetId,
      name: Option[String],
      birthdate: Option[LocalDate],
      species: Option[Species],
      ownerId: Option[OwnerId]
  ): Task[Unit]

}
