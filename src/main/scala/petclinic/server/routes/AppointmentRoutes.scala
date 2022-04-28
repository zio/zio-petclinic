package petclinic.server.routes

import petclinic.models.{AppointmentId, PetId, VetId}
import petclinic.services.AppointmentService
import zhttp.http._
import zio.ZIO
import zio.json._

object AppointmentRoutes {

  final case class CreateAppointment(petId: PetId, date: java.time.LocalDateTime, description: String, vetId: VetId)

  object CreateAppointment {

    implicit val codec: JsonCodec[CreateAppointment] = DeriveJsonCodec.gen[CreateAppointment]
  }

  final case class UpdateAppointment(
      id: AppointmentId,
      date: Option[java.time.LocalDateTime],
      description: Option[String]
  )

  object UpdateAppointment {

    implicit val codec: JsonCodec[UpdateAppointment] = DeriveJsonCodec.gen[UpdateAppointment]
  }

  val routes: Http[AppointmentService, Throwable, Request, Response] =
    Http.collectZIO[Request] {

      case Method.GET -> !! / "appts" / id =>
        for {
          id   <- AppointmentId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid appt id"))
          appt <- AppointmentService.get(id)
        } yield Response.json(appt.toJson)

      case Method.GET -> !! / "appts" =>
        AppointmentService.getAll.map(appts => Response.json(appts.toJson))

      case req @ Method.POST -> !! / "appts" =>
        for {
          body       <- req.bodyAsString.orElseFail(AppError.MissingBodyError)
          createAppt <- ZIO.from(body.fromJson[CreateAppointment]).mapError(AppError.JsonDecodingError)
          appt       <- AppointmentService.create(createAppt.petId, createAppt.date, createAppt.description, createAppt.vetId)
        } yield Response.json(appt.toJson)

      case req @ Method.POST -> !! / "appt" =>
        for {
          body       <- req.bodyAsString.orElseFail(AppError.MissingBodyError)
          updateAppt <- ZIO.from(body.fromJson[UpdateAppointment]).mapError(AppError.JsonDecodingError)
          appt       <- AppointmentService.update(updateAppt.id, updateAppt.date, updateAppt.description)
        } yield Response.ok

      case Method.DELETE -> !! / "appts" / id =>
        for {
          id   <- AppointmentId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid appt id"))
          appt <- AppointmentService.delete(id)
        } yield Response.ok
    }

}
