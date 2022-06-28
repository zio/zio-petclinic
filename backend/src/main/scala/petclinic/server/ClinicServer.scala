package petclinic.server

import petclinic.Migrations
import zhttp.service.Server
import zhttp.http._
import zhttp.http.middleware.HttpMiddleware
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

  val loggingMiddleware: HttpMiddleware[Any, Nothing] =
    new HttpMiddleware[Any, Nothing] {
      override def apply[R1 <: Any, E1 >: Nothing](
          http: Http[R1, E1, Request, Response]
      ): Http[R1, E1, Request, Response] =
        Http.fromOptionFunction[Request] { request =>
          Random.nextUUID.flatMap { requestId =>
            ZIO.logAnnotate("REQUEST-ID", requestId.toString) {
              for {
                _      <- ZIO.logInfo(s"Request: $request")
                result <- http(request)
              } yield result
            }
          }
        }
    }

  def start: ZIO[Any, Throwable, Unit] =
    for {
      // Reset the database to the initial state every 15 minutes
      // to clean up the deployed heroku data.
      _    <- migrations.reset.repeat(Schedule.fixed(15.minutes)).fork
      port <- System.envOrElse("PORT", "8080").map(_.toInt)
      _    <- Server.start(port, allRoutes @@ Middleware.cors() @@ loggingMiddleware)
    } yield ()

}

object ClinicServer {

  val layer: ZLayer[OwnerRoutes with PetRoutes with VetRoutes with VisitRoutes with Migrations, Nothing, ClinicServer] =
    ZLayer.fromFunction(ClinicServer.apply _)

}
