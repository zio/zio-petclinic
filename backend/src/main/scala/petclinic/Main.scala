package petclinic

import petclinic.server._
import petclinic.services._
import zio._
import zio.logging.backend.SLF4J
import zio.logging.removeDefaultLoggers
import zio.metrics.connectors.{MetricsConfig, newrelic}

/** Main is the entry point for the application.
  *
  * Because it extends ZIOAppDefault, we are required to define a `run` method
  * in which our application logic is housed. `run` is responsible for starting
  * the application and providing it with the necessary dependencies.
  */
object Main extends ZIOAppDefault {

  /** Configures Metrics to be run at a set interval, in our case every five
    * seconds
    */
  val metricsConfig =
    ZLayer.succeed(MetricsConfig(5.seconds))

  /** As mentioned above, `provide` is used to pass along the dependencies
    * (dependencies here meaning the internal services our app depends on)
    * required by this ZIO effect (our run method).
    *
    * By providing these dependencies we are saying we have:
    *   - instructions for creating a server
    *   - routes for any request we might need to handle (pet, vet, etc.)
    *   - the datasource for the database
    *   - instructions for how data should be persisted or modified in the
    *     database which makes use of the defined service and the datasource
    *   - migration instructions we can give to our services for interacting
    *     with the database
    *   - instructions for logging and metrics which give us information about
    *     how the app is running and any issues we might run into.
    */
  override val run: Task[Unit] =
    ZIO
      .serviceWithZIO[ClinicServer](_.start)
      .provide(
//        ZLayer.Debug.mermaid,
        ClinicServer.layer,
        // Routes
        PetRoutes.layer,
        VetRoutes.layer,
        OwnerRoutes.layer,
        VisitRoutes.layer,
        // Repositories
        OwnerServiceLive.layer,
        PetServiceLive.layer,
        VetServiceLive.layer,
        VisitServiceLive.layer,
        Migrations.layer,
        QuillContext.dataSourceLayer,
        // Operations
        SLF4J.slf4j,
        removeDefaultLoggers,
        newrelic.newRelicLayer,
        newrelic.NewRelicConfig.fromEnvLayer,
        metricsConfig
      )

}
