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
  * A repository is a collection of files on a filesystem
  **/
trait Repository[FILE <: File] {

  def root: ValidatedNel[Message, Directory[FILE]]
  def directory(path: Path): ValidatedNel[Message, Directory[FILE]]

  def file(path: Path): ValidatedNel[Message, FILE]
}

trait FlacRepository extends Repository[FlacFile]
trait StagingRepository extends Repository[StagingFile]
trait EncodedRepository extends Repository[EncodedFile]
trait DeviceRepository extends Repository[DeviceFile] {
  val user: User
}

trait Repositories {
  def flac(implicit messageService: MessageService): FlacRepository
  def staging(implicit messageService: MessageService): StagingRepository
  def encoded(implicit messageService: MessageService): EncodedRepository
  def device(user: User)(implicit messageService: MessageService): DeviceRepository

}

trait Directory[FILE <: File] {

  val absolutePath: Path

  val relativePath: Path

  def list: SortedSet[FILE]

  def list(maxDepth: Int): SortedSet[FILE]

  def group: SortedMap[Directory[FILE], SortedSet[FILE]]
}

object Directory {
  type FlacDirectory = Directory[FlacFile]
  type StagingDirectory = Directory[StagingFile]
  type EncodedDirectory = Directory[EncodedFile]
  type DeviceDirectory = Directory[DeviceFile]

  implicit def directoryOrdering[F <: File]: Ordering[Directory[F]] = Ordering.by(_.absolutePath.toString)

  implicit def eitherDirectoryOrdering[F1 <: File, F2 <: File]: Ordering[Either[Directory[F1], Directory[F2]]] = {
    Ordering.by(_.fold(_.absolutePath.toString, _.absolutePath.toString))
  }
}

sealed trait File {

  val readOnly: Boolean

  val rootPath: Path

  val basePath: Path

  val relativePath: Path

  val absolutePath: Path

  val tags: TagsContainer

  val exists: Boolean

  val lastModified: Instant
}

object File {
  implicit def fileOrdering[F <: File]: Ordering[F] = Ordering.by(_.absolutePath.toString)
}

trait TagsContainer {

  def read(): ValidatedNel[Message, Tags]
}

trait FlacFile extends File {
  def toStagingFile: StagingFile
  def toEncodedFile: EncodedFile
}

trait StagingFile extends File {
  def isFlacFile: Boolean
  def toFlacFileAndTags: ValidatedNel[Message, (FlacFile, Tags)]
  def writeTags(tags: Tags): StagingFile
}

trait EncodedFile extends File {
  def toTempFile: TempFile
  def toDeviceFile(user: User): DeviceFile
}

trait DeviceFile extends File {
  val user: User
}

trait TempFile extends File {
  def writeTags(): TempFile
}