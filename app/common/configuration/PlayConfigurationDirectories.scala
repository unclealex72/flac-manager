package common.configuration

import java.nio.file.Paths

import play.api.Configuration

/**
 * Get the users using Play configuration
 * Created by alex on 20/11/14.
 */
case class PlayConfigurationDirectories(override val configuration: Configuration) extends PlayConfiguration[Directories](configuration) with Directories {

  def load(configuration: Configuration): Option[Directories] = {
    for {
      directories <- configuration.getConfig("directories")
      flacDir <- directories.getString("flac")
      stagingDir <- directories.getString("staging")
      encodedDir <- directories.getString("encoded")
      devicesDir <- directories.getString("devices")
      tmpDir <- directories.getString("tmp")
    } yield new Directories {
      override val temporaryPath = Paths.get(tmpDir)
      override val encodedPath = Paths.get(encodedDir)
      override val devicesPath = Paths.get(devicesDir)
      override val flacPath = Paths.get(flacDir)
      override val stagingPath = Paths.get(stagingDir)
    }
  }

  override lazy val temporaryPath = result.temporaryPath
  override lazy val encodedPath = result.encodedPath
  override lazy val devicesPath = result.devicesPath
  override lazy val flacPath = result.flacPath
  override lazy val stagingPath = result.stagingPath
}
