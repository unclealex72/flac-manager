package checkin

import java.nio.file.Paths

import common.configuration.{TestDirectories, User}
import common.files.FileLocationImplicits._
import common.files.FileLocationToPathImplicits._
import common.files._
import common.message.MessageTypes._
import common.message.TestMessageService
import common.music.{CoverArt, Tags, TagsService}
import common.owners.OwnerService
import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.specification.Scope

import scala.collection.SortedSet

/**
 * Created by alex on 15/11/14.
 */
class CheckinCommandImplSpec extends Specification with Mockito {

  trait Context extends Scope {
    implicit val directories = TestDirectories(Paths.get("/flac"), Paths.get(""), Paths.get(""), Paths.get("/staging"), Paths.get("/temp"))
    val tags = Tags(
      album = "Metal: A Headbanger's Companion",
      albumArtist = "Various Artists",
      albumArtistId = "89ad4ac3-39f7-470e-963a-56509c546377",
      albumArtistSort = "Various Artists Sort",
      albumId = "6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e",
      artist = "Napalm Death",
      artistId = "ce7bba8b-026b-4aa6-bddb-f98ed6d595e4",
      artistSort = "Napalm Death Sort",
      asin = Some("B000Q66HUA"),
      title = "Suffer The Children",
      trackId = "5b0ef8e9-9b55-4a3e-aca6-d816d6bbc00f",
      coverArt = CoverArt(Array[Byte](), ""),
      discNumber = 1,
      totalDiscs = 6,
      totalTracks = 17,
      trackNumber = 3)
    val brian = User("Brian", "", "", "")
    val fileLocations = Seq.empty[StagedFlacFileLocation]
    lazy val directoryService = mock[DirectoryService]
    lazy implicit val flacFileChecker = mock[FlacFileChecker]
    lazy val ownerService = mock[OwnerService]
    lazy implicit val tagsService = mock[TagsService]
    lazy implicit val messageService = mock[TestMessageService]
    lazy implicit val fileLocationExtensions = mock[TestFileLocationExtensions]
    lazy val checkinService = mock[CheckinService]
    lazy val checkinCommand = new CheckinCommandImpl(directoryService, ownerService, checkinService)

    def andThatsAll() = {
      noMoreCallsTo(messageService, checkinService)
    }
  }

  "Validating checked in files" should {
    "not allow flac files that are not fully tagged" in new Context {
      val sfl = StagedFlacFileLocation("bad.flac")
      flacFileChecker.isFlacFile(sfl) returns true
      tagsService.read(sfl) returns Left(Set())
      directoryService.listFiles(fileLocations) returns SortedSet(sfl)
      checkinCommand.checkin(fileLocations)
      there was one(messageService).printMessage(INVALID_FLAC(sfl))
      andThatsAll()
    }

    "not allow flac files that would overwrite an existing file" in new Context {
      val sfl = StagedFlacFileLocation("good.flac")
      val fl = sfl.toFlacFileLocation(tags)
      flacFileChecker.isFlacFile(sfl) returns true
      tagsService.read(sfl) returns Right(tags)
      directoryService.listFiles(fileLocations) returns SortedSet(sfl)
      fileLocationExtensions.exists(fl) returns true
      checkinCommand.checkin(fileLocations)
      there was one(messageService).printMessage(OVERWRITE(sfl, fl))
      andThatsAll()
    }

    "not allow two flac files that would have the same file name" in new Context {
      val sfl1 = StagedFlacFileLocation("good.flac")
      val sfl2 = StagedFlacFileLocation("bad.flac")
      val sfl3 = StagedFlacFileLocation("ugly.flac")
      val fl = sfl1.toFlacFileLocation(tags)
      Seq(sfl1, sfl2, sfl3).foreach { sfl =>
        flacFileChecker.isFlacFile(sfl) returns true
        tagsService.read(sfl) returns Right(tags)
      }
      directoryService.listFiles(fileLocations) returns SortedSet(sfl1, sfl2, sfl3)
      fileLocationExtensions.exists(fl) returns false
      checkinCommand.checkin(fileLocations)
      there was one(messageService).printMessage(NON_UNIQUE(fl, Set(sfl1, sfl2, sfl3)))
      andThatsAll()
    }
  }

  "not allow flac files that don't have an owner" in new Context {
    val sfl = StagedFlacFileLocation("good.flac")
    val fl = sfl.toFlacFileLocation(tags)
    flacFileChecker.isFlacFile(sfl) returns true
    tagsService.read(sfl) returns Right(tags)
    directoryService.listFiles(fileLocations) returns SortedSet(sfl)
    fileLocationExtensions.exists(fl) returns false
    ownerService.listCollections() returns { fl => Set()}
    checkinCommand.checkin(fileLocations)
    there was one(messageService).printMessage(NOT_OWNED(sfl))
    andThatsAll()
  }

  "Allow valid flac files an non-flac files" in new Context {
    val sfl1 = StagedFlacFileLocation("good.flac")
    val sfl2 = StagedFlacFileLocation("bad.flac")
    val fl = sfl1.toFlacFileLocation(tags)
    flacFileChecker.isFlacFile(sfl1) returns true
    flacFileChecker.isFlacFile(sfl2) returns false
    tagsService.read(sfl1) returns Right(tags)
    directoryService.listFiles(fileLocations) returns SortedSet(sfl1, sfl2)
    fileLocationExtensions.exists(fl) returns false
    ownerService.listCollections() returns { fl => Set(brian)}
    checkinCommand.checkin(fileLocations)
    there was one(checkinService).checkin(Encode(sfl1, fl, tags, Set(brian)))
    there was one(checkinService).checkin(Delete(sfl2))
    andThatsAll()
  }

}
