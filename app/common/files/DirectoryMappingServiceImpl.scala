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

package common.files

import java.nio.file.{Path, Paths}
import javax.inject.Inject

import common.configuration.Directories
import logging.ApplicationLogging

import scala.annotation.tailrec
import scala.util.Try

/**
  * Created by alex on 17/04/17
  **/
class DirectoryMappingServiceImpl @Inject() (val directories: Directories) extends DirectoryMappingService with ApplicationLogging {

  /**
    * Resolve a set of client side directories into a map of server side directories.
    *
    * @param datumFileLocation The location of the datum file on the client.
    * @return A map of the original directories to the resolved directories
    */
  override def withDatumFileLocation(datumFileLocation: String): String => Path = { localDirectory =>
    val datumFileDirectory = Paths.get(datumFileLocation).getParent
    val localDirectoryPath = Paths.get(localDirectory)
    val maybeRelativeLocalDirectoryPath = Try(datumFileDirectory.relativize(localDirectoryPath)).toOption
    val maybeAbsoluteServerDirectoryPath = for {
      relativeLocalDirectoryPath <- maybeRelativeLocalDirectoryPath
      absoluteServerDirectoryPath <-
        Some(directories.datumPath.getParent.resolve(relativeLocalDirectoryPath)) if isAbsolute(absoluteServerDirectoryPath)
    } yield {
      absoluteServerDirectoryPath
    }
    maybeAbsoluteServerDirectoryPath.getOrElse(localDirectoryPath)
  }

  /**
    * A helper function to check that a resolved path is absolute.
    * @param path
    * @return
    */
  def isAbsolute(path: Path): Boolean = {
    !Range(0, path.getNameCount).map(idx => path.getName(idx).toString).contains("..")
  }
}
