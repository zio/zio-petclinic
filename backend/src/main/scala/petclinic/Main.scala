package petclinic

import petclinic.server._
import petclinic.services._
import zio._
import zio.logging.backend.SLF4J
import zio.logging.removeDefaultLoggers
import zio.metrics.connectors.{MetricsConfig, newrelic}

object Main extends ZIOAppDefault {

  /** Configures Metrics to be run at a set interval, in our case every five seconds */
  val metricsConfig =
    ZLayer.succeed(MetricsConfig(5.seconds))

  /** As mentioned above, `provide` is used to pass along the dependencies required by this ZIO effect.  */
  override val run: Task[Unit] =
    ZIO
      .serviceWithZIO[ClinicServer](_.start)
      .provide(
        ClinicServer.layer,
        PetRoutes.layer,
        VetRoutes.layer,
        OwnerRoutes.layer,
        VisitRoutes.layer,
        QuillContext.dataSourceLayer,
        OwnerServiceLive.layer,
        PetServiceLive.layer,
        VetServiceLive.layer,
        VisitServiceLive.layer,
        Migrations.layer,
        SLF4J.slf4j,
        removeDefaultLoggers,

        // newrelic.newRelicLayer,
        // newrelic.NewRelicConfig.fromEnvLayer,
        // metricsConfig
      )

}
