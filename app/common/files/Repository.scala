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

package common.files

import java.nio.file.Path
import java.time.Instant

import cats.data.ValidatedNel
import common.configuration.User
import common.message.{Message, MessageService}
import common.music.Tags

import scala.collection.{SortedMap, SortedSet}

/**
  * A repository is a collection of files on a filesystem. This is the main concept within flac manager as
  * there are four repositories: one for FLAC files, one for staging files, one for encoded files and
  * one for devices that contain symbolic links to encoded files. Defining both different file and directory types
  * for each repository allows for command arguments to be type safe.
  **/
trait Repository[FILE <: File] {

  /**
    * Get the root of the repository if it exits.
    * @return The root or the repository or messages if not found.
    */
  def root: ValidatedNel[Message, Directory[FILE]]

  /**
    * Get a directory within a repository.
    * @param path A relative path.
    * @return The directory within the repository or an error if the path does not point to a directory.
    */
  def directory(path: Path): ValidatedNel[Message, Directory[FILE]]

  /**
    * Get a file within a repository.
    * @param path A relative path.
    * @return The directory within the repository or an error if the path does not point to a file.
    */
  def file(path: Path): ValidatedNel[Message, FILE]
}

/**
  * The repository of FLAC files.
  */
trait FlacRepository extends Repository[FlacFile]

/**
  * The repository for FLAC files that have yet to be checked in.
  */
trait StagingRepository extends Repository[StagingFile]

/**
  * The repository for encoded files.
  */
trait EncodedRepository extends Repository[EncodedFile]

/**
  * A repository for encoded files that are owned by a user.
  */
trait DeviceRepository extends Repository[DeviceFile] {

  /**
    * The user who owns the files in this repository.
    */
  val user: User
}

/**
  * A container for the four repositories
  */
trait Repositories {

  /**
    * Get the FLAC repository.
    * @param messageService The message service used for reporting progress and errors.
    * @return The FLAC repository.
    */
  def flac(implicit messageService: MessageService): FlacRepository

  /**
    * Get the staging repository.
    * @param messageService The message service used for reporting progress and errors.
    * @return The staging repository.
    */
  def staging(implicit messageService: MessageService): StagingRepository

  /**
    * Get the encoded repository.
    * @param messageService The message service used for reporting progress and errors.
    * @return The encoded repository.
    */
  def encoded(implicit messageService: MessageService): EncodedRepository

  /**
    * Get the device repository for a user.
    * @param user The user who owns the device.
    * @param messageService The message service used for reporting progress and errors.
    * @return The user's device repository.
    */
  def device(user: User)(implicit messageService: MessageService): DeviceRepository

}

/**
  * An abstraction of a file. Each file is relative to the root of a repository.
  */
sealed trait File {

  /**
    * True if this file should be read only, false otherwise.
    */
  val readOnly: Boolean

  /**
    * The top level directory for this file. This is the top level directory whose read/write permissions
    * need to be altered to be able to write this file.
    */
  val rootPath: Path

  /**
    * The base directory for this file. This is the root file of the repository.
    */
  val basePath: Path

  /**
    * The relative path of this file. Together with the base path, this comprises the actual path of the file.
    */
  val relativePath: Path

  /**
    * The absolute path of this file.
    */
  val absolutePath: Path

  /**
    * A container for the audio tags for this file.
    */
  val tags: TagsContainer

  /**
    * True if this file is an actual file or false otherwise.
    */
  val exists: Boolean

  /**
    * The last modified time of this file.
    */
  val lastModified: Instant
}

/**
  * Contains orderings for files.
  */
object File {

  /**
    * Order files by their absolute path.
    * @tparam F The type of file to order.
    * @return An ordering that orders file by their absolute path.
    */
  implicit def fileOrdering[F <: File]: Ordering[F] = Ordering.by(_.absolutePath.toString)
}

/**
  * A container for audio tags for a file. This allows for tags to be read only once and evaluated lazily.
  */
