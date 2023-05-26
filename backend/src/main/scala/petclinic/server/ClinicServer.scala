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
  val allRoutes: HttpApp[Any, Throwable] = {
    ownerRoutes.routes ++ petRoutes.routes ++ vetRoutes.routes ++ visitRoutes.routes
  }

  /** Logs the requests made to the server.
    *
    * It also adds a request ID to the logging context, so any further logging
    * that occurs in the handler can be associated with the same request.
    *
    * For more information on the logging, see:
    * https://zio.github.io/zio-logging/
    */
  val loggingMiddleware: HttpAppMiddleware.Simple[Any, Nothing] =
    new HttpAppMiddleware.Simple[Any, Nothing] {
      override def apply[Env, Err](
          http: Http[Env, Err, Request, Response]
      )(implicit trace: zio.Trace): Http[Env, Err, Request, Response] =
        Http.fromHandlerZIO[Request] { request =>
          Random.nextUUID.flatMap { requestId =>
            ZIO.logAnnotate("REQUEST-ID", requestId.toString) {
              for {
                _      <- ZIO.logInfo(s"Request: $request")
                result <- http.runHandler(request)
              } yield result
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
  def start: ZIO[Server, Throwable, Unit] =
    for {
      _ <- migrations.reset.repeat(Schedule.fixed(15.minutes)).fork
      _ <- Server.serve(allRoutes.withDefaultErrorResponse @@ HttpAppMiddleware.cors() @@ loggingMiddleware)
    } yield ()

}

/** Here in the companion object, we define the layer that will be used to
  * create the server.
  */
object ClinicServer {

  val layer: ZLayer[OwnerRoutes with PetRoutes with VetRoutes with VisitRoutes with Migrations, Nothing, ClinicServer] =
    ZLayer.fromFunction(ClinicServer.apply _)

}
