import play.sbt.PlayImport._

name := """Reactive-Bank"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "io.swagger" %% "swagger-play2" % "1.5.1"
)

libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5" % "test"