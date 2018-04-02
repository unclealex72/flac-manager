/*
 * Copyright 2018 Alex Jones
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

package checkin

import java.nio.file.Path

import common.files.Extension

/**
  * Encode a flac file into a lossy file.
  */
trait LossyEncoder {

  /**
   * Encode a flac file into a lossy file.
   * @param source The source flac file.
   * @param target The target lossy file.
   */
  def encode(source: Path, target: Path): Int

  /**
    * The lossy format that this encoder generates.
    * @return The lossy format that this encoder generates.
    */
  val encodesTo: Extension

  /**
    * True if this encoder copies tags from the source flac file, false otherwise.
    * @return
    */
  val copiesTags: Boolean
}
