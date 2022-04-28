package petclinic.services

import petclinic.models.{Appt, ApptId, VetId}
import zio.{Random, Task, ZIO}
import petclinic.QuillContext

import javax.sql.DataSource

trait ApptService {

  def create(date: java.time.LocalDateTime, description: String, vetId: VetId): Task[Appt]
  def delete(id: ApptId): Task[Unit]
  def get(id: ApptId): Task[Option[Appt]]
  def getAll: Task[List[Appt]]
  def update(id: ApptId, date: Option[java.time.LocalDateTime], description: Option[String]): Task[Unit]

}

object ApptService {

  def create(date: java.time.LocalDateTime, description: String, vetId: VetId): ZIO[ApptService, Throwable, Appt] =
    ZIO.serviceWithZIO[ApptService](_.create(date, description, vetId))

  def delete(id: ApptId): ZIO[ApptService, Throwable, Unit] = ZIO.serviceWithZIO[ApptService](_.delete(id))

  def get(id: ApptId): ZIO[ApptService, Throwable, Option[Appt]] = ZIO.serviceWithZIO[ApptService](_.get(id))

  def getAll: ZIO[ApptService, Throwable, List[Appt]] = ZIO.serviceWithZIO[ApptService](_.getAll)

  def update(
      id: ApptId,
      date: Option[java.time.LocalDateTime],
      description: Option[String]
  ): ZIO[ApptService, Throwable, Unit] =
    ZIO.serviceWithZIO[ApptService](_.update(id, date, description))

}

final case class ApptServiceLive(random: Random, dataSource: DataSource) extends ApptService {

  import QuillContext._

  def create(date: java.time.LocalDateTime, description: String, vetId: VetId): Task[Appt] =
    for {
      appt <- Appt.apply(date, description, vetId).provideService(random)
      _    <- run(query[Appt].insertValue(lift(appt))).provideService(dataSource)
    } yield appt

  def delete(id: ApptId): Task[Unit] =
    run(query[Appt].filter(_.id == lift(id)).delete)
      .provideService(dataSource)
      .unit

  def get(id: ApptId): Task[Option[Appt]] =
    run(query[Appt].filter(_.id == lift(id)))
      .provideService(dataSource)
      .map(_.headOption)

  def getAll: Task[List[Appt]] =
    run(query[Appt])
      .provideService(dataSource)
      .map(_.toList)

  def update(id: ApptId, date: Option[java.time.LocalDateTime], description: Option[String]): Task[Unit] =
    run(dynamicQuery[Appt].filter(_.id == lift(id)).update(setOpt(_.date, date), setOpt(_.description, description)))
      .provideService(dataSource)
      .unit

}
