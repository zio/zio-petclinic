package petclinic.services

import zio._
import petclinic.models._

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

object OwnerService {

  def create(
      firstName: String,
      lastName: String,
      address: String,
      phone: String,
      email: String
  ): ZIO[OwnerService, Throwable, Owner] =
    ZIO.serviceWithZIO[OwnerService](_.create(firstName, lastName, address, phone, email))

  def delete(id: OwnerId): ZIO[OwnerService, Throwable, Unit] =
    ZIO.serviceWithZIO[OwnerService](_.delete(id))

  def get(id: OwnerId): ZIO[OwnerService, Throwable, Option[Owner]] =
    ZIO.serviceWithZIO[OwnerService](_.get(id))

  def getAll: ZIO[OwnerService, Throwable, List[Owner]] =
    ZIO.service[OwnerService].flatMap(_.getAll)

  def update(
      id: OwnerId,
      firstName: Option[String],
      lastName: Option[String],
      address: Option[String],
      phone: Option[String],
      email: Option[String]
  ): ZIO[OwnerService, Throwable, Unit] =
    ZIO.serviceWithZIO[OwnerService](_.update(id, firstName, lastName, address, phone, email))

}
