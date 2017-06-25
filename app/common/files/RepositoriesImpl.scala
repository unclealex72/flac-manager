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
import java.nio.file.{Files, Path}
import java.time.Instant
import javax.inject.Inject

import cats.data.Validated.Valid
import cats.data.{Validated, ValidatedNel}
import common.configuration.{Directories, User}
import common.message.Messages.{FOUND_FILE, NOT_A_DIRECTORY, NOT_A_FILE}
import common.message.{Message, MessageService, Messaging}
import common.music.{Tags, TagsService}

import scala.compat.java8.StreamConverters._
import scala.collection.{SortedMap, SortedSet}
import common.files.Extension._

/**
  * The default implementation of [[Repositories]]
  **/
class RepositoriesImpl @Inject() (val directories: Directories, val tagsService: TagsService, val flacFileChecker: FlacFileChecker) extends Repositories with Messaging {

  override def flac(implicit messageService: MessageService): FlacRepository =
    new RepositoryImpl[FlacFile]("flac", directories.flacPath, new FlacFileImpl(_, new PathTagsContainer(_))) with FlacRepository

  override def staging(implicit messageService: MessageService): StagingRepository =
    new RepositoryImpl[StagingFile]("staging", directories.stagingPath, new StagingFileImpl(_, new PathTagsContainer(_))) with StagingRepository

  override def encoded(implicit messageService: MessageService): EncodedRepository =
    new RepositoryImpl[EncodedFile]("encoded", directories.encodedPath, new EncodedFileImpl(_, new PathTagsContainer(_))) with EncodedRepository

  override def device(user: User)(implicit messageService: MessageService): DeviceRepository = {
    val _user = user
    new RepositoryImpl[DeviceFile]("device", directories.devicesPath.resolve(user.name), new DeviceFileImpl(user, _, new PathTagsContainer(_))) with DeviceRepository {
      val user: User = _user
    }
  }

  class PathTagsContainer(absolutePath: Path)(implicit val messageService: MessageService) extends TagsContainer {
    lazy val tags: ValidatedNel[Message, Tags] = tagsService.read(absolutePath)
    override def read(): ValidatedNel[Message, Tags] = tags
  }

  class StaticTagsContainer(staticTags: Tags) extends TagsContainer {
    val tags: ValidatedNel[Message, Tags] = Validated.valid(staticTags)
    override def read(): ValidatedNel[Message, Tags] = tags
  }

  class FileImpl(val readOnly: Boolean,
                 val rootPath : Path,
                 val basePath: Path,
                 val relativePath: Path,
                 tagsContainerProvider: Path => TagsContainer)(implicit val messageService: MessageService) {

    lazy val absolutePath: Path = basePath.resolve(relativePath)
    lazy val exists: Boolean = Files.isSymbolicLink(absolutePath) || Files.exists(absolutePath)
    lazy val lastModified: Instant = Files.getLastModifiedTime(absolutePath).toInstant
    val tags: TagsContainer = tagsContainerProvider(absolutePath)

    override def equals(other: Any): Boolean = other match {
      case f: File => absolutePath.equals(f.absolutePath)
      case _ => false
    }
    override def hashCode(): Int = absolutePath.hashCode()
    override def toString: String = absolutePath.toString
  }

  class DirectoryImpl[F <: File](val basePath: Path, val relativePath: Path, builder: Path => F)(implicit val messageService: MessageService) extends Directory[F] {
    override val absolutePath: Path = basePath.resolve(relativePath)

    override def toString: String = absolutePath.toString

    override def list: SortedSet[F] = list(Int.MaxValue)

    override def list(maxDepth: Int): SortedSet[F] = {
      val empty: SortedSet[F] = SortedSet.empty
      walk(maxDepth).foldLeft(empty) { (files, directoryOrFile) =>
        val maybeFile = directoryOrFile.fold(_ => None, Some(_))
        files ++ maybeFile
      }
    }

    def walk(maxDepth: Int): Seq[Either[Directory[F], F]] = {
      val pathStream = Files.walk(absolutePath, maxDepth)
      try {
        val paths = pathStream.toScala[Seq]
        paths.map { path =>
          val relativePath = basePath.relativize(path)
          if (Files.isDirectory(path)) {
            Left(new DirectoryImpl[F](basePath, relativePath, builder))
          }
          else {
            val file = builder(relativePath)
            log(FOUND_FILE(file))
            Right(file)
          }
        }
      }
      finally {
        pathStream.close()
      }
    }

