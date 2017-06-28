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

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable
import scala.util.matching.Regex

/**
 * An enumeration of all the known music file extensions.
 * @author alex
 *
 */
sealed trait Extension extends EnumEntry {

  /**
   * The string to put at the end of a file.
   * @return
   */
  def extension: String

  /**
    * The extension itself.
    * @return The extension itself.
    */
  override def toString: String = extension
}

/**
  * A container for the different extensions.
  */
object Extension extends Enum[Extension] {

  /**
    * The extension for M4A files.
    */
  case object M4A extends Extension {

    /**
      * @inheritdoc
      */
    override def extension = "m4a"
  }

  /**
    * The extension for flac files.
    */
  case object FLAC extends Extension {

    /**
      * @inheritdoc
      */
    override def extension = "flac"
  }

  /**
    * Get all known extensions.
    * @return All known extensions.
    */
  override def values: immutable.IndexedSeq[Extension] = findValues

  /**
    * Implicits for [[Path]]s that supply functionality to check the extension of a path or to change it.
    * @param path The path to extend.
    */
  implicit class PathExtensions(path: Path) {

    private val FILENAME: Regex = """^(.+)\..+?$""".r

    /**
      * Change the extension of a path.
      * @param extension The new extension of the path.
      * @return A path with the given extension, replacing any other extension.
      */
    def withExtension(extension: Extension): Path = {
      val parent = path.getParent
      val originalFilename = path.getFileName.toString
      val filename = FILENAME.findFirstMatchIn(originalFilename).map { m =>
        s"${m.group(1)}.${extension.extension}"
      }.getOrElse(originalFilename)
      parent.resolve(filename)
    }

    /**
      * Check to see if a path has an extension.
      * @param extension The extension to look for.
      * @return True if the path has the extension, false otherwise.
      */
    def hasExtension(extension: Extension): Boolean = {
      path.getFileName.toString.endsWith(s".$extension")
    }
  }
}
