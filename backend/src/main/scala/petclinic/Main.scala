package petclinic

import petclinic.server._
import petclinic.services._
import zio._
import zio.logging.backend.SLF4J
import zio.logging.removeDefaultLoggers
import zio.metrics.connectors.MetricsConfig
import zio.metrics.connectors.newrelic

object Main extends ZIOAppDefault {

  val metricsConfig =
    ZLayer.succeed(MetricsConfig(5.seconds))

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
        SLF4J.slf4j(LogLevel.Info),
        removeDefaultLoggers,
        newrelic.newRelicLayer,
        newrelic.NewRelicConfig.fromEnvLayer,
        metricsConfig
      )

}
