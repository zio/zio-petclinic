package petclinic.services

import zio._
import zio.macros._
import petclinic.models._

@accessible
trait OwnerService {

  def create(firstName: String, lastName: String, address: String, phone: String, email: String): Task[Owner]

  def delete(id: OwnerId): Task[Unit]

  def get(id: OwnerId): Task[Option[Owner]]

  def getAll: Task[List[Owner]]

  def update(
      id: OwnerId,
      firstName: Option[String] = None,
      lastName: Option[String] = None,
      address: Option[String] = None,
      phone: Option[String] = None,
      email: Option[String] = None
  ): Task[Unit]

}
