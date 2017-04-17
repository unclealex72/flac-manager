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
import javax.inject.Inject

import logging.ApplicationLogging
import play.api.Configuration

import scalaz._
import Scalaz._
import scala.util.Try

/**
 * Get the users using Play configuration
 * Created by alex on 20/11/14.
 */
case class PlayConfigurationDirectories @Inject() (configuration: Configuration) extends Directories with ApplicationLogging {

  val musicDirectory: Path =
    configuration.getString("directories.music").map(Paths.get(_)).getOrElse(Paths.get("/music"))

  val temporaryPath: Path = {
    val fileAttributes = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr--r--"))
    Files.createTempDirectory("flac-manager-", fileAttributes).toAbsolutePath
  }

  val flacPath: Path = musicDirectory.resolve("flac")
  val stagingPath: Path = musicDirectory.resolve("staging")
  val encodedPath: Path = musicDirectory.resolve("encoded")
  val devicesPath: Path = musicDirectory.resolve("devices")

  private val directoryValidation: ValidationNel[String, Unit] = {
    def validateProperty(predicate: Path => Boolean, failureMessage: Path => String): Path => ValidationNel[String, Unit] = { path =>
      if (Try(predicate(path)).toOption.getOrElse(false)) {
        Success({})
      }
      else {
        failureMessage(path).failureNel[Unit]
      }
    }
    val exists = validateProperty(Files.exists(_), path => s"$path does not exist")
    val isDirectory = validateProperty(Files.isDirectory(_), path => s"$path is not a directory")
    val isWriteable = validateProperty(Files.isWritable, path => s"$path is not writeable")

    val allValidations = (for {
      path <- Seq(flacPath, stagingPath, encodedPath, devicesPath)
      validationFunction <- Seq(exists, isDirectory)
    } yield {
      validationFunction(path)
    }) :+ isWriteable(stagingPath)
    val empty: ValidationNel[String, Unit] = Success({})
    allValidations.foldLeft(empty)((acc, v) => (acc |@| v)((_, _) => {}))
  }

  directoryValidation match {
    case Success(_) =>
    case Failure(errors) =>
      errors.foreach { error =>
        logger.error(error)
      }
      throw new IllegalStateException("The supplied directories were invalid:\n" + errors.toList.mkString("\n"))
  }
}