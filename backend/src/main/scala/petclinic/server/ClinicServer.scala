package petclinic.server

import petclinic.Migrations
import zhttp.service.Server
import zhttp.http._
import zio._

final case class ClinicServer(
    ownerRoutes: OwnerRoutes,
    petRoutes: PetRoutes,
    vetRoutes: VetRoutes,
    visitRoutes: VisitRoutes,
    migrations: Migrations
) {

  val allRoutes: HttpApp[Any, Throwable] =
    ownerRoutes.routes ++ petRoutes.routes ++ vetRoutes.routes ++ visitRoutes.routes

  def start: ZIO[Any, Throwable, Unit] =
    for {
      // Reset the database to the initial state every 15 minutes
      // to clean up the deployed heroku data.
      _    <- migrations.reset.repeat(Schedule.fixed(15.minutes)).fork
      port <- System.envOrElse("PORT", "8080").map(_.toInt)
      _    <- Server.start(port, allRoutes @@ Middleware.cors())
    } yield ()

}

object ClinicServer {

  val layer: ZLayer[OwnerRoutes with PetRoutes with VetRoutes with VisitRoutes with Migrations, Nothing, ClinicServer] =
    ZLayer.fromFunction(ClinicServer.apply _)

}
