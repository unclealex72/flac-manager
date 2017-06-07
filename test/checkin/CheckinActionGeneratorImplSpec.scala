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

package checkin

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import common.async.{BackgroundExecutionContext, CommandExecutionContext, GlobalExecutionContext}
import common.configuration.{TestDirectories, User}
import common.files.FileLocationToPathImplicits._
import common.files._
import common.message.Messages._
import common.message.{Message, TestMessageService}
import common.multi.AllowMultiService
import common.music.{CoverArt, Tags, TagsService}
import common.owners.OwnerService
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.specification.Scope

import scala.concurrent.Future

/**
 * Created by alex on 15/11/14.
 */
class CheckinActionGeneratorImplSpec(implicit ee: ExecutionEnv) extends Specification with Mockito {

  trait Context extends Scope {
    val _allowMulti: Boolean
    val commandExecutionContext: CommandExecutionContext = new GlobalExecutionContext with CommandExecutionContext
    val backgroundExecutionContext: BackgroundExecutionContext = new GlobalExecutionContext with BackgroundExecutionContext

    lazy implicit val flacFileChecker: FlacFileChecker = mock[FlacFileChecker]
    lazy val ownerService: OwnerService = mock[OwnerService]
    lazy implicit val tagsService: TagsService = mock[TagsService]
    lazy implicit val messageService = TestMessageService()
    lazy implicit val fileLocationExtensions: TestFileLocationExtensions = mock[TestFileLocationExtensions]
    lazy val checkinService: CheckinService = mock[CheckinService]
    lazy val actionGenerator = new CheckinActionGeneratorImpl(ownerService, new AllowMultiService {
      override def allowMulti: Boolean = _allowMulti
    })(flacFileChecker, commandExecutionContext, tagsService, fileLocationExtensions)
    implicit val directories = TestDirectories()
    val albumId = "6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e"

    val tags = Tags(
      album = "Metal: A Headbanger's Companion",
      albumArtist = "Various Artists",
      albumArtistId = "89ad4ac3-39f7-470e-963a-56509c546377",
      albumArtistSort = "Various Artists Sort",
      albumId = albumId,
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
    val discTwoTags = Tags(
      album = "Metal: A Headbanger's Companion",
      albumArtist = "Various Artists",
      albumArtistId = "89ad4ac3-39f7-470e-963a-56509c546377",
      albumArtistSort = "Various Artists Sort",
      albumId = albumId,
      artist = "Napalm Death",
      artistId = "ce7bba8b-026b-4aa6-bddb-f98ed6d595e4",
      artistSort = "Napalm Death Sort",
      asin = Some("B000Q66HUA"),
      title = "Suffer The Children",
      trackId = "5b0ef8e9-9b55-4a3e-aca6-d816d6bbc00f",
      coverArt = CoverArt(Array[Byte](), ""),
      discNumber = 2,
      totalDiscs = 6,
      totalTracks = 17,
      trackNumber = 3)
    val brian: User = User("Brian")
    val fileLocations = Seq.empty[StagedFlacFileLocation]
  }

  "Validating checked in files" should {
    "not allow flac files that are not fully tagged" in new Context {
      val _allowMulti = false
      val sfl = StagedFlacFileLocation("bad.flac")
      flacFileChecker.isFlacFile(sfl) returns true
      tagsService.read(sfl) returns Invalid(NonEmptyList.of(""))
      ownerService.listOwners().returns(Future.successful(Map(albumId -> Set(brian))))
      actionGenerator.generate(Seq(sfl), allowUnowned = false).map(_.toEither) must beLeft(
        NonEmptyList.of[Message](INVALID_FLAC(sfl))).await
    }

    "not allow flac files that would overwrite an existing file" in new Context {
      val _allowMulti = false
      val sfl = StagedFlacFileLocation("good.flac")
      val fl: FlacFileLocation = sfl.toFlacFileLocation(tags)
      ownerService.listOwners().returns(Future.successful(Map(albumId -> Set(brian))))
      flacFileChecker.isFlacFile(sfl) returns true
      tagsService.read(sfl) returns Valid(tags)
      fileLocationExtensions.exists(fl) returns true
      actionGenerator.generate(Seq(sfl), allowUnowned = false).map(_.toEither) must beLeft(
        NonEmptyList.of[Message](OVERWRITE(sfl, fl))).await
    }

    "not allow two flac files that would have the same file name" in new Context {
      val _allowMulti = false
      val sfl1 = StagedFlacFileLocation("good.flac")
      val sfl2 = StagedFlacFileLocation("bad.flac")
      val sfl3 = StagedFlacFileLocation("ugly.flac")
      val fl: FlacFileLocation = sfl1.toFlacFileLocation(tags)
      Seq(sfl1, sfl2, sfl3).foreach { sfl =>
        flacFileChecker.isFlacFile(sfl) returns true
        tagsService.read(sfl) returns Valid(tags)
      }
      ownerService.listOwners().returns(Future.successful(Map(albumId -> Set(brian))))
      fileLocationExtensions.exists(fl) returns false
      actionGenerator.generate(Seq(sfl1, sfl2, sfl3), allowUnowned = false).map(_.toEither) must beLeft(
        NonEmptyList.of[Message](NON_UNIQUE(fl, Set(sfl1, sfl2, sfl3)))).await
    }
  }

