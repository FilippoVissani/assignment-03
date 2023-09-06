name := "project"

version := "1.0"

scalaVersion := "3.1.1"

lazy val akkaVersion = "2.6.19"
lazy val akkaGroup = "com.typesafe.akka"
libraryDependencies ++= Seq(
  akkaGroup %% "akka-actor-typed" % akkaVersion,
  akkaGroup %% "akka-cluster-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  akkaGroup %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.16" % Test,
  "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
)

assembly / mainClass := Some("pcd.assignment03.actor_programming.app.main")
