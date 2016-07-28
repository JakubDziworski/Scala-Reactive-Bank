import play.sbt.PlayImport._

name := """Reactive Bank Account Service"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "io.swagger" %% "swagger-play2" % "1.5.1"
)

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"
// http://mvnrepository.com/artifact/ch.qos.logback/logback-core
libraryDependencies += "ch.qos.logback" % "logback-core" % "1.1.7"

libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.2.3" % "provided"

libraryDependencies += "com.softwaremill.macwire" %% "util" % "2.2.3"

libraryDependencies += "com.softwaremill.macwire" %% "proxy" % "2.2.3"

libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5" % "test"

routesGenerator := InjectedRoutesGenerator