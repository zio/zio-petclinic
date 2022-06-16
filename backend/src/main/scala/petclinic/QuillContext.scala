package petclinic

import com.typesafe.config.ConfigFactory
import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio._

import javax.sql.DataSource
import scala.jdk.CollectionConverters.MapHasAsJava

object QuillContext extends PostgresZioJdbcContext(SnakeCase) {
  val dataSourceLayer: ZLayer[Any, Nothing, DataSource] =
    ZLayer {
      for {
        herokuURL <- System.env("DATABASE_URL").orDie
        configMap = herokuURL
                      .map(parseHerokuDatabaseUrl(_).toMap)
                      .getOrElse(
                        Map(
                          "dataSource.user"     -> "postgres",
                          "dataSource.password" -> "",
                          "dataSource.url"      -> "jdbc:postgresql://localhost:5432/postgres"
                        )
                      )
        config = ConfigFactory.parseMap(
                   configMap.updated("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource").asJava
                 )
      } yield DataSourceLayer.fromConfig(config).orDie
    }.flatten

  final case class HerokuConnectionInfo(
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

  def parseHerokuDatabaseUrl(string: String): HerokuConnectionInfo =
    string match {
      case s"postgres://$username:$password@$host:$port/$dbname" =>
        HerokuConnectionInfo(username, password, host, port, dbname)
    }
}
