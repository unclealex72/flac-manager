/*
 * Copyright 2018 Alex Jones
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

import java.nio.file.attribute.PosixFilePermission._
import java.nio.file.attribute.{PosixFilePermission, PosixFilePermissions}
import java.nio.file.{FileSystem, Files, Path}
import java.time.Instant
import java.util.UUID

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder
import com.typesafe.scalalogging.StrictLogging
import common.music.{CoverArt, Tags}
import org.specs2.execute.{AsResult, Result}
import org.specs2.matcher.{Matcher, Matchers}
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
    val fs: FileSystem = MemoryFileSystemBuilder.newLinux().setUmask(
      Set(GROUP_READ, GROUP_WRITE, GROUP_EXECUTE, OTHERS_READ, OTHERS_WRITE, OTHERS_EXECUTE).asJava).
      build(s"test-${UUID.randomUUID().toString}")
    try AsResult(f(setup(fs)))
    finally fs.close()
  }
}

object FS {
  private val COVER_ART = CoverArt(Array[Byte](0), "image/jpeg")

  sealed trait Permissions {
    val filePosixPermissions: Set[PosixFilePermission]
    lazy val directoryPosixPermissions: Set[PosixFilePermission] = filePosixPermissions.flatMap { permission =>
      val maybeExtraPermission: Option[PosixFilePermission] = permission match {
        case PosixFilePermission.OWNER_READ => Some(PosixFilePermission.OWNER_EXECUTE)
        case PosixFilePermission.GROUP_READ => Some(PosixFilePermission.GROUP_EXECUTE)
        case PosixFilePermission.OTHERS_READ => Some(PosixFilePermission.OTHERS_EXECUTE)
        case _ => None
      }
      maybeExtraPermission.toSeq :+ permission
    }
  }

  object Permissions {
    object OwnerReadOnly extends Permissions {
      override val filePosixPermissions = Set(PosixFilePermission.OWNER_READ)
    }
    object AllReadOnly extends Permissions {
      override val filePosixPermissions = Set(PosixFilePermission.OWNER_READ, PosixFilePermission.GROUP_READ, PosixFilePermission.OTHERS_READ)
    }
    object OwnerReadAndWrite extends Permissions {
      override val filePosixPermissions: Set[PosixFilePermission] = Set(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE)
    }
    object OwnerWriteAllRead extends Permissions {
      override val filePosixPermissions: Set[PosixFilePermission] = Set(AllReadOnly, OwnerReadAndWrite).flatMap(_.filePosixPermissions)
    }
  }

  object Builder extends Builder
  trait Builder extends StrictLogging {
    type JFS = FileSystem
    sealed trait FsEntryBuilder {
      val name: String
    }
    case class FsDirectoryBuilder(name: String, permissions: Permissions, children: Seq[FsEntryBuilder]) extends FsEntryBuilder
    case class FsFileBuilder(name: String, permissions: Permissions, maybeTags: Option[Tags]) extends FsEntryBuilder
    case class FsLinkBuilder(name: String, target: String) extends FsEntryBuilder

    def construct(fs: JFS, fsEntryBuilders: Seq[FsEntryBuilder]): Unit = {
      def _construct(root: Path, fsEntryBuilders: Seq[FsEntryBuilder]): Unit = {
        fsEntryBuilders.foreach {
          case FsFileBuilder(name, permissions, maybeTags) =>
            val path: Path = root.resolve(name)
            logger.info(s"Creating file $path")
            Files.createFile(path)
            maybeTags.foreach { tags =>
              Files.write(path, Json.prettyPrint(tags.toJson(true)).split('\n').toSeq.asJava)
            }
            Files.setPosixFilePermissions(path, permissions.filePosixPermissions.asJava)
          case FsLinkBuilder(name, target) =>
            val path: Path = root.resolve(name)
            logger.info(s"Creating link $path to $target")
            Files.createSymbolicLink(path, fs.getPath(target))
          case FsDirectoryBuilder(name, permissions, children) =>
            val path: Path = root.resolve(name)
            logger.info(s"Creating directory $path")
            Files.createDirectory(path)
            _construct(path, children)
            Files.setPosixFilePermissions(path, permissions.directoryPosixPermissions.asJava)
        }
      }
      _construct(fs.getPath("/"), fsEntryBuilders)
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
      def apply(name: String, childBuilders: FsEntryBuilder*): FsDirectoryBuilder = FsDirectoryBuilder(name, Permissions.OwnerWriteAllRead, childBuilders)
      def apply(name: String, permissions: Permissions, childBuilders: FsEntryBuilder*): FsDirectoryBuilder = FsDirectoryBuilder(name, permissions, childBuilders)
    }
    object F {
      def apply(name: String, maybeTags: Option[Tags]): FsFileBuilder = FsFileBuilder(name, Permissions.OwnerWriteAllRead, maybeTags)
      def apply(name: String): FsFileBuilder = apply(name, None)
      def apply(name: String, tags: Tags): FsFileBuilder = apply(name, Some(tags))
      def apply(name: String, permissions: Permissions, maybeTags: Option[Tags]): FsFileBuilder = FsFileBuilder(name, permissions, maybeTags)
      def apply(name: String, permissions: Permissions): FsFileBuilder = apply(name, permissions, None)
      def apply(name: String, permissions: Permissions, tags: Tags): FsFileBuilder = apply(name, permissions, Some(tags))
    }
    object L {
      def apply(name: String, target: String): FsLinkBuilder = FsLinkBuilder(name, target)
    }

    implicit class FileSystemExtensions(fs: JFS) {
      val blacklist: Seq[String] = Seq("tmp", "home")
      def entries: Seq[FsEntry] = {
        def list(path: Path): Seq[Path] = Files.list(path).toScala[Seq]
        def deconstruct(path: Path): Seq[FsEntry] = {
          if (Files.isSymbolicLink(path)) {
            Seq(FsLink(path, Files.readSymbolicLink(path)))
          }
          else {
            val permissions: Set[PosixFilePermission] = Files.getPosixFilePermissions(path).asScala.toSet
            if (Files.isRegularFile(path)) {
              val content: String = Files.readAllLines(path).asScala.mkString("\n").trim
              val maybeTags: Option[Tags] = Try(Json.parse(content)) match {
                case Success(json) => Tags.fromJson(json).toOption
                case Failure(_) => None
              }
              Seq(FsFile(path, permissions, maybeTags))
            }
            else if (Files.isDirectory(path)) {
              val children: Seq[FsEntry] = list(path).flatMap(deconstruct).sorted
              Seq(FsDirectory(path, permissions, children))
            }
            else {
              Seq.empty
            }
          }
        }
        list(fs.getPath("/")).flatMap(deconstruct).filterNot(entry => blacklist.contains(entry.path.getFileName.toString))
      }

      def add(fsEntryBuilders: FsEntryBuilder*): Unit = {
        construct(fs, fsEntryBuilders)
      }
      def expected(fsEntryBuilders: FsEntryBuilder*): Seq[FsEntry] = {
        def toEntries(root: Path, fsEntryBuilders: Seq[FsEntryBuilder]): Seq[FsEntry] = {
          fsEntryBuilders.flatMap {
            case FsFileBuilder(name, permissions, maybeTags) =>
              Seq(FsFile(root.resolve(name), permissions.filePosixPermissions, maybeTags))
            case FsLinkBuilder(name, target) =>
              Seq(FsLink(root.resolve(name), fs.getPath(target)))
            case FsDirectoryBuilder(name, permissions, children) =>
              val path: Path = root.resolve(name)
              val childEntries: Seq[FsEntry] = toEntries(path, children)
              Seq(FsDirectory(path, permissions.directoryPosixPermissions, childEntries.sorted))
          }
        }
        fsEntryBuilders.flatMap(fsEntryBuilder => toEntries(fs.getPath("/"), Seq(fsEntryBuilder))).sorted
      }
    }
  }
}

sealed trait FsEntry {
  val path: Path
  def toJson(includePermissions: Boolean): JsValue

  protected def permissionsToEntries(posixFilePermissions: Set[PosixFilePermission], include: Boolean): Map[String, JsValue] =
    if (include) {
      Map("permissions" -> JsString(PosixFilePermissions.toString(posixFilePermissions.asJava)))
    }
    else {
      Map.empty
    }

  protected def pathToEntries(path: Path): Map[String, JsValue] = Map("name" -> JsString(path.getFileName.toString))

}

case class FsDirectory(path: Path, posixFilePermissions: Set[PosixFilePermission], children: Seq[FsEntry]) extends FsEntry {
  override def toJson(includePermissions: Boolean): JsValue = {
    val childObjects: JsArray = JsArray(children.map(_.toJson(includePermissions)))
    JsObject(pathToEntries(path) ++ permissionsToEntries(posixFilePermissions, includePermissions) + ("children" -> childObjects))
  }
}
case class FsFile(path: Path, posixFilePermissions: Set[PosixFilePermission], maybeTags: Option[Tags]) extends FsEntry {
  override def toJson(includePermissions: Boolean): JsValue = {
    JsObject(pathToEntries(path) ++ permissionsToEntries(posixFilePermissions, includePermissions) ++ maybeTags.map(tags => "tags" -> tags.toJson(false)))
  }
}
case class FsLink(path: Path, target: Path) extends FsEntry {
  override def toJson(includePermissions: Boolean): JsValue = {
    JsObject(pathToEntries(path) + ("target" -> JsString(target.toString)))
  }
}

object FsEntry {
  implicit val ordering: Ordering[FsEntry] = Ordering.by(_.path.getFileName.toString)
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

trait FsEntryMatchers extends Matchers {

  def haveTheSameEntriesAs(fsEntries: Seq[FsEntry]): Matcher[Seq[FsEntry]] = {
    def toJson(entries: Seq[FsEntry]): String = Json.prettyPrint(Json.arr(entries.sorted.map(_.toJson(true))))
    be_==(toJson(fsEntries)) ^^ { (entries: Seq[FsEntry]) => toJson(entries)}
  }

  def haveTheSameEntriesAsIgnoringPermissions(fsEntries: Seq[FsEntry]): Matcher[Seq[FsEntry]] = {
    def toJson(entries: Seq[FsEntry]): String = Json.prettyPrint(Json.arr(entries.sorted.map(_.toJson(false))))
    be_==(toJson(fsEntries)) ^^ { (entries: Seq[FsEntry]) => toJson(entries)}
  }

}