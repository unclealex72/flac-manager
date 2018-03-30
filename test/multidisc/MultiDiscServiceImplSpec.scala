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

package multidisc

import java.nio.file.FileSystem

import cats.data.NonEmptyList
import com.typesafe.scalalogging.StrictLogging
import common.files._
import common.message.Message
import common.music.Tags
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import testfilesystem.FS.Permissions
import testfilesystem._
/**
  * Created by alex on 24/05/17
  **/
class MultiDiscServiceImplSpec extends Specification with StrictLogging with
  TestRepositories[FileSystemRepositoriesAndMultiDiscService] with RepositoryEntry.Dsl {

  def populateFileSystem(fs: FileSystem): Unit = {
    fs.staging(
      Permissions.OwnerReadAndWrite,
      Artists("Queen" ->
        Albums(
          Album("A Night at the Opera",
            Discs(
              Tracks("Death on Two Legs", "Lazing on a Sunday Afternoon"),
              Tracks("I'm in Love with my Car", "You're my Best Friend", "39")
            ).withSameId
          ),
          Album("Jazz", Tracks("Mustapha"))
        )
      )
    )
  }

  case class ExpectedTags(path: String, joined: Tags, split: Tags)

  val DEATH_ON_TWO_LEGS: ExpectedTags = ExpectedTags(
    "01 Death on Two Legs.flac",
    tags(artist = "Queen", album = "A Night at the Opera", albumId = "A Night at the Opera", track = "Death on Two Legs", discNumber = 1, trackNumber = 1, totalDiscs = 1, totalTracks = 5),
    tags(artist = "Queen", album = "A Night at the Opera", albumId = "A Night at the Opera", track = "Death on Two Legs", discNumber = 1, trackNumber = 1, totalDiscs = 1, totalTracks = 2)
  )

  val LAZING_ON_A_SUNDAY_AFTERNOON: ExpectedTags = ExpectedTags(
    "02 Lazing on a Sunday Afternoon.flac",
    tags(artist = "Queen", album = "A Night at the Opera", albumId = "A Night at the Opera", track = "Lazing on a Sunday Afternoon", discNumber = 1, trackNumber = 2, totalDiscs = 1, totalTracks = 5),
    tags(artist = "Queen", album = "A Night at the Opera", albumId = "A Night at the Opera", track = "Lazing on a Sunday Afternoon", discNumber = 1, trackNumber = 2, totalDiscs = 1, totalTracks = 2)
  )

  val IM_IN_LOVE_WITH_MY_CAR: ExpectedTags = ExpectedTags(
    "01 I'm in Love with my Car.flac",
    tags(artist = "Queen", album = "A Night at the Opera", albumId = "A Night at the Opera", track = "I'm in Love with my Car", discNumber = 1, trackNumber = 3, totalDiscs = 1, totalTracks = 5),
    tags(artist = "Queen", album = "A Night at the Opera (Extras)", albumId = "A Night at the Opera_EXTRAS", track = "I'm in Love with my Car", discNumber = 1, trackNumber = 1, totalDiscs = 1, totalTracks = 3)
  )

  val YOURE_MY_BEST_FRIEND: ExpectedTags = ExpectedTags(
    "02 You're my Best Friend.flac",
    tags(artist = "Queen", album = "A Night at the Opera", albumId = "A Night at the Opera", track = "You're my Best Friend", discNumber = 1, trackNumber = 4, totalDiscs = 1, totalTracks = 5),
    tags(artist = "Queen", album = "A Night at the Opera (Extras)", albumId = "A Night at the Opera_EXTRAS", track = "You're my Best Friend", discNumber = 1, trackNumber = 2, totalDiscs = 1, totalTracks = 3)
  )

  val THIRTY_NINE: ExpectedTags = ExpectedTags(
    "03 39.flac",
    tags(artist = "Queen", album = "A Night at the Opera", albumId = "A Night at the Opera", track = "39", discNumber = 1, trackNumber = 5, totalDiscs = 1, totalTracks = 5),
    tags(artist = "Queen", album = "A Night at the Opera (Extras)", albumId = "A Night at the Opera_EXTRAS", track = "39", discNumber = 1, trackNumber = 3, totalDiscs = 1, totalTracks = 3)
  )

  val MUSTAPHA: ExpectedTags = ExpectedTags(
    "01 Mustapha.flac",
    tags(artist = "Queen", album = "Jazz", albumId = "Jazz", track = "Mustapha", discNumber = 1, trackNumber = 1, totalDiscs = 1, totalTracks = 1),
    tags(artist = "Queen", album = "Jazz", albumId = "Jazz", track = "Mustapha", discNumber = 1, trackNumber = 1, totalDiscs = 1, totalTracks = 1)
  )

  def executeTest(validatedResults: Either[NonEmptyList[Message], Seq[FsEntry]], expectedTags: ExpectedTags, tagExtractor: ExpectedTags => Tags): MatchResult[Option[Tags]] = {
    validatedResults must beRight
    def taggedEntries(entries: Seq[FsEntry]): Seq[Tags] = {
      entries.flatMap {
        case FsFile(path, _, Some(fileTags)) if path.getFileName.toString == expectedTags.path => Seq(fileTags)
        case FsDirectory(_, _, children) => taggedEntries(children)
        case _ => Seq.empty
      }
    }
    val entries: Seq[FsEntry] = validatedResults.right.get
    val tags: Seq[Tags] = taggedEntries(entries)
    tags.headOption must beSome { tags: Tags =>
      tags must be_===(tagExtractor(expectedTags))
    }
  }

  "Joining multi-disc albums" should {
    "create one album with all the tracks on one disc" in { fsrmds: FileSystemRepositoriesAndMultiDiscService =>
      val fs = fsrmds.fs
      val repositories = fsrmds.repositories
      val multiDiscService = fsrmds.multiDiscService
      val validatedResults = for {
        root <- repositories.staging.root.toEither
        _ <- multiDiscService.createSingleAlbum(Seq(root)).toEither
      } yield {
        fs.entries
      }
      executeTest(validatedResults, DEATH_ON_TWO_LEGS, _.joined)
      executeTest(validatedResults, LAZING_ON_A_SUNDAY_AFTERNOON, _.joined)
      executeTest(validatedResults, IM_IN_LOVE_WITH_MY_CAR, _.joined)
      executeTest(validatedResults, YOURE_MY_BEST_FRIEND, _.joined)
      executeTest(validatedResults, THIRTY_NINE, _.joined)
      executeTest(validatedResults, MUSTAPHA, _.joined)
    }
  }

  "Splitting multi-disc albums" should {
    "Create two albums" in { fsrmds: FileSystemRepositoriesAndMultiDiscService =>
      val fs = fsrmds.fs
      val repositories = fsrmds.repositories
      val multiDiscService = fsrmds.multiDiscService
      val validatedResults = for {
        root <- repositories.staging.root.toEither
        _ <- multiDiscService.createAlbumWithExtras(Seq(root)).toEither
      } yield {
        fs.entries
      }
      executeTest(validatedResults, DEATH_ON_TWO_LEGS, _.split)
      executeTest(validatedResults, LAZING_ON_A_SUNDAY_AFTERNOON, _.split)
      executeTest(validatedResults, IM_IN_LOVE_WITH_MY_CAR, _.split)
      executeTest(validatedResults, YOURE_MY_BEST_FRIEND, _.split)
      executeTest(validatedResults, THIRTY_NINE, _.split)
      executeTest(validatedResults, MUSTAPHA, _.split)
    }
  }

  override def generate(fs: FileSystem, repositories: Repositories): FileSystemRepositoriesAndMultiDiscService = {
    populateFileSystem(fs)
    FileSystemRepositoriesAndMultiDiscService(fs, repositories, new MultiDiscServiceImpl)
  }
}

case class FileSystemRepositoriesAndMultiDiscService(fs: FileSystem, repositories: Repositories, multiDiscService: MultiDiscService)