import com.typesafe.sbt.packager.docker._
import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
import com.typesafe.sbt.packager.linux._

resolvers += "4thline resolver" at "http://4thline.org/m2"
resolvers ++= Seq("releases").map(Resolver.sonatypeRepo)

lazy val root = (project in file(".")).
  settings(
    name := "flac-manager",
    scalaVersion := "2.11.7",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    libraryDependencies ++= Seq(
      "org.squeryl" %% "squeryl" % "0.9.6-RC3",
      "org.postgresql" % "postgresql" % "42.0.0.jre7",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "joda-time" % "joda-time" % "2.2",
      "org.joda" % "joda-convert" % "1.3.1",
      // Musicbrainz REST web client
      "com.sun.jersey" % "jersey-client" % "1.5",
      "com.sun.jersey.contribs" % "jersey-apache-client" % "1.5",
      // Validation
      "org.scalaz" %% "scalaz-core" % "7.1.3",
      "com.wix" %% "accord-core" % "0.4",
      "org" % "jaudiotagger" % "2.0.3",
      "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0",
      jdbc,
      cache,
      evolutions,
      "net.codingwell" %% "scala-guice" % "4.0.1",
      "cglib" % "cglib-nodep" % "3.1",
      "org.fourthline.cling" % "cling-core" % "2.1.1",
      "org.mockito" % "mockito-core" % "1.9.5" % "test",
      "org.eclipse.jetty" % "jetty-servlet" % "9.3.0.M0" % "test",
      "com.h2database" % "h2" % "1.4.182" % "test"
    ),
    libraryDependencies ++= Seq("core", "mock", "junit").
      map(name => "org.specs2" %% s"specs2-$name" % "2.4.8" % "test"),
    unmanagedResourceDirectories in Compile <+= baseDirectory(_ / "resources"),
    // Docker
    dockerBaseImage := "openjdk:alpine",
    dockerExposedPorts := Seq(9999),
    maintainer := "Alex Jones <alex.jones@unclealex.co.uk>",
    dockerRepository := Some("unclealex72"),
    daemonUser in Docker := "music",
    // Installing packages requires the run commands to be put high up in the list of docker commands
    dockerCommands := {
      val commands = dockerCommands.value
      val (prefixCommands, suffixCommands) = commands.splitAt {
        val firstRunCommand = commands.indexWhere {
          case Cmd("FROM", _) => true
          case _ => false
        }
        firstRunCommand + 1
      }
      val installPackageCommands = Seq("flac", "lame").map { pkg =>
        Cmd("RUN", "apk", "add", "--update", "--no-cache", pkg)
      }
      val createUserCommands = Seq(Cmd("RUN", "adduser", "-D",  "-u", "1000", "music"))
      val mkDirCommands = Seq(Cmd("RUN", "mkdir", "-p", "/music"), Cmd("VOLUME", "/music"))
      prefixCommands ++ installPackageCommands ++ createUserCommands ++ mkDirCommands ++ suffixCommands
    },
    javaOptions in Docker ++= Seq("-DapplyEvolutions.default=true")
  ).
  enablePlugins(PlayScala, DockerPlugin, AshScriptPlugin)

lazy val client = (project in file("client")).
  settings(
    name := "flac-manager-client",
    maintainer := "Alex Jones <alex.jones@unclealex.co.uk>",
    packageSummary := "Flac Manager Debian Package",
    packageDescription := "Flac Manager Debian Package",
    version in Debian := ((v: String) => v + (if (v.endsWith("-")) "" else "-") + "build-aj")(version.value),
    linuxPackageSymlinks ++= Seq("checkin", "checkout", "initialise", "own", "unown").map { cmd =>
      LinuxSymlink(s"/usr/bin/flacman-$cmd", "/usr/bin/flac-manager.py")
    },
    debianPackageDependencies := Seq("python-pycurl")
  ).
  enablePlugins(DebianPlugin)

/* Releases */
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies, // : ReleaseStep
  inquireVersions, // : ReleaseStep
  runTest, // : ReleaseStep
  setReleaseVersion, // : ReleaseStep
  commitReleaseVersion, // : ReleaseStep, performs the initial git checks
  tagRelease, // : ReleaseStep
  releaseStepCommand("docker:publish"), // : ReleaseStep, build server docker image.
  releaseStepCommand("client/debian:packageBin"), // : ReleaseStep, client build deb file.
  setNextVersion, // : ReleaseStep
  commitNextVersion, // : ReleaseStep
  pushChanges // : ReleaseStep, also checks that an upstream branch is properly configured
)
