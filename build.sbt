ThisBuild / scalaVersion := "2.13.12"
ThisBuild / organization := "io.entystal"
ThisBuild / version      := "0.1.0-SNAPSHOT"

val javafxVersion = "21.0.1"

import sbtassembly.AssemblyPlugin.autoImport._
import sbtassembly.MergeStrategy

lazy val root = (project in file("."))
  .aggregate(core)
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
      "org.openjfx" % "javafx-base" % javafxVersion classifier "linux",
      "org.openjfx" % "javafx-controls" % javafxVersion classifier "linux",
      "org.openjfx" % "javafx-fxml" % javafxVersion classifier "linux",
      "org.openjfx" % "javafx-graphics" % javafxVersion classifier "linux",
      "org.openjfx" % "javafx-media" % javafxVersion classifier "linux",
      "org.openjfx" % "javafx-swing" % javafxVersion classifier "linux",
      "org.openjfx" % "javafx-web" % javafxVersion classifier "linux",
      "org.scalafx" %% "scalafx" % "21.0.0-R32",
      "org.scalafx" %% "scalafxml-core-sfx8" % "0.5",
      "org.testfx" % "testfx-core" % "4.0.15-alpha" % Test,
      "org.testfx" % "testfx-junit" % "4.0.15-alpha" % Test,
      "org.testfx" % "openjfx-monocle" % "21.0.2" % Test,
      "junit" % "junit" % "4.13.2" % Test,
      "com.novocode" % "junit-interface" % "0.11" % Test
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
      Test / javaOptions ++= Seq(
        "-Dtestfx.robot=glass",
        "-Dtestfx.headless=true",
        "-Dglass.platform=Monocle",
        "-Dmonocle.platform=Headless",
        "-Dprism.order=sw",
        "-Dprism.text=t2k",
        "-Djava.awt.headless=true"
      ),
      assembly / assemblyMergeStrategy := {
        case PathList("META-INF", _ @ _*) => MergeStrategy.discard
        case "module-info.class"         => MergeStrategy.discard
        case _                            => MergeStrategy.first
      }
    )
