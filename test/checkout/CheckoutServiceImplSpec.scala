package checkout

import java.nio.file.{Path, Paths}

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
    val freddie: User = User("freddie", "", "", "")
    val brian: User = User("brian", "", "", "")

    val users = new Users {
      override def allUsers: Set[User] = Set(brian, freddie)
    }

    implicit val stringToPath: String => Path = Paths.get(_)
    implicit val directories: Directories = TestDirectories("/flac", "/devices", "/encoded", "/staging", "/temp")

    object Dsl {

      implicit class ImplicitAlbumBuilder(title: String) {
        def tracks(tracks: String*) = {
          new AlbumBuilder(title, tracks)
        }
      }

      case class AlbumBuilder(val title: String, val tracks: Seq[String]) {
        def using[FL <: FileLocation](factory: Path => FL): Album[FL] = {
          val album = factory(Paths.get(title))
          val songs = tracks.foldLeft(Map.empty[String, FL])((fls, track) => fls + (track -> factory(Paths.get(title, track))))
          Album(album, songs)
        }
      }

      case class Album[FL <: FileLocation](album: FL, tracksByTitle: Map[String, FL]) {
        val tracks = tracksByTitle.values.foldLeft(SortedSet.empty[FL])(_ + _)

        def apply(trackTitle: String): FL = tracksByTitle.get(trackTitle).get
      }

      implicit def albumToPair[FL <: FileLocation](album: Album[FL]) = (album.album, album.tracks)

      implicit def albumsToMap[FL <: FileLocation](albums: Seq[Album[FL]]) =
        SortedMap.empty[FL, SortedSet[FL]] ++ albums.map(albumToPair(_))

      implicit def genericAlbum[FL <: FileLocation](album: Album[FL]) = {
        val newTracks = album.tracksByTitle.map { case (k, v) => k -> v}.toMap
        Album[FileLocation](album.album, newTracks)
      }
    }

    import Dsl._

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

    case class Track(title: String) {
      def mp3 = s"$title.mp3"

      def flac = s"$title.flac"
    }

    def OneVision = Track("01 One Vision")

    def AKindOfMagic = Track("02 A Kind of Magic")

    def Mustapha = Track("01 Mustapha")

    def FatBottomedGirls = Track("02 Fat Bottomed Girls")

    val aKindOfMagicFlac = "A Kind of Magic" tracks(OneVision.flac, AKindOfMagic.flac) using (p => FlacFileLocation(p))
    val jazzFlac = "Jazz" tracks(Mustapha.flac, FatBottomedGirls.flac) using (p => FlacFileLocation(p))

    val aKindOfMagicStagedFlac = "A Kind of Magic" tracks(OneVision.flac, AKindOfMagic.flac) using (p => StagedFlacFileLocation(p))
    val jazzStagedFlac = "Jazz" tracks(Mustapha.flac, FatBottomedGirls.flac) using (p => StagedFlacFileLocation(p))

    val aKindOfMagicMp3 = "A Kind of Magic" tracks(OneVision.mp3, AKindOfMagic.mp3) using (p => EncodedFileLocation(p))
    val jazzMp3 = "Jazz" tracks(Mustapha.mp3, FatBottomedGirls.mp3) using (p => EncodedFileLocation(p))

    val briansAKindOfMagicMp3 = "A Kind of Magic" tracks(OneVision.mp3, AKindOfMagic.mp3) using (p => DeviceFileLocation(brian, p))
    val briansJazzMp3 = "Jazz" tracks(Mustapha.mp3, FatBottomedGirls.mp3) using (p => DeviceFileLocation(brian, p))
    val freddiesJazzMp3 = "Jazz" tracks(Mustapha.mp3, FatBottomedGirls.mp3) using (p => DeviceFileLocation(freddie, p))

    val allAlbums: Seq[Album[FileLocation]] =
      Seq(
        aKindOfMagicFlac, jazzFlac, aKindOfMagicStagedFlac, jazzStagedFlac,
        aKindOfMagicMp3, jazzMp3, briansAKindOfMagicMp3, briansJazzMp3, freddiesJazzMp3)

    implicit val fileLocationExtensions = new TestFileLocationExtensions {
      override def isDirectory(fileLocation: FileLocation): Boolean = allAlbums.map(_.album).contains(fileLocation)

      override def exists(fileLocation: FileLocation): Boolean = fileLocation match {
        case StagedFlacFileLocation(_) => false
        case _ => isDirectory(fileLocation) || allAlbums.map(_.tracks).flatten.contains(fileLocation)
      }
    }

    implicit val tagsService = mock[TagsService]
    tagsService.read("/flac/A Kind of Magic/01 One Vision.flac") returns Right(oneVisionTags)
    tagsService.read("/flac/Jazz/01 Mustapha.flac") returns Right(mustaphaTags)

    implicit val messageService = mock[MessageService]
    implicit val changeDao = mock[ChangeDao]
    val fileSystem = mock[FileSystem]
    val ownerService = mock[OwnerService]
    val nowService = mock[NowService]
    val now = new DateTime()
    nowService.now() returns now
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

  }

  "Checking out albums" should {
    "remove all traces of the album, including ownership if asked for" in new Context {
      checkoutService.checkout(Seq(aKindOfMagicFlac, jazzFlac), true)
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
      checkoutService.checkout(Seq(aKindOfMagicFlac, jazzFlac), false)
      checkFilesRemoved

      noMoreCallsTo(fileSystem)
      noMoreCallsTo(ownerService)
      noMoreCallsTo(changeDao)
    }
  }

}