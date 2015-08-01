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

import com.wix.accord._
import common.music.Tags._

import scalaz.{Failure => ZFailure, NonEmptyList, Success => ZSuccess, ValidationNel}
/**
 * A trait to add tags to and read tags from files.
 * Created by alex on 02/11/14.
 */
trait TagsService {

  this: {
    def readTags(path: Path): Tags
  } =>

  def read(path: Path): ValidationNel[String, Tags] = {
    val tags = readTags(path)
    validate(tags) match {
      case Success => ZSuccess(tags)
      case Failure(violations) => {
        val descriptions = violations.flatMap(_.description)
        ZFailure(NonEmptyList(descriptions.head, descriptions.tail.toSeq: _*))
      }
    }
  }

  def write(path: Path, tags: Tags): Unit
}