  "not allow flac files that don't have an owner without the allow unowned flag" in new Context {
    val _allowMulti = false
    val sfl = StagedFlacFileLocation("good.flac")
    val fl: FlacFileLocation = sfl.toFlacFileLocation(tags)
    flacFileChecker.isFlacFile(sfl) returns true
    tagsService.read(sfl) returns Valid(tags)
    fileLocationExtensions.exists(fl) returns false
    ownerService.listOwners().returns(Future.successful(Map.empty))
    actionGenerator.generate(Seq(sfl), allowUnowned = false).map(_.toEither) must beLeft(
      NonEmptyList.of[Message](NOT_OWNED(sfl))).await
    actionGenerator.generate(Seq(sfl), allowUnowned = true).map(_.toEither) must beRight(
      Seq[Action](Encode(sfl, fl, tags, Set.empty))).await
  }

  "Allow valid flac files an non-flac files" in new Context {
    val _allowMulti = false
    val sfl1 = StagedFlacFileLocation("good.flac")
    val sfl2 = StagedFlacFileLocation("bad.flac")
    val fl: FlacFileLocation = sfl1.toFlacFileLocation(tags)
    flacFileChecker.isFlacFile(sfl1) returns true
    flacFileChecker.isFlacFile(sfl2) returns false
    tagsService.read(sfl1) returns Valid(tags)
    fileLocationExtensions.exists(fl) returns false
    ownerService.listOwners().returns(Future.successful(Map(albumId -> Set(brian))))
    actionGenerator.generate(Seq(sfl1, sfl2), allowUnowned = false).map(_.toEither) must beRight(
      Seq[Action](Encode(sfl1, fl, tags, Set(brian)), Delete(sfl2))).await
  }

  "Allow multi disc flac files if so configured" in new Context {
    val _allowMulti = true
    val sfl1 = StagedFlacFileLocation("good.flac")
    val fl: FlacFileLocation = sfl1.toFlacFileLocation(discTwoTags)
    flacFileChecker.isFlacFile(sfl1) returns true
    tagsService.read(sfl1) returns Valid(discTwoTags)
    fileLocationExtensions.exists(fl) returns false
    ownerService.listOwners().returns(Future.successful(Map(albumId -> Set(brian))))
    actionGenerator.generate(Seq(sfl1), allowUnowned = false).map(_.toEither) must beRight(
      Seq[Action](Encode(sfl1, fl, discTwoTags, Set(brian)))).await
  }

  "Not allow multi disc flac files if so configured" in new Context {
    val _allowMulti = false
    val sfl1 = StagedFlacFileLocation("bad.flac")
    val fl: FlacFileLocation = sfl1.toFlacFileLocation(tags)
    flacFileChecker.isFlacFile(sfl1) returns true
    tagsService.read(sfl1) returns Valid(discTwoTags)
    fileLocationExtensions.exists(fl) returns false
    ownerService.listOwners().returns(Future.successful(Map(albumId -> Set(brian))))
    actionGenerator.generate(Seq(sfl1), allowUnowned = false).map(_.toEither) must beLeft(
      NonEmptyList.of[Message](MULTI_DISC(sfl1))).await
  }
}
