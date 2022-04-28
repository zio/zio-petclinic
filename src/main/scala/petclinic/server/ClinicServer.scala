package petclinic.server

import petclinic.QuillContext
import petclinic.services._
import zhttp.service.Server
import zhttp.http._
import zio._

object ClinicServer extends ZIOAppDefault {

  val handledApp: Http[OwnerService with PetService with AppointmentService, Nothing, Request, Response] = {
    import routes._
    (OwnerRoutes.routes ++ PetRoutes.routes ++ AppointmentRoutes.routes).catchAll {
      case AppError.MissingBodyError =>
        Http.text("MISSING BODY").setStatus(Status.BAD_REQUEST)
      case AppError.JsonDecodingError(message) =>
        Http.text(s"JSON DECODING ERROR: $message").setStatus(Status.BAD_REQUEST)
    }
  }

  override val run: ZIO[Any, Throwable, Nothing] =
    Server
      .start(8080, handledApp)
      .provide(
        Random.live,
        QuillContext.dataSourceLayer,
        OwnerServiceLive.layer,
        PetServiceLive.layer,
        AppointmentServiceLive.layer
      )
}
