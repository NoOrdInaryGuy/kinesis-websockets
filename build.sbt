name := "kinesis-websockets"
 
version := "0.0.1"
 
scalaVersion := "2.11.6"
 
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0",
  "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0",
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0",
  "com.typesafe.play" %% "play-json" % "2.4.0-M2",
  "com.amazonaws" % "amazon-kinesis-client" % "1.2.1",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "net.ceedubs" %% "ficus" % "1.1.2"
)
 
resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
 
resolvers += "Typesafe" at "https://repo.typesafe.com/typesafe/releases/"
