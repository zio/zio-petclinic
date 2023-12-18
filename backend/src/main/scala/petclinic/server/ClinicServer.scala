package petclinic.server

import petclinic.Migrations
import zio.http._
import zio._

/** ClinicServer is a service that houses the details for how to set up the
  * ZIO-Http server.
  *
  * It is comprised of the various routes, which in this case are also services
  * that we defined in the different route files
  */
final case class ClinicServer(
    ownerRoutes: OwnerRoutes,
    petRoutes: PetRoutes,
    vetRoutes: VetRoutes,
    visitRoutes: VisitRoutes,
    migrations: Migrations
) {

  /** Composes the routes together, returning a single HttpApp.
    */
  val allRoutes: Routes[Any, Throwable] =
    ownerRoutes.routes ++ petRoutes.routes ++ vetRoutes.routes ++ visitRoutes.routes

  /** Handles all errors with an internal server error response of the message
    * and the stack trace.
    */
  def withDefaultErrorHandling(routes: Routes[Any, Throwable]): Routes[Any, Nothing] =
    routes.handleErrorCause { cause =>
      Response.internalServerError(cause.prettyPrint)
    }

  /** Logs the requests made to the server.
    *
    * It also adds a request ID to the logging context, so any further logging
    * that occurs in the handler can be associated with the same request.
    *
    * For more information on the logging, see:
    * https://zio.github.io/zio-logging/
    */
  val loggingMiddleware: Middleware[Any] =
    new Middleware[Any] {
      override def apply[Env1 <: Any, Err](routes: Routes[Env1, Err]): Routes[Env1, Err] =
        routes.transform { handler =>
          handler.contramapZIO { request =>
            Random.nextUUID.flatMap { requestId =>
              ZIO.logAnnotate("REQUEST-ID", requestId.toString) {
                for {
                  _ <- ZIO.logInfo(s"Request: $request")
                } yield request
              }
            }
          }
        }
    }

  /** Resets the database to the initial state every 15 minutes to clean up the
    * deployed Heroku data. Then, it obtains a port from the environment on
    * which to start the server. In the case of being run in production, the
    * port will be provided by Heroku, otherwise the port will be 8080. The
    * server is then started on the given port with the routes provided.
    */
  def start: ZIO[Server, Nothing, Unit] =
    for {
      _ <- migrations.reset.repeat(Schedule.fixed(15.minutes)).logError.fork
      _ <- Server.serve((withDefaultErrorHandling(allRoutes) @@ Middleware.cors @@ loggingMiddleware).toHttpApp)
    } yield ()

}

/** Here in the companion object, we define the layer that will be used to
  * create the server.
  */
object ClinicServer {

  val layer: ZLayer[OwnerRoutes with PetRoutes with VetRoutes with VisitRoutes with Migrations, Nothing, ClinicServer] =
    ZLayer.fromFunction(ClinicServer.apply _)

}
