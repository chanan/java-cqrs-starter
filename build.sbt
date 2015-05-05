name := "java-cqrs-starter"

version := "1.0.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.example" %% "java-cqrs-api" % "1.0.0-SNAPSHOT",
  "com.typesafe.akka" %% "akka-contrib" % "2.3.10",
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars.bower" % "jquery" % "2.1.3",
  "org.webjars.bower" % "bootstrap" % "3.3.4",
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)