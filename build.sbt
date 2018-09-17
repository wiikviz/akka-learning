import sbt.Resolver

name := "gucli"

version := "0.3-SNAPSHOT"

scalaVersion := "2.12.6"

resolvers += Resolver.sonatypeRepo("snapshots")

val playJsonVersion = "2.5.4"
val playJsonStandaloveVersion = "2.0.0-M2"
val playWSVersion = "1.0.0"
val scallopVersion = "3.1.0"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ahc-ws-standalone" % playWSVersion,
  "com.typesafe.play" %% "play-ws-standalone-json" % playJsonStandaloveVersion,
  "org.rogach" %% "scallop" % scallopVersion,
  "org.yaml" % "snakeyaml" % "1.22",
  "io.circe" %% "circe-yaml" % "0.8.0",
  "io.circe" %% "circe-parser" % "0.9.3",
  "io.circe" %% "circe-generic" % "0.9.3",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.16" % Test,
  "org.scalatest" % "scalatest_2.12" % "3.0.5" % Test
  //"org.yaml" % "snakeyaml" % ""
)
