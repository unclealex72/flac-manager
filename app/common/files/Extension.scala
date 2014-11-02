/**
 * Copyright 2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * @author unclealex72
 *
 */

package common.files

import java.nio.file.Path

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

  override def toString = extension
}

case object MP3 extends Extension {
  def extension = "mp3"
}

case object FLAC extends Extension {
  def extension = "flac"
}

object PathImplicits {

  implicit class WithMp3Extension(path: Path) {

    val FILENAME = """^(.+)\..+?$""".r

    def withExtension(extension: Extension): Path = {
      val parent = path.getParent
      val originalFilename = path.getFileName.toString
      val filename = FILENAME.findFirstMatchIn(originalFilename).map { m =>
        s"${m.group(1)}.${extension.extension}"
      }.getOrElse(originalFilename)
      parent.resolve(filename)
    }
  }

}