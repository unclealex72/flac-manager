import com.typesafe.sbt.packager.docker._
import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

resolvers += "4thline resolver" at "http://4thline.org/m2"
resolvers ++= Seq("releases").map(Resolver.sonatypeRepo)
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
resolvers += "JaudioTagger Repository" at "https://dl.bintray.com/ijabz/maven"

lazy val testingDependencies = Seq("core", "mock", "junit").
  map(name => "org.specs2" %% s"specs2-$name" % "3.8.9" % "test")

lazy val shared = (project in file("shared")).
  settings(
    name := "flac-manager-shared",
    scalaVersion := "2.12.2",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    libraryDependencies ++= Seq(
        "org.typelevel" %% "cats" % "0.9.0",
        "com.beachape" %% "enumeratum" % "1.5.12") ++
      Seq(
        "io.circe" %% "circe-core",
        "io.circe" %% "circe-generic",
        "io.circe" %% "circe-parser"
      ).map(_ % "0.7.0") ++
      testingDependencies ++
      Seq(
        "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
        "ch.qos.logback" % "logback-classic" % "1.1.7")
  )

lazy val root = (project in file(".")).
  settings(
    name := "flac-manager",
    scalaVersion := "2.12.2",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-slick" % "3.0.0-RC1",
      "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0-RC1",
      "org.xerial" % "sqlite-jdbc" % "3.18.0",
      "com.wix" %% "accord-core" % "0.6.1",
      "net.jthink" % "jaudiotagger" % "2.2.5",
      "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0",
      ws,
      guice,
      "com.vladsch.flexmark" % "flexmark-all" % "0.19.0",
      "net.codingwell" %% "scala-guice" % "4.1.0",
      "cglib" % "cglib-nodep" % "3.1",
      "org.fourthline.cling" % "cling-core" % "2.1.1",
      "commons-io" % "commons-io" % "2.5",
      "org.mockito" % "mockito-core" % "1.9.5" % "test",
      "org.eclipse.jetty" % "jetty-servlet" % "9.3.0.M0" % "test",
      "com.github.marschall" % "memoryfilesystem" % "0.9.2" % "test",
      "com.jsuereth" %% "scala-arm" % "2.0" % "test"
    ),
    libraryDependencies ++= testingDependencies,
    unmanagedResourceDirectories in Compile <+= baseDirectory(_ / "resources"),
    // Docker
    dockerBaseImage := "openjdk:alpine",
    dockerExposedPorts := Seq(9999),
    maintainer := "Alex Jones <alex.jones@unclealex.co.uk>",
    dockerRepository := Some("unclealex72"),
    version in Docker := "latest",
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
      val sqliteCommands = Seq(
        Cmd("RUN", "wget", "http://www.sqlite.org/2017/sqlite-autoconf-3180000.tar.gz"),
        Cmd("RUN", "tar", "xvfz", "sqlite-autoconf-3180000.tar.gz"),
        Cmd("RUN", "apk", "add", "--update alpine-sdk"),
        Cmd("RUN", "./sqlite-autoconf-3180000/configure", "--prefix=/usr"),
        Cmd("RUN", "make"),
        Cmd("RUN", "make", "install"),
        Cmd("RUN", "rm", "sqlite-autoconf-3180000.tar.gz"),
        Cmd("RUN", "chmod", "777", "/tmp")
      )
      val installPackageCommands = Seq("flac", "lame").map { pkg =>
        Cmd("RUN", "apk", "add", "--update", "--no-cache", pkg)
      }
      val createUserCommands = Seq(Cmd("RUN", "adduser", "-D",  "-u", "1000", "music"))
      val mkDirCommands = Seq(Cmd("RUN", "mkdir", "-p", "/music"), Cmd("VOLUME", "/music"))
      prefixCommands ++ sqliteCommands ++ installPackageCommands ++ createUserCommands ++ mkDirCommands ++ suffixCommands
    },
    javaOptions in Docker ++= Seq("-DapplyEvolutions.default=true")
  ).
  enablePlugins(PlayScala, DockerPlugin, AshScriptPlugin).
  dependsOn(shared)

lazy val client = (project in file("client")).
  settings(
    name := "flac-manager-client",
    scalaVersion := "2.12.2",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    maintainer := "Alex Jones <alex.jones@unclealex.co.uk>",
    packageSummary := "Flac Manager Client Debian Package",
    packageDescription := "Flac Manager Client Debian Package",
    libraryDependencies ++= Seq(
      ws,
      "com.beachape" %% "enumeratum" % "1.3.6",
      "com.github.scopt" %% "scopt" % "3.5.0",
      "org.fourthline.cling" % "cling-core" % "2.1.1",
      "org.typelevel" %% "cats" % "0.9.0",
      "com.github.marschall" % "memoryfilesystem" % "0.9.1" % "test"
    ) ++ testingDependencies,
    // Remove the /usr/bin/ symlinks
    linuxPackageSymlinks := linuxPackageSymlinks.value.filterNot { linuxSymlink =>
      linuxSymlink.link.startsWith("/usr/bin/")
    },
    version in Debian := ((v: String) => v + (if (v.endsWith("-")) "" else "-") + "build-aj")(version.value),
    debianPackageDependencies := Seq("java8-runtime-headless")
  ).
  enablePlugins(DebianPlugin, JavaAppPackaging).
  dependsOn(shared)

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
