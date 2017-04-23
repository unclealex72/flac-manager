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

package common.configuration

import java.nio.file.Path

/**
  * A common.configuration interface that is used to hold where the various directories are.
  *
  * @author alex
  *
  */
trait FlacDirectories {
  /**
    * Gets the top level path where FLAC files are stored.
    *
    * @return the top level path where FLAC files are stored
    */
  def flacPath: Path = datumPath.resolveSibling("flac")

  /**
    * Gets the top level path where new and altered FLAC files are staged.
    *
    * @return the top level path where new and altered FLAC files are staged
    */
  def stagingPath: Path = datumPath.resolveSibling("staging")

  /**
    * Gets the file that is used by clients to work out what relative directory a file is in
    * @return
    */
  def datumPath: Path
}