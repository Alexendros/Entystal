ThisBuild / scalaVersion := "2.13.12"
ThisBuild / organization := "io.entystal"
ThisBuild / version      := "0.1.0-SNAPSHOT"

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
      "org.scalafx" %% "scalafx" % "21.0.0-R32",
      "org.scalafx" %% "scalafxml-core-sfx8" % "0.5"
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-encoding", "utf8",
      "-Xfatal-warnings"
    ),
    Test / fork := true,
    Test / parallelExecution := false
  )
