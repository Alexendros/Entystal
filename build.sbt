ThisBuild / scalaVersion := "2.13.12"
ThisBuild / organization := "io.entystal"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / coverageMinimumStmtTotal := 80
ThisBuild / coverageFailOnMinimum := true
// Excluimos solo el proyecto raíz de la cobertura
root / coverageExcludedPackages := ".*"

val javafxVersion = "21.0.1"
// Clasificador de JavaFX según el sistema operativo actual
val osName = System.getProperty("os.name").toLowerCase
val javafxPlatform =
  if (osName.contains("win")) "win"
  else if (osName.contains("mac")) "mac"
  else "linux"

import sbtassembly.AssemblyPlugin.autoImport._
import sbtassembly.MergeStrategy

lazy val root = (project in file("."))
  .aggregate(core, rest)
  .settings(
    name := "entystal-root"
  )

lazy val core = (project in file("core"))
  .settings(
    name := "entystal-core",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"          % "2.0.15",
      "dev.zio" %% "zio-logging"  % "2.1.13",
      "dev.zio" %% "zio-json"     % "0.6.2",
      "org.tpolecat" %% "doobie-core"  % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC4",
      "dev.zio" %% "zio-interop-cats" % "23.1.0.0",
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "dev.zio" %% "zio-test"     % "2.0.15" % Test,
      "dev.zio" %% "zio-test-sbt" % "2.0.15" % Test,
      "com.github.scopt" %% "scopt" % "4.1.0",
      "org.openjfx" % "javafx-base" % javafxVersion classifier javafxPlatform,
      "org.openjfx" % "javafx-controls" % javafxVersion classifier javafxPlatform,
      "org.openjfx" % "javafx-fxml" % javafxVersion classifier javafxPlatform,
      "org.openjfx" % "javafx-graphics" % javafxVersion classifier javafxPlatform,
      "org.openjfx" % "javafx-media" % javafxVersion classifier javafxPlatform,
      "org.openjfx" % "javafx-swing" % javafxVersion classifier javafxPlatform,
      "org.openjfx" % "javafx-web" % javafxVersion classifier javafxPlatform,
      "org.scalafx" %% "scalafx" % "21.0.0-R32",
      "org.scalafx" %% "scalafxml-core-sfx8" % "0.5",
      "org.apache.pdfbox" % "pdfbox" % "2.0.30"
      ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-encoding", "utf8",
      "-Xfatal-warnings"
    ),
      Test / fork := true,
      Test / parallelExecution := false,
      assembly / assemblyMergeStrategy := {
        case PathList("META-INF", _ @ _*) => MergeStrategy.discard
        case "module-info.class"         => MergeStrategy.discard
        case _                            => MergeStrategy.first
      },
      coverageEnabled := true,
      coverageFailOnMinimum := false,
      coverageHighlighting := true
    )

lazy val rest = (project in file("rest"))
  .dependsOn(core)
  .settings(
    name := "entystal-rest",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % "0.23.23",
      "org.http4s" %% "http4s-dsl" % "0.23.23",
      "org.http4s" %% "http4s-circe" % "0.23.23",
      "org.http4s" %% "http4s-ember-client" % "0.23.23" % Test,
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.prometheus" % "simpleclient" % "0.16.0",
      "io.prometheus" % "simpleclient_common" % "0.16.0",
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "dev.zio" %% "zio-json" % "0.6.2"
    )
  )
