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

import java.nio.file.{Path, Paths}

import cats.data.Validated
import common.configuration.{Directories, TestDirectories}
import common.files.{DirectoryService, FlacFileChecker, StagedFlacFileLocation}
import common.message.NoOpMessageService
import common.music.{CoverArt, Tags, TagsService}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scala.collection.SortedSet

/**
  * Created by alex on 24/05/17
  **/
class MultiDiscServiceImplSpec extends Specification with Mockito {

  implicit val messageService = NoOpMessageService
  implicit val flacFileChecker: FlacFileChecker = new FlacFileChecker {
    override def isFlacFile(path: Path): Boolean = true
  }
  implicit val directories: Directories = TestDirectories()
  val rootLocations = Seq(StagedFlacFileLocation("Q"))

  "Joining multi-disc albums" should {

    val (tagsService, multiDiscService) = setup()

    "Create one album with all tracks" in {
      multiDiscService.createSingleAlbum(rootLocations)
      for {
        track <- allTracks
        expectedJoinTags <- track.maybeExpectedJoinTags.toSeq
      } yield {
        there was one(tagsService).write(Paths.get("/staging", track.path), expectedJoinTags)
      }
      1 must be_===(1)
    }
    "Ignore non multi-disc albums" in {
      there were no(tagsService).write(be_===(Paths.get("/staging", BICYCLE_RACE.path)), any[Tags])
    }
  }

  "Splitting multi-disc albums" should {

    val (tagsService, multiDiscService) = setup()

    "Create two albums" in {
      multiDiscService.createAlbumWithExtras(rootLocations)
      for {
        track <- allTracks
        expectedSplitTags <- track.maybeExpectedSplitTags.toSeq
      } yield {
        there was one(tagsService).write(Paths.get("/staging", track.path), expectedSplitTags)
      }
      1 must be_===(1)
    }
    "Ignore non multi-disc albums" in {
      there were no(tagsService).write(be_===(Paths.get("/staging", BICYCLE_RACE.path)), any[Tags])
    }
  }

  def setup(): (TagsService, MultiDiscService) = {
    val tagsService = mock[TagsService]
    val directoryService = mock[DirectoryService]
    directoryService.listFiles(rootLocations) returns allTracks.foldLeft(SortedSet.empty[StagedFlacFileLocation]) { (acc, track) =>
      acc + StagedFlacFileLocation(track.path)
    }
    allTracks.foreach { track =>
      val path = Paths.get("/staging", track.path)
      tagsService.read(path) returns Validated.valid(track.originalTags)
    }

    (tagsService, new MultiDiscServiceImpl(directoryService)(tagsService, flacFileChecker))
  }

  case class Track(
    path: String,
    originalTags: Tags,
    maybeExpectedSplitTags: Option[Tags] = None,
    maybeExpectedJoinTags: Option[Tags] = None
  )

  object Track {
    def apply(path: String, originalTags: Tags, expectedSplitTags: Tags, expectedJoinTags: Tags): Track =
      Track(path, originalTags, Some(expectedSplitTags), Some(expectedJoinTags))
  }

  val ONE_VISION: Track = Track(
    "Q/Queen/AKOM 1/01 One Vision.flac",
    quickTags("AKOM", "A Kind of Magic", "One Vision", trackNumber =  1, totalTracks = 2, discNumber = 1, totalDiscs = 3),
    quickTags("AKOM", "A Kind of Magic", "One Vision", trackNumber =  1, totalTracks = 2, discNumber = 1, totalDiscs = 1),
    quickTags("AKOM", "A Kind of Magic", "One Vision", trackNumber =  1, totalTracks = 5, discNumber = 1, totalDiscs = 1)
  )

