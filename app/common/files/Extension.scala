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
import play.api.mvc.PathBindable

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
    * True if this format is lossy, false otherwise.
    * @return
    */

  def lossy: Boolean
  /**
    * The extension itself.
    * @return The extension itself.
    */
  override def toString: String = extension

  /**
    * The mime type associated with this extension.
    * @return The extension itself.
    */
  def mimeType: String = extension
}

/**
  * A container for the different extensions.
  */
object Extension extends Enum[Extension] {

  /**
    * The base for all extensions.
    * @param extension The extension string.
    * @param mimeType The mime type of the extension.
    * @param lossy True if this encoding is lossy, false otherwise.
    */
  sealed case class ExtensionImpl(
                                   override val extension: String,
                                   override val mimeType: String,
                                   override val lossy: Boolean) extends Extension

  /**
    * The extension for M4A files.
    */
  object M4A extends ExtensionImpl("m4a", "audio/m4a", true)

  /**
    * The extension for MP3 files.
    */

  object MP3 extends ExtensionImpl("mp3", "audio/mpeg", true)
  /**
    * The extension for flac files.
    */
  object FLAC extends ExtensionImpl("flac", "audio/flac", false) {

  }

  /**
    * Get all known extensions.
    * @return All known extensions.
    */
  override def values: immutable.IndexedSeq[Extension] = findValues

  val lossyValues: Seq[Extension] = values.filter(_.lossy)

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
      val parent: Path = path.getParent
      val originalFilename: String = path.getFileName.toString
      val filename: String = FILENAME.findFirstMatchIn(originalFilename).map { m =>
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

    /**
      * Get the extension of this file.
      * @return The extension of this file if it has a known extension, none otherwise.
      */
    def extension: Option[Extension] = {
      Extension.values.find(hasExtension)
    }
  }

  /**
    * Allow extensions to be referenced in URLs.
    * @return A path binder allowing extensions to be referenced in URLs.
    */
  implicit val pathBinder: PathBindable[Extension] = new PathBindable[Extension] {
    override def bind(key: String, value: String): Either[String, Extension] = {
      Extension.lossyValues.find(_.extension == value).toRight(s"$value is not a valid extension.")
    }
    override def unbind(key: String, value: Extension): String = value.extension
  }

}