    override def group: SortedMap[Directory[F], SortedSet[F]] = {
      case class State(directories: Map[Path, Directory[F]] = Map.empty, groupedFiles: SortedMap[Directory[F], SortedSet[F]] = SortedMap.empty) {
        def directory(directory: Directory[F]): State = copy(directories = directories + (directory.relativePath -> directory))
        def +(file: F): State = {
          directories.get(file.relativePath.getParent) match {
            case Some(directory) =>
              val files = groupedFiles.getOrElse(directory, SortedSet.empty[F]) + file
              copy(groupedFiles = groupedFiles + (directory -> files))
            case None => this
          }
        }
      }
      val state = walk(Int.MaxValue).foldLeft(State()) { (state, directoryOrFile) =>
        directoryOrFile match {
          case Left(directory) => state.directory(directory)
          case Right(file) => state + file
        }
      }
      state.groupedFiles
    }
  }

  class FlacFileImpl(override val relativePath: Path, tagsContainerProvider: Path => TagsContainer)(implicit messageService: MessageService) extends FileImpl(true, directories.flacPath, directories.flacPath, relativePath, tagsContainerProvider)(messageService) with FlacFile {
    override def toStagingFile: StagingFile = {
      new StagingFileImpl(relativePath, _ => tags)
    }

    override def toEncodedFile: EncodedFile = {
      new EncodedFileImpl(relativePath.withExtension(MP3), _ => tags)
    }
  }

  class StagingFileImpl(override val relativePath: Path, tagsContainerProvider: Path => TagsContainer)(implicit messageService: MessageService) extends FileImpl(false, directories.stagingPath, directories.stagingPath, relativePath, tagsContainerProvider)(messageService) with StagingFile {
    override def isFlacFile: Boolean = flacFileChecker.isFlacFile(absolutePath)

    override def toFlacFileAndTags: ValidatedNel[Message, (FlacFile, Tags)] = {
      tags.read().map { tags =>
        val flacFile = new FlacFileImpl(tags.asPath(relativePath.getFileSystem, FLAC), _ => new StaticTagsContainer(tags))
        (flacFile, tags)
      }
    }

    override def writeTags(tags: Tags): StagingFile = {
      tagsService.write(absolutePath, tags)
      new StagingFileImpl(relativePath, _ => new StaticTagsContainer(tags))
    }
  }

  class EncodedFileImpl(override val relativePath: Path, tagsContainerProvider: Path => TagsContainer)(implicit messageService: MessageService) extends FileImpl(true, directories.encodedPath, directories.encodedPath, relativePath, tagsContainerProvider)(messageService) with EncodedFile {
    override def toTempFile: TempFile = {
      val baseDirectory = directories.temporaryPath
      val tempPath = Files.createTempFile(baseDirectory, "flacmanager-encoding-", MP3.extension)
      new TempFileImpl(baseDirectory, baseDirectory.resolve(tempPath), _ => tags)
    }

    override def toDeviceFile(user: User): DeviceFile = new DeviceFileImpl(user, relativePath, _ => tags)
  }

  class DeviceFileImpl(val user: User, override val relativePath: Path, tagsContainerProvider: Path => TagsContainer)(implicit messageService: MessageService) extends FileImpl(true, directories.devicesPath, directories.devicesPath.resolve(user.name), relativePath, tagsContainerProvider)(messageService) with DeviceFile {
  }

  class TempFileImpl(override val basePath: Path, override val relativePath: Path, tagsContainerProvider: Path => TagsContainer)(implicit messageService: MessageService) extends FileImpl(false, basePath, basePath, relativePath, tagsContainerProvider)(messageService) with TempFile {
    override def writeTags(): TempFile = {
      tags.read().foreach { tags =>
        tagsService.write(absolutePath, tags)
      }
      new TempFileImpl(basePath: Path, relativePath, _ => tags)
    }
  }
  class RepositoryImpl[F <: File](val repositoryType: String, basePath: Path, builder: Path => F)(implicit val messageService: MessageService) extends Repository[F] {
    override def root: ValidatedNel[Message, Directory[F]] = {
      directory(basePath.relativize(basePath))
    }

    override def directory(path: Path): ValidatedNel[Message, Directory[F]] = {
      val newDirectory = new DirectoryImpl[F](basePath, path, builder)
      if (Files.isDirectory(newDirectory.absolutePath)) {
        Valid(newDirectory)
      }
      else {
        Validated.invalidNel(NOT_A_DIRECTORY(path, repositoryType))
      }
    }

    override def file(path: Path): ValidatedNel[Message, F] = {
      val newFile = builder(path)
      if (Files.isDirectory(newFile.absolutePath)) {
        Validated.invalidNel(NOT_A_FILE(path, repositoryType))
      }
      else {
        Valid(newFile)
      }
    }
  }
}
