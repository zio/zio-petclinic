ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val zioVersion               = "2.0.0-RC6"
val zioJsonVersion           = "0.3.0-RC8"
val zioHttpVersion           = "2.0.0-RC7"
val zioQuillVersion          = "3.17.0-RC3"
val postgresVersion          = "42.3.4"
val flywayVersion            = "8.5.10"
val zioTestContainersVersion = "0.4.1"

lazy val root = (project in file("."))
  .settings(
    name := "pet-clinic",
    libraryDependencies ++= Seq(
      "dev.zio"               %% "zio"                               % zioVersion,
      "dev.zio"               %% "zio-test"                          % zioVersion     % Test,
      "dev.zio"               %% "zio-test-sbt"                      % zioVersion     % Test,
      "dev.zio"               %% "zio-json"                          % zioJsonVersion,
      "io.d11"                %% "zhttp"                             % zioHttpVersion,
      "io.d11"                %% "zhttp-test"                        % zioHttpVersion % Test,
      "io.getquill"           %% "quill-jdbc-zio"                    % zioQuillVersion,
      "org.postgresql"         % "postgresql"                        % postgresVersion,
      "org.flywaydb"           % "flyway-core"                       % flywayVersion,
      "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % zioTestContainersVersion,
      "io.github.scottweaver" %% "zio-2-0-db-migration-aspect"       % zioTestContainersVersion
    ),
    Test / fork := true,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
