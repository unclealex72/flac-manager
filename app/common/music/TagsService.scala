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

package common.music
import java.nio.file.Path

import cats.data.{Validated, ValidatedNel}
import common.message.Message
import common.message.Messages.{EXCEPTION, INVALID_TAGS}

import scala.util.{Failure, Success, Try}
/**
 * A trait to add tags to and read tags from files.
 * Created by alex on 02/11/14.
 */
trait TagsService {

  /**
    * Read tags from an audio file.
    * @param path The path of the audio file.
    * @return The path's tags.
    */
  def readTags(path: Path): Tags

  /**
    * Read an validate the tags of an audio file.
    * @param path The path of the audio file.
    * @return A validated tags object.
    */
  def read(path: Path): ValidatedNel[Message, Tags] = {
    Try(readTags(path)) match {
      case Success(tags) =>
        Tags.validate(tags).leftMap(errors => errors.map(error => INVALID_TAGS(error)))
      case Failure(e) =>
        Validated.invalidNel(EXCEPTION(e))
    }
  }

  /**
    * Write tags to an audio file.
    * @param path The path of the audio file.
    * @param tags The tags to write.
    */
  def write(path: Path, tags: Tags): Unit
}
