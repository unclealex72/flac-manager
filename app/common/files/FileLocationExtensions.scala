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

package common.files

import java.nio.file.Path
import java.time.Instant

import common.configuration.Directories

/**
 * A trait that wraps [[Path]] like functionality for [[FileLocation]]s.
 */
trait FileLocationExtensions {

  /**
   * Return true if the file location points to a directory, false otherwise.
   * @param fileLocation The file location to check.
   * @return True if the file location is a directory, false otherwise.
   */
  protected[files] def isDirectory(fileLocation: FileLocation): Boolean

  /**
    * Check to see if a file location exists.
    * @param fileLocation The file location to look for.
    * @return True if the file exists, false otherwise.
    */
  protected[files] def exists(fileLocation: FileLocation): Boolean

  /**
    * Create a new temporary file.
    * @param extension The extension to give the file.
    * @param directories The locations of the repositories.
    * @return A new temporary file with the given extension.
    */
  protected[files] def createTemporaryFileLocation(extension: Extension)
                                                  (implicit directories: Directories): TemporaryFileLocation

  /**
    * Get the time a file location was last modified.
    * @param fileLocation The file location to check.
    * @return The number of milliseconds since the UNIX Epoch when the file was last modified.
    */
  protected[files] def lastModified(fileLocation: FileLocation): Instant

  /**
    * Get the first file in a directory that has a given extension.
    * @param parentFileLocation The location of the directory.
    * @param extension The extension to look for.
    * @param builder A function to turn the found path back in to a file location.
    * @tparam F A type of file location.
    * @return The first file location with the given extension in the directory or none.
    */
  protected[files] def firstFileIn[F <: FileLocation](
                                                       parentFileLocation: F,
                                                       extension: Extension,
                                                       builder: Path => F): Option[F]
}
