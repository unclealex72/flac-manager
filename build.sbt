import play.PlayScala

name := "flac-manager"

version := "6.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "org.squeryl" %% "squeryl" % "0.9.6-RC3",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "joda-time" % "joda-time" % "2.2",
  "org.joda" % "joda-convert" % "1.3.1",
  "com.escalatesoft.subcut" %% "subcut" % "2.1",
  // Validation
  "com.wix" %% "accord-core" % "0.4",
  "org" % "jaudiotagger" % "2.0.3",
  // HTTP
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  jdbc,
  cache,
  "cglib" % "cglib-nodep" % "3.1",
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.specs2" %% "specs2-core" % "2.4.8" % "test",
  "org.specs2" %% "specs2-mock" % "2.4.8" % "test",
  "org.specs2" %% "specs2-junit" % "2.4.8" % "test",
  "org.eclipse.jetty" % "jetty-servlet" % "9.3.0.M0" % "test",
  "com.h2database" % "h2" % "1.4.182" % "test"
)

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

unmanagedResourceDirectories in Compile <+= baseDirectory(_ / "resources")