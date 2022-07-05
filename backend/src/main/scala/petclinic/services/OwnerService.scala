package petclinic.services

import petclinic.models._
import zio._
import zio.macros._

/** OwnerService manages the CRUD operations for the Owner type. Services like
  * this are responsible for persisting and modifying saved data. Note that the
  * "@accessible" macro annotation is used to add "accessors" to the companion
  * object for use in the corresponding spec.
  */
@accessible
trait OwnerService {

  /** Creates a new Owner
    */
  def create(firstName: String, lastName: String, address: String, phone: String, email: String): Task[Owner]

  /** Deletes an existing Owner
    */
  def delete(id: OwnerId): Task[Unit]

  /** Retreives an Owner from the database
    */
  def get(id: OwnerId): Task[Option[Owner]]

  /** Retreives all Owners from the database
    */
  def getAll: Task[List[Owner]]

  /** Updates an existing Owner
    */
  def update(
      id: OwnerId,
      firstName: Option[String] = None,
      lastName: Option[String] = None,
      address: Option[String] = None,
      phone: Option[String] = None,
      email: Option[String] = None
  ): Task[Unit]

}
