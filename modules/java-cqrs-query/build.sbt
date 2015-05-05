name := "java-cqrs-query"

organization := "com.example"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.6"

resolvers += "krasserm at bintray" at "http://dl.bintray.com/krasserm/maven"

libraryDependencies ++= Seq(
  "com.example" %% "java-cqrs-models" % "1.0.0-SNAPSHOT",
  "com.example" %% "java-cqrs-api" % "1.0.0-SNAPSHOT",
  "com.typesafe.akka" %% "akka-actor" % "2.3.10",
  "com.typesafe.akka" %% "akka-persistence-experimental" % "2.3.10",
  "com.github.krasserm" %% "akka-persistence-cassandra" % "0.3.7",
  "com.typesafe.akka" %% "akka-contrib" % "2.3.10",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.10" % "test",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test")  