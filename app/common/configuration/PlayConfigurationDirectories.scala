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

import cats.data.Validated._
import cats.data.{NonEmptyList, ValidatedNel}
import cats.implicits._
import common.async.CommandExecutionContext
import logging.ApplicationLogging
import org.apache.commons.io.FileUtils
import play.api.Configuration
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future
import scala.util.Try

/**
  * Find repositories using the Play Framework's configuration.
  * @param configuration The underlying [[Configuration]] object.
  * @param lifecycle Play's [[ApplicationLifecycle]], used to remove temporary files on shutdown.
  * @param ec The execution context used to remove temporary files on shutdown.
  */
case class PlayConfigurationDirectories @Inject()(
                                                   configuration: Configuration,
                                                   lifecycle: ApplicationLifecycle)
                                                  (implicit commandExecutionContext: CommandExecutionContext) extends Directories with ApplicationLogging {

  /**
    * The parent directory for all repositories.
    */
  val musicDirectory: Path =
    configuration.getOptional[String]("directories.music").map(Paths.get(_)).getOrElse(Paths.get("/music"))

  val temporaryPath: Path = {
    val fileAttributes = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr--r--"))
    val rootTmpDir = musicDirectory.resolve("tmp")
    val tmp = if (Files.isDirectory(rootTmpDir)) {
      Files.createTempDirectory(rootTmpDir, "flac-manager-", fileAttributes).toAbsolutePath
    }
    else {
      Files.createTempDirectory("flac-manager-", fileAttributes).toAbsolutePath
    }
    logger.info(s"Created temporary directory $tmp")
    tmp
  }

  val datumPath: Path = musicDirectory.resolve(".flac-manager-datum-file-" + java.util.UUID.randomUUID.toString)

  // Create the datum file
  Files.createFile(datumPath)

  private val directoryValidation: ValidatedNel[String, Unit] = {
    def validateProperty(predicate: Path => Boolean, failureMessage: Path => String): Path => ValidatedNel[String, Unit] = { path =>
      if (Try(predicate(path)).toOption.getOrElse(false)) {
        Valid({})
      }
      else {
        Invalid(NonEmptyList.of(failureMessage(path)))
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
    val empty: ValidatedNel[String, Unit] = Valid({})
    allValidations.foldLeft(empty)((acc, v) => (acc |@| v).map((_, _) => {}))
  }

  directoryValidation match {
    case Valid(_) =>
    case Invalid(errors) =>
      errors.toList.foreach { error =>
        logger.error(error)
      }
      throw new IllegalStateException("The supplied directories were invalid:\n" + errors.toList.mkString("\n"))
  }

  lifecycle.addStopHook { () =>
    Future {
      Try(FileUtils.deleteDirectory(temporaryPath.toFile))
      FileUtils.deleteQuietly(datumPath.toFile)
    }
  }
}