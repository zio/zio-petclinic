package petclinic

import com.typesafe.config.ConfigFactory
import io.getquill.jdbczio.Quill
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio._

import javax.sql.DataSource
import scala.jdk.CollectionConverters.MapHasAsJava

/** QuillContext houses the datasource layer which initializes a connection
  * pool. This has been slightly complicated by the way Postgres exposes its
  * connection details. Database URL will only be defined when run from Postgres
  * in production.
  */
object QuillContext extends PostgresZioJdbcContext(SnakeCase) {
  val dataSourceLayer: ZLayer[Any, Nothing, DataSource] =
    ZLayer {
      for {
        postgresURL <- System.env("DATABASE_URL").orDie
        localDBConfig = Map(
                          "dataSource.user"     -> "postgres",
                          "dataSource.password" -> "password",
                          "dataSource.url"      -> "jdbc:postgresql://localhost:5432/postgres"
                        )
        configMap = postgresURL
                      .map(parsePostgresDatabaseUrl(_).toMap)
                      .getOrElse(localDBConfig)
        config = ConfigFactory.parseMap(
                   configMap.updated("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource").asJava
                 )
      } yield Quill.DataSource.fromConfig(config).orDie
    }.flatten

  final case class PostgresConnectionInfo(
      username: String,
      password: String,
      host: String,
      port: String,
      dbname: String
  ) {
    def toMap: Map[String, String] =
      Map(
        "dataSource.user"     -> username,
        "dataSource.password" -> password,
        "dataSource.url"      -> s"jdbc:postgresql://$host:$port/$dbname"
      )
  }

  def parsePostgresDatabaseUrl(string: String): PostgresConnectionInfo =
    string match {
      case s"postgres://$username:$password@$host:$port/$dbname" =>
        PostgresConnectionInfo(username, password, host, port, dbname)
    }
}
