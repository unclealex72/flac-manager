/*
 * Copyright 2014 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package checkout

import java.nio.file.{Path, Paths}

import cats.data.Validated.Valid
import common.changes.{Change, ChangeDao, ChangeMatchers}
import common.configuration.{Directories, TestDirectories, User, Users}
import common.files.FileLocationImplicits._
import common.files._
import common.message.MessageService
import common.music.{CoverArt, Tags, TagsService}
import common.now.NowService
import common.owners.OwnerService
import org.joda.time.DateTime
import org.specs2.matcher.MatchResult
import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.specification.Scope

import scala.collection.{SortedMap, SortedSet}

/**
 * Created by alex on 18/11/14.
 */
class CheckoutServiceImplSpec extends Specification with Mockito with ChangeMatchers {

  trait Context extends Scope {
    val freddie: User = User("freddie")
    val brian: User = User("brian")

    val users = new Users {
      override def allUsers: Set[User] = Set(brian, freddie)
    }

    implicit val stringToPath: String => Path = Paths.get(_)
    implicit val directories: Directories = TestDirectories()
    val oneVisionTags = Tags(
      album = "A Kind of Magic",
      albumArtist = "Queen",
      albumArtistId = "Queen",
      albumArtistSort = "Queen",
      albumId = "A Kind of Magic",
      artist = "Queen",
      artistId = "Queen",
      artistSort = "Queen",
      asin = Some("AKINDOFMAGIC"),
      title = "One Vision",
      trackId = "One Vision",
      coverArt = CoverArt(Array[Byte](), ""),
      discNumber = 1,
      totalDiscs = 1,
      totalTracks = 10,
      trackNumber = 1)

    import Dsl._
    val mustaphaTags = Tags(
      album = "Jazz",
      albumArtist = "Queen",
      albumArtistId = "Queen",
      albumArtistSort = "Queen",
      albumId = "Jazz",
      artist = "Queen",
      artistId = "Queen",
      artistSort = "Queen",
      asin = Some("JAZZ"),
      title = "Mustapha",
      trackId = "Mustapha",
      coverArt = CoverArt(Array[Byte](), ""),
      discNumber = 1,
      totalDiscs = 1,
      totalTracks = 10,
      trackNumber = 1)
    val aKindOfMagicFlac: Dsl.Album[FlacFileLocation] = "A Kind of Magic" tracks(OneVision.flac, AKindOfMagic.flac) using (p => FlacFileLocation(p))
    val jazzFlac: Dsl.Album[FlacFileLocation] = "Jazz" tracks(Mustapha.flac, FatBottomedGirls.flac) using (p => FlacFileLocation(p))
    val aKindOfMagicStagedFlac: Dsl.Album[StagedFlacFileLocation] = "A Kind of Magic" tracks(OneVision.flac, AKindOfMagic.flac) using (p => StagedFlacFileLocation(p))
    val jazzStagedFlac: Dsl.Album[StagedFlacFileLocation] = "Jazz" tracks(Mustapha.flac, FatBottomedGirls.flac) using (p => StagedFlacFileLocation(p))
    val aKindOfMagicMp3: Dsl.Album[EncodedFileLocation] = "A Kind of Magic" tracks(OneVision.mp3, AKindOfMagic.mp3) using (p => EncodedFileLocation(p))
    val jazzMp3: Dsl.Album[EncodedFileLocation] = "Jazz" tracks(Mustapha.mp3, FatBottomedGirls.mp3) using (p => EncodedFileLocation(p))
    val briansAKindOfMagicMp3: Dsl.Album[DeviceFileLocation] = "A Kind of Magic" tracks(OneVision.mp3, AKindOfMagic.mp3) using (p => DeviceFileLocation(brian, p))
    val briansJazzMp3: Dsl.Album[DeviceFileLocation] = "Jazz" tracks(Mustapha.mp3, FatBottomedGirls.mp3) using (p => DeviceFileLocation(brian, p))
    val freddiesJazzMp3: Dsl.Album[DeviceFileLocation] = "Jazz" tracks(Mustapha.mp3, FatBottomedGirls.mp3) using (p => DeviceFileLocation(freddie, p))
    val allAlbums: Seq[Album[FileLocation]] =
      Seq(
        aKindOfMagicFlac.asInstanceOf[Album[FileLocation]],
        jazzFlac.asInstanceOf[Album[FileLocation]],
        aKindOfMagicStagedFlac.asInstanceOf[Album[FileLocation]],
        jazzStagedFlac.asInstanceOf[Album[FileLocation]],
        aKindOfMagicMp3.asInstanceOf[Album[FileLocation]],
        jazzMp3.asInstanceOf[Album[FileLocation]],
        briansAKindOfMagicMp3.asInstanceOf[Album[FileLocation]],
        briansJazzMp3.asInstanceOf[Album[FileLocation]],
        freddiesJazzMp3.asInstanceOf[Album[FileLocation]])
    implicit val fileLocationExtensions = new TestFileLocationExtensions {
      override def isDirectory(fileLocation: FileLocation): Boolean = allAlbums.map(_.album).contains(fileLocation)

      override def exists(fileLocation: FileLocation): Boolean = fileLocation match {
        case StagedFlacFileLocation(_) => false
        case _ => isDirectory(fileLocation) || allAlbums.flatMap(a => a.tracks).contains(fileLocation)
      }

      override def firstFileIn[F <: FileLocation](parentFileLocation: F, extension: Extension, builder: (Path) => F): Option[F] = None
    }

