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

package common.files

import java.nio.file.{Files, FileSystem => JFS}
import java.time.{Clock, Instant}

import cats.data.Validated.Valid
import cats.data.{NonEmptyList, ValidatedNel}
import cats.implicits._
import com.typesafe.scalalogging.StrictLogging
import common.configuration.User
import common.files.Extension.{M4A, MP3}
import common.message.Message
import common.music.{CoverArt, Tags}
import org.specs2.mutable.Specification
import org.specs2.specification.core.Fragment
import play.api.libs.json.Json
import testfilesystem.FS.Permissions

import scala.collection.SortedSet
/**
  * Created by alex on 14/06/17
  **/
class RepositoriesImplSpec extends Specification with TestRepositories[FileSystemAndRepositories] with StrictLogging with PathMatchers with RepositoryEntry.Dsl {

  val defaultTags = Tags(albumArtistSort = "albumArtistSort",
    albumArtist = "albumArtist",
    album = "album",
    artist = "artist",
    artistSort = "artistSort",
    title = "title",
    totalDiscs = 1,
    totalTracks = 2,
    discNumber = 3,
    albumArtistId = "albumArtistId",
    albumId = "albumId",
    artistId = "artistId",
    trackId = Some("trackId"),
    asin = Some("asin"),
    trackNumber = 4,
    coverArt = CoverArt(Array[Byte](0), "image/jpeg"))

  def now: Instant = Clock.systemDefaultZone().instant()


