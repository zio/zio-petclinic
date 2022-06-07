package petclinic.server

import petclinic.{Migrations, QuillContext}
import petclinic.services._
import zhttp.service.Server
import zhttp.http._
import zio._

import java.time.LocalDate

object ClinicServer extends ZIOAppDefault {

  val handledApp: Http[OwnerService with PetService with VisitService with VetService, Nothing, Request, Response] = {
    import routes._
    (OwnerRoutes.routes ++ PetRoutes.routes ++ VisitRoutes.routes ++ VetRoutes.routes).catchAll {
      case AppError.MissingBodyError =>
        Http.text("MISSING BODY").setStatus(Status.BadRequest)
      case AppError.JsonDecodingError(message) =>
        Http.text(s"JSON DECODING ERROR: $message").setStatus(Status.BadRequest)
    }
  }

  override val run: ZIO[Any, Throwable, Unit] = {
    for {
      _ <- Migrations.migrate
      _ <- Server.start(8080, handledApp @@ Middleware.cors())
    } yield ()
  }
    .provide(
      QuillContext.dataSourceLayer,
      OwnerServiceLive.layer,
      PetServiceLive.layer,
      VetServiceLive.layer,
      VisitServiceLive.layer
    )

}
