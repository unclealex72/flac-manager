/*
 * Copyright 2017 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client

import java.nio.file.{Files, Path}

import cats.data.NonEmptyList
import cats.syntax.either._
import common.configuration.FlacDirectories
import json.DirectoryType
import json.DirectoryType.{FlacDirectoryType, StagingDirectoryType}

/**
  * Created by alex on 21/04/17
  **/
object DirectoryRelativiser {

  case class DatumBasedFlacDirectories(override val datumPath: Path) extends FlacDirectories

  def relativise(datumFilename: String, directoryType: DirectoryType, directory: Path): Either[NonEmptyList[String], Path] = {
    val absoluteDirectoryPath = directory.toAbsolutePath
    if (Files.isDirectory(absoluteDirectoryPath)) {
      def maybeFlacDirectories(dir: Path): Either[String, FlacDirectories] = {
        val datumPath = dir.resolve(datumFilename)
        if (Files.exists(datumPath)) {
          Right(DatumBasedFlacDirectories(datumPath))
        }
        else {
          Option(dir.getParent) match {
            case Some(parentDir) => maybeFlacDirectories(parentDir)
            case None => Left(s"$absoluteDirectoryPath is not relative to a datum file.")
          }
        }
      }

      maybeFlacDirectories(absoluteDirectoryPath).flatMap { flacDirectories =>
        val (parentDirectory, dir) = directoryType match {
          case FlacDirectoryType => (flacDirectories.flacPath, "flac")
          case StagingDirectoryType => (flacDirectories.stagingPath, "staging")
        }
        if (absoluteDirectoryPath.startsWith(parentDirectory)) {
          Right(parentDirectory.relativize(absoluteDirectoryPath))
        }
        else {
          Left(s"$absoluteDirectoryPath is not a $dir directory.")
        }
      }
    }
    else {
      Left(s"$absoluteDirectoryPath is not a directory.")
    }
  }.leftMap(NonEmptyList.of(_))

}
