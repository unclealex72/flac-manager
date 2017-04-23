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

import scala.util.matching.Regex

/**
 * An enumeration of all the known music file extensions.
 * @author alex
 *
 */
sealed trait Extension {

  /**
   * The string to put at the end of a file.
   * @return
   */
  def extension: String

  override def toString: String = extension
}

case object MP3 extends Extension {
  def extension = "mp3"
}

case object FLAC extends Extension {
  def extension = "flac"
}

object PathImplicits {

  implicit class WithMp3Extension(path: Path) {

    val FILENAME: Regex = """^(.+)\..+?$""".r

    def withExtension(extension: Extension): Path = {
      val parent = path.getParent
      val originalFilename = path.getFileName.toString
      val filename = FILENAME.findFirstMatchIn(originalFilename).map { m =>
        s"${m.group(1)}.${extension.extension}"
      }.getOrElse(originalFilename)
      parent.resolve(filename)
    }

    def hasExtension(extension: Extension): Boolean = {
      path.getFileName.toString.endsWith(s".$extension")
    }
  }

}