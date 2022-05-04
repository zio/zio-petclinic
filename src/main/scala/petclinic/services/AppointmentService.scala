package petclinic.services

import petclinic.models.{Appointment, AppointmentId, PetId, VetId}
import zio.{Random, Task, URLayer, ZIO}
import petclinic.QuillContext

import javax.sql.DataSource

trait AppointmentService {

  def create(petId: PetId, date: java.time.LocalDateTime, description: String): Task[Appointment]

  def delete(id: AppointmentId): Task[Unit]

  def get(id: AppointmentId): Task[Option[Appointment]]

  def getAll: Task[List[Appointment]]

  def update(id: AppointmentId, date: Option[java.time.LocalDateTime], description: Option[String]): Task[Unit]

}

object AppointmentService {

  def create(
      petId: PetId,
      date: java.time.LocalDateTime,
      description: String
  ): ZIO[AppointmentService, Throwable, Appointment] =
    ZIO.serviceWithZIO[AppointmentService](_.create(petId, date, description))

  def delete(id: AppointmentId): ZIO[AppointmentService, Throwable, Unit] =
    ZIO.serviceWithZIO[AppointmentService](_.delete(id))

  def get(id: AppointmentId): ZIO[AppointmentService, Throwable, Option[Appointment]] =
    ZIO.serviceWithZIO[AppointmentService](_.get(id))

  def getAll: ZIO[AppointmentService, Throwable, List[Appointment]] = ZIO.serviceWithZIO[AppointmentService](_.getAll)

  def update(
      id: AppointmentId,
      date: Option[java.time.LocalDateTime],
      description: Option[String]
  ): ZIO[AppointmentService, Throwable, Unit] =
    ZIO.serviceWithZIO[AppointmentService](_.update(id, date, description))

}

final case class AppointmentServiceLive(random: Random, dataSource: DataSource, vetService: VetService)
    extends AppointmentService {

  import QuillContext._

  override def create(
      petId: PetId,
      date: java.time.LocalDateTime,
      description: String
  ): Task[Appointment] =
    for {
      vets        <- vetService.getAll
      id           = vets(scala.util.Random.nextInt(vets.length)).id
      appointment <- Appointment.apply(petId, date, description, id).provideService(random)
      _           <- run(query[Appointment].insertValue(lift(appointment))).provideService(dataSource)
    } yield appointment

  override def delete(id: AppointmentId): Task[Unit] =
    run(query[Appointment].filter(_.id == lift(id)).delete)
      .provideService(dataSource)
      .unit

  override def get(id: AppointmentId): Task[Option[Appointment]] =
    run(query[Appointment].filter(_.id == lift(id)))
      .provideService(dataSource)
      .map(_.headOption)

  override def getAll: Task[List[Appointment]] =
    run(query[Appointment])
      .provideService(dataSource)
      .map(_.toList)

  override def update(
      id: AppointmentId,
      date: Option[java.time.LocalDateTime],
      description: Option[String]
  ): Task[Unit] =
    run(
      dynamicQuery[Appointment]
        .filter(_.id == lift(id))
        .update(setOpt(_.date, date), setOpt(_.description, description))
    )
      .provideService(dataSource)
      .unit

}

object AppointmentServiceLive {

  val layer: URLayer[Random with DataSource with VetService, AppointmentService] =
    (AppointmentServiceLive.apply _).toLayer[AppointmentService]

}
