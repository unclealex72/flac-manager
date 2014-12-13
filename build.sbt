import com.typesafe.sbt.SbtNativePackager.NativePackagerKeys._
import com.typesafe.sbt.SbtNativePackager._
import play.PlayScala
import sbtrelease._

name := "flac-manager"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "org.squeryl" %% "squeryl" % "0.9.6-RC3",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "joda-time" % "joda-time" % "2.2",
  "org.joda" % "joda-convert" % "1.3.1",
  "org.scaldi" %% "scaldi-play" % "0.4.1",
  // Musicbrainz REST web client
  "com.sun.jersey" % "jersey-client" % "1.5",
  "com.sun.jersey.contribs" % "jersey-apache-client" % "1.5",
  // Validation
  "com.wix" %% "accord-core" % "0.4",
  "org" % "jaudiotagger" % "2.0.3",
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

// Debian packaging

maintainer := "Alex Jones <alex.jones@unclealex.co.uk>"

packageSummary := "Flac Manager Debian Package"

packageDescription := "Flac Manager Debian Package"

version in Debian := ((v: String) => v + (if (v.endsWith("-")) "" else "-") + "build-aj")(version.value)

debianPackageDependencies := Seq("flac", "lame", "python-gpod", "python-pycurl", "python-eyed3")

daemonUser in Linux := "music"

daemonGroup in Linux := (daemonUser in Linux).value

mappings in Universal ++= Seq("checkin", "checkout", "initialise", "own", "unown", "sync").map { cmd =>
  baseDirectory.value / "scripts" / "flac-manager.py" -> s"bin/flacman-$cmd"
}


// Releasing

releaseSettings

ReleaseKeys.releaseProcess <<= thisProjectRef apply { ref =>
  import sbtrelease.ReleaseStateTransformations._
  Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  releaseTask(packageBin in Debian in ref),
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion,
  pushChanges
  )
}