  "Reading files in a repository" should {
    case class FilesTestCase[F <: File, R <: Repository[F]](name: String, repositoryFactory: Repositories => R, rootDirs: String*)
    val stagingTestCase = FilesTestCase[StagingFile, StagingRepository]("staging", _.staging, "staging")
    val flacTestCase = FilesTestCase[FlacFile, FlacRepository]("flac", _.flac, "flac")
    val encodedTestCase = FilesTestCase[EncodedFile, EncodedRepository]("encoded", _.encoded(MP3), "encoded", "mp3")
    val devicesTestCase = FilesTestCase[DeviceFile, DeviceRepository]("devices", _.device(User("freddie"), MP3), "devices", "freddie", "mp3")
    def runTests[F <: File, R <: Repository[F]](testCase: FilesTestCase[F, R]): Fragment = {
      val rootPaths = "music" +: testCase.rootDirs
      val rootPath = "/" + rootPaths.mkString("/")
      def R(child: FsEntryBuilder, children: FsEntryBuilder*): FsEntryBuilder = {
        def rootDir(directories: List[String]): NonEmptyList[FsEntryBuilder] = {
          directories match {
            case Nil => NonEmptyList.of(child, children :_*)
            case d :: ds => NonEmptyList.of(D(d, rootDir(ds).toList :_*))
          }
        }
        rootDir(rootPaths.toList).head
      }
      s"be able to identify a directory in the ${testCase.name} repository" in { fsr: FileSystemAndRepositories =>
        val fs = fsr.fs
        val repositories = fsr.repositories
        val repository = testCase.repositoryFactory(repositories)
        fs.add(
          R(
            D("dir")
          )
        )
        repository.directory(fs.getPath("dir")).toEither must beRight { dir: Directory[_] =>
          dir.absolutePath.toString must be_===(s"$rootPath/dir")
          dir.relativePath.toString must be_===(s"dir")
        }
        repository.file(fs.getPath("dir")).toEither must beLeft
      }
      s"be able to identify a file in the ${testCase.name} repository" in { fsr: FileSystemAndRepositories =>
        val fs = fsr.fs
        val repositories = fsr.repositories
        val repository = testCase.repositoryFactory(repositories)
        fs.add(
          R(
            F("file")
          )
        )
        repository.file(fs.getPath("file")).toEither must beRight { f: File =>
          f.absolutePath.toString must be_===(s"$rootPath/file")
          f.relativePath.toString must be_===(s"file")
          f.exists must beTrue
        }
        repository.directory(fs.getPath("file")).toEither must beLeft
      }
      s"be able to list and group files in a directory in the ${testCase.name} repository" in { fsr: FileSystemAndRepositories =>
        val fs = fsr.fs
        val repositories = fsr.repositories
        val repository = testCase.repositoryFactory(repositories)
        fs.add(
          R(
            D("numbers",
              F("one"),
              F("two"),
              D("biggest",
                F("three")
              )
            ),
            D("letters",
              F("a"),
              F("b"),
              D("biggest",
                F("c")
              )
            )
          )
        )
        repository.directory(fs.getPath("")).toEither must beRight { d: Directory[_ <: File] =>
          d.list.map(_.relativePath.toString) must containTheSameElementsAs(
            Seq("letters/a", "letters/b", "letters/biggest/c", "numbers/one", "numbers/two", "numbers/biggest/three")
          )
          d.group.map(kv => kv._1.relativePath.toString -> kv._2.map(_.relativePath.toString)) must havePairs(
            "letters" -> SortedSet("letters/a", "letters/b"),
            "letters/biggest" -> SortedSet("letters/biggest/c"),
            "numbers" -> SortedSet("numbers/one", "numbers/two"),
            "numbers/biggest" -> SortedSet("numbers/biggest/three")
          )
        }
      }
      s"be able to read tags from a file in the ${testCase.name} repository" in { fsr: FileSystemAndRepositories =>
        val fs = fsr.fs
        val repositories = fsr.repositories
        val repository = testCase.repositoryFactory(repositories)
        fs.add(
          R(
            F("file", defaultTags)
          )
        )
        repository.file(fs.getPath("file")).toEither.flatMap(_.tags.read().toEither) must beRight { t: Tags =>
          t must be_===(defaultTags)
        }
        repository.directory(fs.getPath("file")).toEither must beLeft
      }

      s"ignore hidden files and directories in the ${testCase.name} repository" in { fsr: FileSystemAndRepositories =>
        val fs = fsr.fs
        val repositories = fsr.repositories
        val repository = testCase.repositoryFactory(repositories)
        fs.add(
          R(
            D("visible",
              F("F1"),
              F("F2"),
              F(".hidden.invisible")
            ),
            D(".hidden",
              F("thumb.1"),
              F("thumb.2")
            )
          )
        )
        repository.root.toEither must beRight { root: Directory[F] =>
          val validatedExpectedFiles = {
            val empty: ValidatedNel[Message, Seq[F]] = Valid(Seq.empty)
            Seq("F1", "F2").foldLeft(empty) { (acc, filename) =>
              val validatedFile = repository.file(fs.getPath("visible", filename))
              (acc |@| validatedFile).map(_ :+ _)
            }
          }
          validatedExpectedFiles.toEither must beRight { files: Seq[F] =>
            root.list must containTheSameElementsAs(files)
          }
        }
      }
    }
    runTests(stagingTestCase)
    runTests(flacTestCase)
    runTests(encodedTestCase)
    runTests(devicesTestCase)
  }

  "Flac files" should {
    "resolve to staging files and encoded files with the same directory structure" in { fsr: FileSystemAndRepositories =>
      val fs = fsr.fs
      val repositories = fsr.repositories
      fs.flac(
        Artists(
          "Queen" -> Albums(Album("A Night at the Opera",Tracks("Death on Two Legs"))))
      )
      repositories.flac.file(fs.getPath("Q/Queen/A Night at the Opera/01 Death on Two Legs.flac")).toEither must beRight { flacFile: FlacFile =>
        flacFile.toStagingFile.absolutePath.toString must be_===("/music/staging/Q/Queen/A Night at the Opera/01 Death on Two Legs.flac")
        flacFile.toEncodedFile(M4A).absolutePath.toString must be_===("/music/encoded/m4a/Q/Queen/A Night at the Opera/01 Death on Two Legs.m4a")
      }
    }
  }

