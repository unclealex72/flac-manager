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

import java.time.Instant

import common.configuration.Directories

/**
 * Created by alex on 16/11/14.
 */
trait TestFileLocationExtensions extends FileLocationExtensions {

  /**
   * Return true if the file location points to a directory, false otherwise.
   * @param fileLocation
   * @return
   */
  def isDirectory(fileLocation: FileLocation): Boolean = false

  def exists(fileLocation: FileLocation): Boolean = false

  def createTemporaryFileLocation(extension: Extension)(implicit directories: Directories): TemporaryFileLocation = null

  def lastModified(fileLocation: FileLocation): Instant = Instant.EPOCH

}
