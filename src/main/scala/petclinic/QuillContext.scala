package petclinic

import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio.ZLayer

import javax.sql.DataSource

object QuillContext extends PostgresZioJdbcContext(SnakeCase) {
  val dataSourceLayer: ZLayer[Any, Throwable, DataSource] =
    DataSourceLayer.fromPrefix("database")
}


