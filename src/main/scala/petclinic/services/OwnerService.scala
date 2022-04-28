package petclinic.services

import petclinic.models.{Owner, OwnerId}
import zio.{Random, Task, URLayer, ZIO}
import petclinic.QuillContext

import javax.sql.DataSource

trait OwnerService {

  def create(firstName: String, lastName: String, address: String, phone: String): Task[Owner]
  def delete(id: OwnerId): Task[Unit]
  def get(id: OwnerId): Task[Option[Owner]]
  def getAll: Task[List[Owner]]
  def update(
      id: OwnerId,
      firstName: Option[String] = None,
      lastName: Option[String] = None,
      address: Option[String] = None,
      phone: Option[String] = None
  ): Task[Unit]

}

object OwnerService {

  def create(firstName: String, lastName: String, address: String, phone: String): ZIO[OwnerService, Throwable, Owner] =
    ZIO.serviceWithZIO[OwnerService](_.create(firstName, lastName, address, phone))

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
      phone: Option[String]
  ): ZIO[OwnerService, Throwable, Unit] =
    ZIO.serviceWithZIO[OwnerService](_.update(id, firstName, lastName, address, phone))

}

final case class OwnerServiceLive(random: Random, dataSource: DataSource) extends OwnerService {

  import QuillContext._

  override def create(firstName: String, lastName: String, address: String, phone: String): Task[Owner] =
    for {
      owner <- Owner.apply(firstName, lastName, address, phone).provideService(random)
      _     <- run(query[Owner].insertValue(lift(owner))).provideService(dataSource)
    } yield owner

  override def delete(id: OwnerId): Task[Unit] =
    run(query[Owner].filter(_.id == lift(id)).delete)
      .provideService(dataSource)
      .unit

  override def get(id: OwnerId): Task[Option[Owner]] =
    run(query[Owner].filter(_.id == lift(id)))
      .provideService(dataSource)
      .map(_.headOption)

  override def getAll: Task[List[Owner]] =
    run(query[Owner])
      .provideService(dataSource)
      .map(_.toList)

  override def update(
      id: OwnerId,
      firstName: Option[String],
      lastName: Option[String],
      address: Option[String],
      phone: Option[String]
  ): Task[Unit] =
    run(
      dynamicQuery[Owner]
        .filter(_.id == lift(id))
        .update(
          setOpt(_.firstName, firstName),
          setOpt(_.lastName, lastName),
          setOpt(_.address, address),
          setOpt(_.phone, phone)
        )
    )
      .provideService(dataSource)
      .unit

}

object OwnerServiceLive {
  val layer: URLayer[Random with DataSource, OwnerService] = (OwnerServiceLive.apply _).toLayer[OwnerService]
}
