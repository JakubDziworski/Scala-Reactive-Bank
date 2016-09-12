resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.6")

// web plugins
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.6")
addSbtPlugin("com.softwaremill.clippy" % "plugin-sbt" % "0.3.0")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")
