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
import json.RepositoryType.{FlacRepositoryType, StagingRepositoryType}
import json.{PathAndRepository, RepositoryType}

/**
  * An object to work out whether a path is relative to a server's datum file.
  **/
object DirectoryRelativiser {

  /**
    * Attempt to relativise a directory to a directory that contains the datum file.
    * @param datumFilename The name of the server's datum file.
    * @param repositoryTypes Either the staging or flac repository directory.
    * @param directory The directory to attempt to relativise.
    * @return A path relative to the root directory on the server or a list of errors.
    */
  def relativise(
                  datumFilename: String,
                  repositoryTypes: Seq[RepositoryType],
                  directory: Path): Either[NonEmptyList[String], PathAndRepository] = {

    case class DatumBasedFlacDirectories(override val datumPath: Path) extends FlacDirectories

    val absoluteDirectoryPath: Path = directory.toRealPath()
    if (Files.isDirectory(absoluteDirectoryPath)) {
      def maybeFlacDirectories(dir: Path): Either[String, FlacDirectories] = {
        val datumPath: Path = dir.resolve(datumFilename)
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
        val maybePathAndRepository: Option[PathAndRepository] = repositoryTypes.toStream.flatMap { repositoryType =>
          val parentDirectory: Path = repositoryType match {
            case FlacRepositoryType => flacDirectories.flacPath
            case StagingRepositoryType => flacDirectories.stagingPath
          }
          if (absoluteDirectoryPath.startsWith(parentDirectory)) {
            Some(PathAndRepository(parentDirectory.relativize(absoluteDirectoryPath), repositoryType))
          }
          else {
           None
          }
        }.headOption
        maybePathAndRepository.toRight(
          s"$absoluteDirectoryPath is not relative to one of the following repositories: " +
            repositoryTypes.map(_.identifier).mkString(", "))
      }
    }
    else {
      Left(s"$absoluteDirectoryPath is not a directory.")
    }
  }.leftMap(NonEmptyList.of(_))

}
