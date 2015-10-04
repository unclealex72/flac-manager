/*
 * Copyright 2014 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.configuration

import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{Files, Path, Paths}

import com.typesafe.scalalogging.StrictLogging
import play.api.Configuration

/**
 * Get the users using Play configuration
 * Created by alex on 20/11/14.
 */
case class PlayConfigurationDirectories(override val configuration: Configuration)
  extends PlayConfiguration[Directories](configuration) with Directories {

  def load(configuration: Configuration): Option[Directories] = {
    configuration.getConfig("directories").flatMap { directories =>
      val fileAttributes = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr--r--"))
      val tmpDir = Files.createTempDirectory("flac-manager-", fileAttributes).toAbsolutePath.toString
      for {
        directories <- configuration.getConfig("directories")
        flacDir <- directories.getString("flac")
        stagingDir <- directories.getString("staging")
        encodedDir <- directories.getString("encoded")
        devicesDir <- directories.getString("devices")
      } yield InternalDirectories(tmpDir, encodedDir, devicesDir, flacDir, stagingDir)
    }
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