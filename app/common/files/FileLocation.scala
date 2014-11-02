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

import java.nio.file.{Paths, Path}

import common.configuration.{User, Directories}

/**
 * A class that encapsulates a location of a file within a repository of music
 * files. This is a reusable component because the general actions on files
 * require doing something to a file and then making sure that some sort of
 * change is then rippled up to the base of the repository.
 *
 * @author alex
 *
 */
trait FileLocation {
  /**
   * The base path of the repository.
   */
  val basePath: Path

  /**
   * The location of the file relative to the base path.
   */
  val relativePath: Path

  /**
   * Resolve this file location to its absolute path.
   *
   * @return The absolute path of the file identified by this class.
   */
  def resolve: Path = {
    basePath.resolve(relativePath);
  }

}

/**
 * A `FileLocation` in the staging repository.
 */
trait StagedFlacFileLocation extends FileLocation

/**
 * A `FileLocation` in the FLAC repository.
 */
trait FlacFileLocation extends FileLocation

/**
 * A `FileLocation` in the encoded repository.
 */
trait EncodedFileLocation extends FileLocation

/**
 * A `FileLocation` in the devices repository.
 */
trait DeviceFileLocation extends FileLocation


object FileLocationImplicits {

  implicit def fileLocationOrdering[FL <: FileLocation]: Ordering[FL] = Ordering.by(_.resolve)

  implicit def asAbsolutePath(fileLocation: FileLocation): Path = fileLocation.resolve
}

abstract class AbstractFileLocation(
                                     val name: String,
                                     val relativePath: Path,
                                     val directoryFactory: Directories => Path,
                                     val directories: Directories) {

  val basePath = directoryFactory(directories)

  override def toString: String = s"$name($relativePath)"
}

import PathImplicits._

sealed abstract class AbstractFlacFileLocation(
                                                override val name: String,
                                                override val relativePath: Path,
                                                override val directoryFactory: Directories => Path,
                                                override implicit val directories: Directories) extends AbstractFileLocation(name, relativePath, directoryFactory, directories) {

  def toEncodedFileLocation: EncodedFileLocation = EncodedFileLocation(relativePath withMp3Extension)

  def toOwnedEncodedFileLocation(user: User): DeviceFileLocation = DeviceFileLocation(user, relativePath withMp3Extension)

}

/**
 * Staged Flac files
 * @param relativePath
 * @param directories
 */
case class StagedFlacFileLocationImpl(
                                       override val relativePath: Path,
                                       override val directories: Directories) extends AbstractFlacFileLocation(
  "StagedFlacFileLocation", relativePath, _.stagingPath, directories) with StagedFlacFileLocation

object StagedFlacFileLocation {

  def apply(relativePath: Path)(implicit directories: Directories): StagedFlacFileLocation =
    StagedFlacFileLocationImpl(relativePath, directories)

  def apply(path: String, paths: String*)(implicit directories: Directories): StagedFlacFileLocation =
    StagedFlacFileLocation(Paths.get(path, paths: _*))
}

/**
 * Flac files
 * @param relativePath
 * @param directories
 */
case class FlacFileLocationImpl(
                                 override val relativePath: Path, override val directories: Directories) extends AbstractFlacFileLocation(
  "FlacFileLocation", relativePath, _.flacPath, directories) with FlacFileLocation

object FlacFileLocation {

  def apply(relativePath: Path)(implicit directories: Directories): FlacFileLocation =
    FlacFileLocationImpl(relativePath, directories)

  def apply(path: String, paths: String*)(implicit directories: Directories): FlacFileLocation =
    FlacFileLocation(Paths.get(path, paths: _*))
}

/**
 * Encoded files
 * @param relativePath
 * @param directories
 */
case class EncodedFileLocationImpl(
                                    override val relativePath: Path, override val directories: Directories) extends AbstractFileLocation(
  "EncodedFlacFileLocation", relativePath, _.encodedPath, directories) with EncodedFileLocation

object EncodedFileLocation {

  def apply(relativePath: Path)(implicit directories: Directories): EncodedFileLocation = EncodedFileLocationImpl(relativePath, directories)

  def apply(path: String, paths: String*)(implicit directories: Directories): EncodedFileLocation = EncodedFileLocation(Paths.get(path, paths: _*))
}

/**
 * Owned encoded files
 * @param relativePath
 * @param directories
 */
case class DeviceFileLocationImpl(
                                   val user: User, override val relativePath: Path, override val directories: Directories) extends AbstractFileLocation(
  "OwnedEncodedFlacFileLocation", relativePath, _.devicesPath.resolve(user.name), directories) with DeviceFileLocation

object DeviceFileLocation {
  def apply(user: User, relativePath: Path)(implicit directories: Directories): DeviceFileLocation =
    DeviceFileLocationImpl(user, relativePath, directories)

  def apply(user: User, path: String, paths: String*)(implicit directories: Directories): DeviceFileLocation =
    DeviceFileLocation(user, Paths.get(path, paths: _*))
}

object PathImplicits {

  implicit class WithMp3Extension(path: Path) {

    val FILENAME = """(.+)\..+?""".r

    def withMp3Extension: Path = {
      val parent = path.getParent
      val filename = FILENAME.replaceAllIn(path.getFileName.toString, m => s"${m.group(1)}.mp3")
      parent.resolve(filename)
    }
  }

}