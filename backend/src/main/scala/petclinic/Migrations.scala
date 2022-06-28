package petclinic

import org.flywaydb.core.Flyway
import zio._

import javax.sql.DataSource

final case class Migrations(dataSource: DataSource) {

  private val loadFlyway: Task[Flyway] =
    for {
      flyway <- ZIO.attempt {
                  Flyway
                    .configure()
                    .dataSource(dataSource)
                    .baselineOnMigrate(true)
                    .baselineVersion("0")
                    .load()
                }
    } yield flyway

  val migrate: Task[Unit] =
    for {
      flyway <- loadFlyway
      _      <- ZIO.attempt(flyway.migrate())
    } yield ()

  val reset: Task[Unit] =
    for {
      _      <- ZIO.debug("RESETTING DATABASE!")
      flyway <- loadFlyway
      _      <- ZIO.attempt(flyway.clean())
      _      <- ZIO.attempt(flyway.migrate())
    } yield ()

}

object Migrations {
  val layer: ZLayer[DataSource, Nothing, Migrations] =
    ZLayer.fromFunction(Migrations.apply _)
}