  val A_KIND_OF_MAGIC: Track = Track(
    "Q/Queen/AKOM 1/02 A Kind of Magic.flac",
    quickTags("AKOM", "A Kind of Magic", "A Kind of Magic", trackNumber =  2, totalTracks = 2, discNumber = 1, totalDiscs = 3),
    quickTags("AKOM", "A Kind of Magic", "A Kind of Magic", trackNumber =  2, totalTracks = 2, discNumber = 1, totalDiscs = 1),
    quickTags("AKOM", "A Kind of Magic", "A Kind of Magic", trackNumber =  2, totalTracks = 5, discNumber = 1, totalDiscs = 1)
  )

  val FOREVER: Track = Track(
    "Q/Queen/AKOM 2/01 Forever.flac",
    quickTags("AKOM", "A Kind of Magic", "Forever", trackNumber =  1, totalTracks = 2, discNumber = 2, totalDiscs = 3),
    quickTags("AKOM_EXTRAS", "A Kind of Magic (Extras)", "Forever", trackNumber =  1, totalTracks = 3, discNumber = 1, totalDiscs = 1),
    quickTags("AKOM", "A Kind of Magic", "Forever", trackNumber =  3, totalTracks = 5, discNumber = 1, totalDiscs = 1)
  )

  val FRIENDS_WILL_BE_FRIENDS: Track = Track(
    "Q/Queen/AKOM 2/02 Friends Will Be Friends.flac",
    quickTags("AKOM", "A Kind of Magic", "Friends Will Be Friends", trackNumber =  2, totalTracks = 2, discNumber = 2, totalDiscs = 3),
    quickTags("AKOM_EXTRAS", "A Kind of Magic (Extras)", "Friends Will Be Friends", trackNumber = 2, totalTracks = 3, discNumber = 1, totalDiscs = 1),
    quickTags("AKOM", "A Kind of Magic", "Friends Will Be Friends", trackNumber =  4, totalTracks = 5, discNumber = 1, totalDiscs = 1)
  )

  val A_DOZEN_RED_ROSES_FOR_MY_DARLING: Track = Track(
    "Q/Queen/AKOM 3/01 A Dozen Red Roses For My Darling.flac",
    quickTags("AKOM", "A Kind of Magic", "A Dozen Red Roses For My Darling", trackNumber =  1, totalTracks = 1, discNumber = 3, totalDiscs = 3),
    quickTags("AKOM_EXTRAS", "A Kind of Magic (Extras)", "A Dozen Red Roses For My Darling", trackNumber =  3, totalTracks = 3, discNumber = 1, totalDiscs = 1),
    quickTags("AKOM", "A Kind of Magic", "A Dozen Red Roses For My Darling", trackNumber =  5, totalTracks = 5, discNumber = 1, totalDiscs = 1)
  )

  val BICYCLE_RACE: Track = Track(
    "Q/Queen/JAZZ/01 Bicycle Race.flac",
    quickTags("JAZZ", "Jazz", "Bicycle Race", trackNumber =  1, totalTracks = 1, discNumber = 1, totalDiscs = 1)
  )

  val allTracks: Seq[Track] =
    Seq(ONE_VISION, A_KIND_OF_MAGIC, FOREVER, FRIENDS_WILL_BE_FRIENDS, A_DOZEN_RED_ROSES_FOR_MY_DARLING, BICYCLE_RACE)

  def quickTags(albumId: String, album: String, title: String, trackNumber: Int, totalTracks: Int, discNumber: Int, totalDiscs: Int): Tags = {
    Tags(
      albumArtistSort = "ALBUM_ARTIST_SORT",
      albumArtist = "ALBUM_ARTIST",
      album = album,
      artist = "ARTIST",
      artistSort = "ARTIST_SORT",
      title = title,
      totalDiscs = totalDiscs,
      totalTracks = totalTracks,
      discNumber = discNumber,
      albumArtistId = "123",
      albumId = albumId,
      artistId = "123",
      trackId = "123",
      asin = None,
      trackNumber = trackNumber,
      coverArt = CoverArt(Array.emptyByteArray, "image/jpg")
    )
  }
}
