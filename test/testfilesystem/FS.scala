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

package testfilesystem

import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{FileSystem, Files, LinkOption, Path}
import java.time.Instant
import java.util.UUID

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder
import com.typesafe.scalalogging.StrictLogging
import common.music.{CoverArt, Tags}
import org.specs2.execute.{AsResult, Result}
import org.specs2.specification.ForEach
import play.api.libs.json._

import scala.collection.JavaConverters._
import scala.compat.java8.StreamConverters._
import scala.util.{Failure, Success, Try}
/**
  * Created by alex on 12/06/17
  **/

trait FS[T] extends ForEach[T] {

  def setup(fs: FileSystem): T

  override def foreach[R: AsResult](f: T => R): Result = {
    val fs: FileSystem = MemoryFileSystemBuilder.newLinux().build(s"test-${UUID.randomUUID().toString}")
    try AsResult(f(setup(fs)))
    finally fs.close()
  }
}

object FS {
  private val COVER_ART = CoverArt(Array[Byte](0), "image/jpeg")

  object Builder extends Builder
  trait Builder extends StrictLogging {
    type JFS = FileSystem
    sealed trait FsEntryBuilder
    case class FsReadOnlyBuilder(child: FsEntryBuilder) extends FsEntryBuilder
    case class FsReadWriteBuilder(child: FsEntryBuilder) extends FsEntryBuilder
    case class FsDirectoryBuilder(name: String, children: Seq[FsEntryBuilder]) extends FsEntryBuilder
    case class FsFileBuilder(name: String, maybeTags: Option[Tags]) extends FsEntryBuilder
    case class FsLinkBuilder(name: String, target: String) extends FsEntryBuilder

    def toEntries(fs: JFS, fsEntryBuilder: FsEntryBuilder): Seq[FsEntry] = {
      def _toEntries(path: Path, readOnly: Boolean)(fsEntryBuilder: FsEntryBuilder): Seq[FsEntry] = {
        fsEntryBuilder match {
          case FsReadOnlyBuilder(child) =>
            _toEntries(path, readOnly = true)(child)
          case FsReadWriteBuilder(child) =>
            _toEntries(path, readOnly = false)(child)
          case FsFileBuilder(name, maybeTags) =>
            val childPath = path.resolve(name)
            Seq(FsFile(childPath, readOnly, maybeTags))
          case FsLinkBuilder(name, target) =>
            val childPath = path.resolve(name)
            Seq(FsLink(childPath, path.getFileSystem.getPath(target)))
          case FsDirectoryBuilder(name, children) =>
            val childPath = path.resolve(name)
            FsDirectory(childPath, readOnly) +: children.flatMap(_toEntries(childPath, readOnly))
        }
      }
      _toEntries(fs.getPath("/"), readOnly = false)(fsEntryBuilder)
    }

    def construct(fs: JFS, fsEntryBuilders: Seq[FsEntryBuilder]): Unit = {
      val fsEntries = fsEntryBuilders.flatMap(fsEntryBuilder => toEntries(fs, fsEntryBuilder)).sortBy {
        case _: FsFile => 0
        case _: FsLink => 1
        case _: FsDirectory => 2
      }
      fsEntries.foreach { fsEntry =>
        fsEntry match {
          case FsFile(path, _, maybeTags) =>
            logger.info(s"Creating file $path")
            Files.createDirectories(path.getParent)
            Files.createFile(path)
            maybeTags.foreach { tags =>
              Files.write(path, Json.prettyPrint(tags.toJson(true)).split('\n').toSeq.asJava)
            }
          case FsLink(path, target) =>
            logger.info(s"Creating link $path to $target")
            Files.createDirectories(path.getParent)
            Files.createSymbolicLink(path, target)
          case FsDirectory(path, _) =>
            if (!Files.isDirectory(path)) {
              logger.info(s"Creating directory $path")
              Files.createDirectories(path)
            }
        }
        if (fsEntry.readOnly) {
          val path = fsEntry.path
          logger.info(s"Setting $path to read-only")
          val ps = if (Files.isDirectory(path)) "r-x" else "r--"
          Files.setPosixFilePermissions(path, PosixFilePermissions.fromString(s"$ps$ps$ps"))
          logger.info(s"Path $path now has permissions ${PosixFilePermissions.toString(Files.getPosixFilePermissions(path))}")
        }
      }
    }

