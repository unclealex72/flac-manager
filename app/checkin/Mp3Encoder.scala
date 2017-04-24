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

package checkin

import java.nio.file.Path

/**
  * Encode a flac file into an MP3 file.
  */
trait Mp3Encoder {

  /**
   * Encode a flac file into an MP3 file.
   * @param source The source flac file.
   * @param target The target MP3 file.
   */
  def encode(source: Path, target: Path)
}