    implicit val tagsService: TagsService = mock[TagsService]
    implicit val messageService: MessageService = mock[MessageService]
    implicit val changeDao: ChangeDao = mock[ChangeDao]
    val fileSystem: FileSystem = mock[FileSystem]
    val ownerService: OwnerService = mock[OwnerService]
    val nowService: NowService = mock[NowService]
    val now = new DateTime()
    tagsService.read("/flac/A Kind of Magic/01 One Vision.flac") returns Valid(oneVisionTags)
    tagsService.read("/flac/Jazz/01 Mustapha.flac") returns Valid(mustaphaTags)
    val checkoutService = new CheckoutServiceImpl(fileSystem, users, ownerService, nowService)

    def checkFilesRemoved: MatchResult[Unit] = {
      // check flac files are moved
      there was one(fileSystem).move(aKindOfMagicFlac(OneVision.flac), aKindOfMagicStagedFlac(OneVision.flac))
      there was one(fileSystem).move(aKindOfMagicFlac(AKindOfMagic.flac), aKindOfMagicStagedFlac(AKindOfMagic.flac))
      there was one(fileSystem).move(jazzFlac(Mustapha.flac), jazzStagedFlac(Mustapha.flac))
      there was one(fileSystem).move(jazzFlac(FatBottomedGirls.flac), jazzStagedFlac(FatBottomedGirls.flac))
      // check that encoded files are deleted
      there was one(fileSystem).remove(aKindOfMagicMp3(OneVision.mp3))
      there was one(fileSystem).remove(aKindOfMagicMp3(AKindOfMagic.mp3))
      there was one(fileSystem).remove(jazzMp3(Mustapha.mp3))
      there was one(fileSystem).remove(jazzMp3(FatBottomedGirls.mp3))
      // check that device files are deleted
      there was one(fileSystem).remove(briansAKindOfMagicMp3(OneVision.mp3))
      there was one(fileSystem).remove(briansAKindOfMagicMp3(AKindOfMagic.mp3))
      there was one(fileSystem).remove(briansJazzMp3(Mustapha.mp3))
      there was one(fileSystem).remove(briansJazzMp3(FatBottomedGirls.mp3))
      there was one(fileSystem).remove(freddiesJazzMp3(Mustapha.mp3))
      there was one(fileSystem).remove(freddiesJazzMp3(FatBottomedGirls.mp3))
      // check that changes are recorded
      there was one(changeDao).store(beTheSameChangeAs(Change.removed(briansAKindOfMagicMp3(OneVision.mp3), now)))
      there was one(changeDao).store(beTheSameChangeAs(Change.removed(briansAKindOfMagicMp3(AKindOfMagic.mp3), now)))
      there was one(changeDao).store(beTheSameChangeAs(Change.removed(briansJazzMp3(Mustapha.mp3), now)))
      there was one(changeDao).store(beTheSameChangeAs(Change.removed(briansJazzMp3(FatBottomedGirls.mp3), now)))
      there was one(changeDao).store(beTheSameChangeAs(Change.removed(freddiesJazzMp3(Mustapha.mp3), now)))
      there was one(changeDao).store(beTheSameChangeAs(Change.removed(freddiesJazzMp3(FatBottomedGirls.mp3), now)))
    }

