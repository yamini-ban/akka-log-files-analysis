name := "akka-log-files"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies += "org.apache.commons" % "commons-io" % "1.3.2"

libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.6.3"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.6.3" % Test

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