  "Encoded files" should {
    "resolve to a device file with the same directory structure" in { fsr: FileSystemAndRepositories =>
      val fs = fsr.fs
      val repositories = fsr.repositories
      fs.encoded(
        Artists(
          "Queen" -> Albums(Album("A Night at the Opera", Tracks("Death on Two Legs"))))
      )
      repositories.encoded(M4A).file(fs.getPath("Q/Queen/A Night at the Opera/01 Death on Two Legs.m4a")).toEither must beRight { encodedFile: EncodedFile =>
        encodedFile.toDeviceFile(User("brian")).absolutePath.toString must be_===("/music/devices/brian/m4a/Q/Queen/A Night at the Opera/01 Death on Two Legs.m4a")
      }
    }
    "create a temporary file on demand" in {
      fsr: FileSystemAndRepositories =>
        val fs = fsr.fs
        val repositories = fsr.repositories
        fs.encoded(
          Artists(
            "Queen" -> Albums(Album("A Night at the Opera", Tracks("Death on Two Legs"))))
        )
        repositories.encoded(M4A).file(fs.getPath("Q/Queen/A Night at the Opera/01 Death on Two Legs.m4a")).toEither must beRight { encodedFile: EncodedFile =>
          encodedFile.toTempFile.absolutePath must exist
        }
    }
  }

  "Staging files" should {
    val originalTags = Tags(albumArtistSort = "Queen",
      albumArtist = "albumArtist",
      album = "A Night at the Opera",
      artist = "Queen!",
      artistSort = "artistSort",
      title = "Lazing on a Sunday Afternoon",
      totalDiscs = 2,
      totalTracks = 2,
      discNumber = 3,
      albumArtistId = "albumArtistId",
      albumId = "albumId",
      artistId = "artistId",
      trackId = Some("trackId"),
      asin = Some("asin"),
      trackNumber = 2,
      coverArt = CoverArt(Array[Byte](0), "image/jpeg"))
    val newTags = Tags(albumArtistSort = "Queen",
      albumArtist = "albumArtist",
      album = "A Night at the Opera",
      artist = "Queen!",
      artistSort = "artistSort",
      title = "Lazing on a Sunday Afternoon",
      totalDiscs = 1,
      totalTracks = 2,
      discNumber = 1,
      albumArtistId = "albumArtistId",
      albumId = "albumId",
      artistId = "artistId",
      trackId = Some("trackId"),
      asin = Some("asin"),
      trackNumber = 2,
      coverArt = CoverArt(Array[Byte](0), "image/jpeg"))
    "resolve to a flac file with a directory structure determined by its tags" in { fsr: FileSystemAndRepositories =>
      val fs = fsr.fs
      val repositories = fsr.repositories
      fs.staging(
        Permissions.OwnerReadAndWrite -> F("Lazing on a Sunday Afternoon.flac", originalTags)
      )
      repositories.staging.file(fs.getPath("Lazing on a Sunday Afternoon.flac")).toEither.flatMap(_.toFlacFileAndTags.toEither) must beRight { flacFileAndTags: (FlacFile, Tags) =>
        val (flacFile, tags) = flacFileAndTags
        flacFile.relativePath.toString must be_===("Q/Queen/A Night at the Opera 03/02 Lazing on a Sunday Afternoon.flac")
        tags must be_===(originalTags)
      }
    }
    "be able to write tags" in { fsr: FileSystemAndRepositories =>
      val fs = fsr.fs
      val repositories = fsr.repositories
      fs.staging(
        Permissions.OwnerReadAndWrite -> F("Lazing on a Sunday Afternoon.flac", originalTags)
      )
      val path = fs.getPath("Lazing on a Sunday Afternoon.flac")
      repositories.staging.file(path).toEither.map(_.writeTags(newTags)) must beRight { stagingFile: StagingFile =>
        stagingFile.tags.read().toEither must beRight(newTags)
        Json.parse(Files.readAllBytes(stagingFile.absolutePath)) must be_===(newTags.toJson(true))
      }
    }

  }

  override def generate(fs: JFS, repositories: Repositories): FileSystemAndRepositories =
    FileSystemAndRepositories(fs, repositories)
}

case class FileSystemAndRepositories(fs: JFS, repositories: Repositories)
