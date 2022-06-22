package petclinic.services

import zio._
import petclinic.QuillContext
import petclinic.models.{Owner, OwnerId}

import javax.sql.DataSource

final case class OwnerServiceLive(dataSource: DataSource) extends OwnerService {

  import QuillContext._

  override def create(firstName: String, lastName: String, address: String, phone: String, email: String): Task[Owner] =
    for {
      owner <- Owner.make(firstName, lastName, address, phone, email)
      _     <- run(query[Owner].insertValue(lift(owner))).provideEnvironment(ZEnvironment(dataSource))
    } yield owner

  override def delete(id: OwnerId): Task[Unit] =
    run(query[Owner].filter(_.id == lift(id)).delete)
      .provideEnvironment(ZEnvironment(dataSource))
      .unit

  override def get(id: OwnerId): Task[Option[Owner]] =
    run(query[Owner].filter(_.id == lift(id)))
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.headOption)

  override def getAll: Task[List[Owner]] =
    run(query[Owner])
      .provideEnvironment(ZEnvironment(dataSource))

  override def update(
      id: OwnerId,
      firstName: Option[String],
      lastName: Option[String],
      address: Option[String],
      phone: Option[String],
      email: Option[String]
  ): Task[Unit] =
    run(
      dynamicQuery[Owner]
        .filter(_.id == lift(id))
        .update(
          setOpt(_.firstName, firstName),
          setOpt(_.lastName, lastName),
          setOpt(_.address, address),
          setOpt(_.phone, phone),
          setOpt(_.email, email)
        )
    )
      .provideEnvironment(ZEnvironment(dataSource))
      .unit

}

object OwnerServiceLive {

  val layer: URLayer[DataSource, OwnerService] = ZLayer.fromFunction(OwnerServiceLive.apply _)

}
