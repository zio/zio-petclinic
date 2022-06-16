package petclinic.server

import petclinic.services._
import petclinic.{Migrations, QuillContext}
import zhttp.http._
import zhttp.service.Server
import zio._

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
      // Reset the database to the initial state every 15 minutes
      // to clean up the deployed heroku data.
      _    <- Migrations.reset.repeat(Schedule.fixed(15.minutes)).fork
      port <- System.envOrElse("PORT", "8080").map(_.toInt)
      _    <- Server.start(port, handledApp @@ Middleware.cors())
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
