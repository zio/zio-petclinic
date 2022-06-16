package petclinic

import org.flywaydb.core.Flyway
import zio.ZIO

import javax.sql.DataSource

object Migrations {

  private val loadFlyway: ZIO[DataSource, Throwable, Flyway] =
    for {
      datasource <- ZIO.service[DataSource]
      flyway <- ZIO.attempt {
                  Flyway
                    .configure()
                    .dataSource(datasource)
                    .baselineOnMigrate(true)
                    .baselineVersion("0")
                    .load()
                }
    } yield flyway

  val migrate: ZIO[DataSource, Throwable, Unit] =
    for {
      flyway <- loadFlyway
      _      <- ZIO.attempt(flyway.migrate())
    } yield ()

  val reset: ZIO[DataSource, Throwable, Unit] =
    for {
      _      <- ZIO.debug("RESETTING DATABASE!")
      flyway <- loadFlyway
      _      <- ZIO.attempt(flyway.clean())
      _      <- ZIO.attempt(flyway.migrate())
    } yield ()

}
