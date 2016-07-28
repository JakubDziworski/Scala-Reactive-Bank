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
  "com.typesafe.slick" %% "slick" % "3.0.0"
)
resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"
// http://mvnrepository.com/artifact/ch.qos.logback/logback-core
libraryDependencies += "ch.qos.logback" % "logback-core" % "1.1.7"

libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.2.3" % "provided"

libraryDependencies += "com.softwaremill.macwire" %% "util" % "2.2.3"

libraryDependencies += "com.softwaremill.macwire" %% "proxy" % "2.2.3"

libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5" % "test"
// http://mvnrepository.com/artifact/com.github.t3hnar/scala-bcrypt_2.10
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m"
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.4.6"

// http://mvnrepository.com/artifact/com.typesafe.akka/akka-testkit_2.11
libraryDependencies += "com.typesafe.akka" % "akka-testkit_2.11" % "2.4.6"

routesGenerator := InjectedRoutesGenerator