trait TagsContainer {

  /**
    * Read the audio tags from a file
    * @return The tags for the file or error messages if they cannot be read.
    */
  def read(): ValidatedNel[Message, Tags]
}

/**
  * A file inside the FLAC repository.
  */
trait FlacFile extends File {

  /**
    * The location of this file should it be checked out.
    * @return The location of this file should it be checked out.
    */
  def toStagingFile: StagingFile

  /**
    * The location of where the encoded version of this file resides.
    * @return The location of where the encoded version of this file resides.
    */
  def toEncodedFile: EncodedFile
}

/**
  * A file in the staging repository.
  */
trait StagingFile extends File {

  /**
    * Determine whether this file is a valid FLAC file.
    * @return True if this file is a FLAC file, false otherwise.
    */
  def isFlacFile: Boolean

  /**
    * Read this file's audio tags and convert it to a file in the FLAC repository.
    * @return Either the relevant FLAC file and its tags or errors.
    */
  def toFlacFileAndTags: ValidatedNel[Message, (FlacFile, Tags)]

  /**
    * Update this staging file with new tags.
    * @param tags The new tags to write.
    * @return This staging file with its new tags.
    */
  def writeTags(tags: Tags): StagingFile
}

/**
  * A file in the encoded repository.
  */
trait EncodedFile extends File {

  /**
    * Create a temporary file that can be used to create this file.
    * @return A new temporary file.
    */
  def toTempFile: TempFile

  /**
    * The corresponding location of this file in a user's device repository.
    * @param user The user who owns the repository.
    * @return The corresponding location of this file in a user's device repository.
    */
  def toDeviceFile(user: User): DeviceFile
}

/**
  * A file in a user's device repository.
  */
trait DeviceFile extends File {

  /**
    * The user who owns the device.
    */
  val user: User
}

/**
  * A temporary file.
  */
trait TempFile extends File {

  /**
    * Write this file's tags to the file system.
    * @return This file with its tags written.
    */
  def writeTags(): TempFile
}

/**
  * A directory within a repository.
  * @tparam FILE The type of files in the repository.
  */
trait Directory[FILE <: File] {

  /**
    * The absolute path of this directory.
    */
  val absolutePath: Path

  /**
    * The path of this directory relative to the repository.
    */
  val relativePath: Path

  /**
    * List all files in this directory and its sub-directories.
    * @return All the files in this directory and its sub-directories.
    */
  def list: SortedSet[FILE]

  /**
    * List all files in this directory and its sub-directories up to a maximum depth.
    * @param maxDepth The maximum depth to search for files.
    * @return All the files in this directory and its sub-directories.
    */
  def list(maxDepth: Int): SortedSet[FILE]

  /**
    * Group all files underneath this directory by their parent directory.
    * @return A map of all the files underneath this directory, grouped by their parent directory.
    */
  def group: SortedMap[Directory[FILE], SortedSet[FILE]]
}

/**
  * Contains type aliases for the four different types of directory as well as ordering.
  */
object Directory {
  type FlacDirectory = Directory[FlacFile]
  type StagingDirectory = Directory[StagingFile]
  type EncodedDirectory = Directory[EncodedFile]
  type DeviceDirectory = Directory[DeviceFile]

  /**
    * Order directories by their absolute path.
    * @tparam F The type of file in the directory.
    * @return An ordering that orders directories by their absolute path.
    */
  implicit def directoryOrdering[F <: File]: Ordering[Directory[F]] = Ordering.by(_.absolutePath.toString)

  /**
    * Order two different types of directory by their absolute path.
    * @tparam F1 The type of files in the first directory.
    * @tparam F2 The type of files in the second directory.
    * @return An ordering that orders directories by their absolute path.
    */
  implicit def eitherDirectoryOrdering[F1 <: File, F2 <: File]: Ordering[Either[Directory[F1], Directory[F2]]] = {
    Ordering.by(_.fold(_.absolutePath.toString, _.absolutePath.toString))
  }
}