    def OneVision = Track("01 One Vision")

    def AKindOfMagic = Track("02 A Kind of Magic")

    def Mustapha = Track("01 Mustapha")

    def FatBottomedGirls = Track("02 Fat Bottomed Girls")

    nowService.now() returns now

    case class Track(title: String) {
      def mp3 = s"$title.mp3"

      def flac = s"$title.flac"
    }

    object Dsl {

      implicit def albumsToMap[FL <: FileLocation](albums: Seq[Album[FL]]): SortedMap[FL, SortedSet[FL]] =
        SortedMap.empty[FL, SortedSet[FL]] ++ albums.map(albumToPair)

      implicit def albumToPair[FL <: FileLocation](album: Album[FL]): (FL, SortedSet[FL]) = (album.album, album.tracks)

      implicit def genericAlbum[FL <: FileLocation](album: Album[FL]): Album[FileLocation] = {
        val newTracks = album.tracksByTitle.map { case (k, v) => k -> v }
        Album[FileLocation](album.album, newTracks)
      }

      implicit class ImplicitAlbumBuilder(title: String) {
        def tracks(tracks: String*): AlbumBuilder = {
          AlbumBuilder(title, tracks)
        }
      }

      case class AlbumBuilder(title: String, tracks: Seq[String]) {
        def using[FL <: FileLocation](factory: Path => FL): Album[FL] = {
          val album = factory(Paths.get(title))
          val songs = tracks.foldLeft(Map.empty[String, FL])((fls, track) => fls + (track -> factory(Paths.get(title, track))))
          Album(album, songs)
        }
      }

      case class Album[FL <: FileLocation](album: FL, tracksByTitle: Map[String, FL]) {
        val tracks: SortedSet[FL] = tracksByTitle.values.foldLeft(SortedSet.empty[FL])(_ + _)

        def apply(trackTitle: String): FL = tracksByTitle(trackTitle)
      }
    }

  }

  "Checking out albums" should {
    "remove all traces of the album, including ownership if asked for" in new Context {
      checkoutService.checkout(Seq(aKindOfMagicFlac, jazzFlac), unown = true)
      checkFilesRemoved

      //check that albums were disowned
      there was one(ownerService).unown(brian, Set(oneVisionTags, mustaphaTags))
      there was one(ownerService).unown(freddie, Set(mustaphaTags))
      noMoreCallsTo(fileSystem)
      noMoreCallsTo(ownerService)
      noMoreCallsTo(changeDao)
    }
  }

  "Checking out albums" should {
    "remove all traces of the album, excluding ownership if not asked for" in new Context {
      checkoutService.checkout(Seq(aKindOfMagicFlac, jazzFlac), unown = false)
      checkFilesRemoved

      noMoreCallsTo(fileSystem)
      noMoreCallsTo(ownerService)
      noMoreCallsTo(changeDao)
    }
  }

}