    def deconstruct(fs: JFS): Seq[FsEntry] = {
      def _deconstruct(path: Path): Seq[FsEntry] = {
        if (Files.isSymbolicLink(path)) {
          Seq(FsLink(path, Files.readSymbolicLink(path)))
        }
        else if (Files.isRegularFile(path)) {
          val content = Files.readAllLines(path).asScala.mkString("\n").trim
          val maybeTags = Try(Json.parse(content)) match {
            case Success(json) => Tags.fromJson(json).toOption
            case Failure(_) => None
          }
          Seq(FsFile(path, !Files.isWritable(path), maybeTags))
        }
        else if (Files.isDirectory(path)) {
          FsDirectory(path, !Files.isWritable(path)) +: Files.list(path).toScala[Seq].flatMap(_deconstruct)
        }
        else {
          Seq.empty
        }
      }

      _deconstruct(fs.getPath("/")).sorted
    }

    def tags(artist: String, album: String, albumId: String, totalDiscs: Int, discNumber: Int, totalTracks: Int, trackNumber: Int, track: String): Tags = {
      Tags(albumArtistSort = artist, albumArtist = artist, album = album, artist = artist, artistSort = artist, title = track,
        totalDiscs = totalDiscs, totalTracks = totalTracks, discNumber = discNumber, albumArtistId = artist, albumId = albumId, artistId = artist,
        trackId = None, asin = None, trackNumber = trackNumber, coverArt = COVER_ART)
    }
  }
  object Dsl extends Dsl
  trait Dsl extends Builder {
    object D {
      def apply(name: String, childBuilders: FsEntryBuilder*): FsDirectoryBuilder = FsDirectoryBuilder(name, childBuilders)
    }
    object F {
      def apply(name: String, maybeTags: Option[Tags]): FsFileBuilder = FsFileBuilder(name, maybeTags)
      def apply(name: String): FsFileBuilder = apply(name, None)
      def apply(name: String, tags: Tags): FsFileBuilder = apply(name, Some(tags))
    }
    object L {
      def apply(name: String, target: String): FsLinkBuilder = FsLinkBuilder(name, target)
    }
    object RO {
      def apply(child: FsEntryBuilder): FsEntryBuilder = FsReadOnlyBuilder(child)
    }
    object RW {
      def apply(child: FsEntryBuilder): FsEntryBuilder = FsReadWriteBuilder(child)
    }

    implicit class FileSystemExtensions(fs: JFS) {
      def entries: Seq[FsEntry] = deconstruct(fs).filterNot { e =>
        val path = e.path
        path.startsWith("/home") || path.startsWith("/tmp") || path.equals(fs.getPath("/"))
      }
      def add(fsEntryBuilders: FsEntryBuilder*): Unit = {
        construct(fs, fsEntryBuilders)
      }
      def expected(fsEntryBuilders: FsEntryBuilder*): Seq[FsEntry] = {
        fsEntryBuilders.flatMap(fsEntryBuilder => toEntries(fs, fsEntryBuilder))
      }
    }
  }
}

sealed trait FsEntry {
  val path: Path
  val readOnly: Boolean
}

case class FsDirectory(path: Path, readOnly: Boolean) extends FsEntry
case class FsFile(path: Path, readOnly: Boolean, maybeTags: Option[Tags]) extends FsEntry
case class FsLink(path: Path, target: Path) extends FsEntry {
  override val readOnly = false
}

object FsEntry {
  implicit val ordering: Ordering[FsEntry] = Ordering.by(_.path.toAbsolutePath)
}

object LatestModified {
  def apply(times: Seq[Instant]): Instant = {
    if (times.isEmpty) {
      Instant.EPOCH
    }
    else {
      times.max
    }
  }
}