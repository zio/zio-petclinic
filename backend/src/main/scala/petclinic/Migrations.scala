package petclinic

import org.flywaydb.core.Flyway
import zio.ZIO

import javax.sql.DataSource

object Migrations {

  val migrate: ZIO[DataSource, Throwable, Unit] =
    for {
      datasource <- ZIO.service[DataSource]
      _ <- ZIO.attempt {
             Flyway
               .configure()
               .dataSource(datasource)
               .baselineOnMigrate(true)
               .baselineVersion("0")
               .load()
               .migrate()
           }
    } yield ()

}
