package common.configuration

import java.nio.file.{Path, Paths}

import com.typesafe.scalalogging.StrictLogging
import play.api.Configuration

/**
 * Get the users using Play configuration
 * Created by alex on 20/11/14.
 */
case class PlayConfigurationDirectories(override val configuration: Configuration)
  extends PlayConfiguration[Directories](configuration) with Directories {

  def load(configuration: Configuration): Option[Directories] = {
    for {
      directories <- configuration.getConfig("directories")
      flacDir <- directories.getString("flac")
      stagingDir <- directories.getString("staging")
      encodedDir <- directories.getString("encoded")
      devicesDir <- directories.getString("devices")
      tmpDir <- directories.getString("tmp")
    } yield InternalDirectories(tmpDir, encodedDir, devicesDir, flacDir, stagingDir)
  }

  override lazy val temporaryPath = result.temporaryPath
  override lazy val encodedPath = result.encodedPath
  override lazy val devicesPath = result.devicesPath
  override lazy val flacPath = result.flacPath
  override lazy val stagingPath = result.stagingPath
}

case object InternalDirectories extends StrictLogging {

  def apply(tmpDir: String, encodedDir: String, devicesDir: String, flacDir: String, stagingDir: String): Directories = {
    val path: String => Path = str => Paths.get(str).toAbsolutePath
    val directories = new Directories {
      override val temporaryPath = path(tmpDir)
      override val encodedPath = path(encodedDir)
      override val devicesPath = path(devicesDir)
      override val flacPath = path(flacDir)
      override val stagingPath = path(stagingDir)
    }
    logger.info(s"Configuring directories...")
    Seq(
      "temp" -> directories.temporaryPath,
      "encoded" -> directories.encodedPath,
      "devices" -> directories.devicesPath,
      "flac" -> directories.flacPath,
      "staging" -> directories.stagingPath).foreach { case (k, v) =>
      logger.info(s"$k: $v")
    }
    directories
  }
}