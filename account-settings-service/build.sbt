import play.sbt.PlayImport._

name := """Reactive-Bank"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  specs2 % Test,
  jdbc,
  cache,
  ws,
  "io.swagger" %% "swagger-play2" % "1.5.1",
  "org.postgresql" % "postgresql" % "9.3-1104-jdbc41",
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "ch.qos.logback" % "logback-core" % "1.1.7",
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.6",
  "com.typesafe.akka" % "akka-testkit_2.11" % "2.4.6"
)
resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
routesGenerator := InjectedRoutesGenerator