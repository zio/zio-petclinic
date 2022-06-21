package petclinic.server

import petclinic.server.routes._
import petclinic.services._
import petclinic.{Migrations, QuillContext}
import zhttp.http._
import zhttp.service.Server
import zio._

object ClinicServer extends ZIOAppDefault {

  val allRoutes: HttpApp[VetService with VisitService with PetService with OwnerService, Throwable] =
    OwnerRoutes.routes ++ PetRoutes.routes ++ VisitRoutes.routes ++ VetRoutes.routes

  override val run: ZIO[Any, Throwable, Unit] = {
    for {
      // Reset the database to the initial state every 15 minutes
      // to clean up the deployed heroku data.
      _    <- Migrations.reset.repeat(Schedule.fixed(15.minutes)).fork
      port <- System.envOrElse("PORT", "8080").map(_.toInt)
      _    <- Server.start(port, allRoutes @@ Middleware.cors())
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
