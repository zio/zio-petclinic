ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val animusVersion               = "0.1.15"    // animation library for Laminar
val flywayVersion               = "8.5.12"    // manages database migrations
val laminarVersion              = "0.14.2"    // functional reactive programming (FRP) library
val postgresVersion             = "42.3.6"    // Java database connectivity (JDBC) driver for PostgreSQL
val scalaJavaTimeVersion        = "2.4.0"     // an implementation of the java.time package for Scala
val slf4jVersion                = "1.7.36"    // logging framework
val sttpClientVersion           = "3.6.2"     // an API for describing HTTP requests and how to handle responses
val waypointVersion             = "0.5.0"     // router for Laminar for URL matching and managing URL transitions
val zioHttpVersion              = "3.0.0-RC1" // HTTP client library for ZIO
val zioJsonVersion              = "0.5.0"     // JSON serialization library for ZIO
val zioLoggingVersion           = "2.1.13"    // logging library for ZIO
val zioQuillVersion             = "4.6.0"     // compile-time database query library for ZIO
val zioTestContainersVersion    = "0.10.0"    // library fro testing database queries with ZIO
val zioVersion                  = "2.0.13"    // Scala library for asynchronous and concurrent programming
val zioMetricsConnectorsVersion = "2.0.8"     // metrics library for ZIO

Global / onChangedBuildSource := ReloadOnSourceChanges

val sharedSettings = Seq(
  libraryDependencies ++= Seq(
    "dev.zio" %%% "zio-json" % zioJsonVersion
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "utf8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",
    "-Ymacro-annotations"
  )
)

lazy val root = (project in file("."))
  .aggregate(backend, frontend, shared)
  .settings(name := "pet-clinic")

lazy val backend = (project in file("backend"))
  .settings(
    name := "pet-clinic-backend",
    libraryDependencies ++= Seq(
      "dev.zio"               %% "zio"                               % zioVersion,
      "dev.zio"               %% "zio-macros"                        % zioVersion,
      "dev.zio"               %% "zio-metrics-connectors"            % zioMetricsConnectorsVersion,
      "dev.zio"               %% "zio-test"                          % zioVersion % Test,
      "dev.zio"               %% "zio-test-sbt"                      % zioVersion % Test,
      "dev.zio"               %% "zio-http"                          % zioHttpVersion,
      "io.getquill"           %% "quill-jdbc-zio"                    % zioQuillVersion,
      "org.postgresql"         % "postgresql"                        % postgresVersion,
      "org.flywaydb"           % "flyway-core"                       % flywayVersion,
      "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % zioTestContainersVersion,
      "io.github.scottweaver" %% "zio-2-0-db-migration-aspect"       % zioTestContainersVersion,
      "dev.zio"               %% "zio-logging-slf4j"                 % zioLoggingVersion,
      "org.slf4j"              % "slf4j-api"                         % slf4jVersion,
      "org.slf4j"              % "slf4j-simple"                      % slf4jVersion
    ),
    Test / fork := true,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
  .enablePlugins(JavaAppPackaging)
  .settings(sharedSettings)
  .enablePlugins(FlywayPlugin)
  .settings(
    flywayUrl      := "jdbc:postgresql://localhost:5432/postgres",
    flywayUser     := "postgres",
    flywayPassword := ""
  )
  .dependsOn(shared)

lazy val frontend = (project in file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "pet-clinic-frontend",
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withSourceMap(false) },
    libraryDependencies ++= Seq(
      "com.raquo"                     %%% "laminar"         % laminarVersion,
      "io.github.kitlangton"          %%% "animus"          % animusVersion,
      "com.raquo"                     %%% "waypoint"        % waypointVersion,
      "io.github.cquiroz"             %%% "scala-java-time" % scalaJavaTimeVersion,
      "com.softwaremill.sttp.client3" %%% "core"            % sttpClientVersion
    )
  )
  .settings(sharedSettings)
  .dependsOn(shared)

lazy val shared = (project in file("shared"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSLinkerConfig ~= { _.withSourceMap(false) },
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) }
  )
  .settings(sharedSettings)
