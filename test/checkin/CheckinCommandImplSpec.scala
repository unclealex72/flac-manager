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

package checkin

import java.nio.file.{CopyOption, Files, Path, StandardCopyOption, FileSystem => JFS}

import cats.data.{NonEmptyList, ValidatedNel}
import cats.data.Validated.Valid
import common.async.{BackgroundExecutionContext, CommandExecutionContext, GlobalExecutionContext, ThreadPoolThrottler}
import common.changes.{Change, ChangeDao, ChangeMatchers}
import common.configuration.User
import common.files.Directory.StagingDirectory
import common.files._
import common.message.Messages.{NOT_OWNED, OVERWRITE}
import common.message.{Message, MessageService}
import common.multi.AllowMultiService
import common.music.Tags
import common.owners.OwnerService
import org.specs2.mock.Mockito
import org.specs2.mutable._
import own.OwnAction
import cats.implicits._

import scala.collection.SortedSet
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * Created by alex on 18/11/14.
 */
class CheckinCommandImplSpec extends Specification with Mockito with ChangeMatchers with TestRepositories[Services] with RepositoryEntry.Dsl {

  sequential

  implicit val commandExecutionContext: CommandExecutionContext = new GlobalExecutionContext with CommandExecutionContext
  implicit val backgroundExecutionContext: BackgroundExecutionContext = new GlobalExecutionContext with BackgroundExecutionContext

  val A_KIND_OF_MAGIC: Album = Album(
    "A Kind of Magic", Tracks("One Vision", "A Kind of Magic", "One Year of Love")
  )

  val INNUENDO: Album = Album(
    "Innuendo", Tracks("Innuendo", "Im Going Slightly Mad", "Headlong")
  )

  val SOUTH_OF_HEAVEN: Album = Album(
    "South of Heaven", Tracks("South of Heaven", "Silent Scream", "Live Undead")
  )

  val entriesBeforeCheckin = Repos(
    staging = Artists("Queen" -> Albums(A_KIND_OF_MAGIC, INNUENDO), "Slayer" -> Albums(SOUTH_OF_HEAVEN))
  )

  val expectedEntriesAfterCheckin = Repos(
    flac = Artists("Queen" -> Seq(A_KIND_OF_MAGIC, INNUENDO), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
    encoded = Artists("Queen" -> Albums(A_KIND_OF_MAGIC, INNUENDO), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
    devices = Users(
      "Freddie" -> Artists("Queen" -> Albums(A_KIND_OF_MAGIC), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
      "Brian" -> Artists("Queen" -> Albums(A_KIND_OF_MAGIC, INNUENDO))
    )
  )

  val expectedEntriesAfterUnownedCheckin = Repos(
    flac = Artists("Queen" -> Seq(A_KIND_OF_MAGIC, INNUENDO), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
    encoded = Artists("Queen" -> Albums(A_KIND_OF_MAGIC, INNUENDO), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
    devices = Users(
      "Freddie" -> Artists("Queen" -> Albums(A_KIND_OF_MAGIC), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
      "Brian" -> Artists("Queen" -> Albums(A_KIND_OF_MAGIC))
    )
  )

  "Checking in albums from scratch" should {
    "encode the albums and create links in the devices repository" in { services: Services =>
      val fs = services.fileSystem
      fs.add(entriesBeforeCheckin :_*)
      services.ownerService.listOwners() returns Future.successful(
        Map(
          "A Kind of Magic" -> Set(User("Brian"), User("Freddie")),
          "Innuendo" -> Set(User("Brian")),
          "South of Heaven" -> Set(User("Freddie"))
        )
      )
      services.repositories.staging.directory(fs.getPath("")).toEither must beRight { stagingDirectory: StagingDirectory =>
        Await.result(services.checkinCommand.checkin(SortedSet(stagingDirectory), allowUnowned = false), 1.hour)
        val entries = fs.entries
        entries must containTheSameElementsAs(fs.expected(expectedEntriesAfterCheckin :_*))
      }
    }
  }

  "Checking in albums from scratch" should {
    "allow unowned albums if so desired" in { services: Services =>
      val fs = services.fileSystem
      fs.add(entriesBeforeCheckin :_*)
      services.ownerService.listOwners() returns Future.successful(
        Map(
          "A Kind of Magic" -> Set(User("Brian"), User("Freddie")),
          "South of Heaven" -> Set(User("Freddie"))
        )
      )
      services.repositories.staging.directory(fs.getPath("")).toEither must beRight { stagingDirectory: StagingDirectory =>
        Await.result(services.checkinCommand.checkin(SortedSet(stagingDirectory), allowUnowned = true), 1.hour)
        val entries = fs.entries
        entries must containTheSameElementsAs(fs.expected(expectedEntriesAfterUnownedCheckin :_*))
      }
    }
  }

  "Checking in albums from scratch" should {
    "not allow unowned albums if so desired" in { services: Services =>
      val fs = services.fileSystem
      fs.add(entriesBeforeCheckin :_*)
      services.ownerService.listOwners() returns Future.successful(
        Map(
          "A Kind of Magic" -> Set(User("Brian"), User("Freddie")),
          "South of Heaven" -> Set(User("Freddie"))
        )
      )
      services.repositories.staging.directory(fs.getPath("")).toEither must beRight { stagingDirectory: StagingDirectory =>
        Await.result(services.checkinCommand.checkin(SortedSet(stagingDirectory), allowUnowned = false), 1.hour).toEither must beLeft { messages: NonEmptyList[Message] =>
          val entries = fs.entries
          val empty: ValidatedNel[Message, Seq[Message]] = Valid(Seq.empty[Message])
          val vExpectedNotOwneds = Seq("01 Innuendo.flac", "02 Im Going Slightly Mad.flac", "03 Headlong.flac").foldLeft(empty) { (acc, track) =>
            val path = fs.getPath("Q", "Queen", "Innuendo", track)
            val vStagingFile = services.repositories.staging.file(path)
            (acc |@| vStagingFile).map { (messages, stagingFile) =>
              messages :+ NOT_OWNED(stagingFile)
            }
          }
          vExpectedNotOwneds.toEither must beRight { expectedNotOwneds: Seq[Message] =>
            messages.toList must containTheSameElementsAs(expectedNotOwneds)
          }

          entries must containTheSameElementsAs(fs.expected(entriesBeforeCheckin :_*))
        }
      }
    }
  }

  override def generate(fs: JFS, repositories: Repositories): Services = {
    val ownerService = mock[OwnerService]
    val changeDao = mock[ChangeDao]
    changeDao.store(any[Change])(any[MessageService]) returns Future.successful({})
    ownerService.changeFlacOwnership(any[User], any[OwnAction], any[NonEmptyList[FlacFile]])(any[MessageService]) returns Future.successful(Valid({}))
    ownerService.changeStagingOwnership(any[User], any[OwnAction], any[NonEmptyList[StagingFile]])(any[MessageService]) returns Future.successful(Valid({}))
    ownerService.unown(any[User], any[Set[Tags]])(any[MessageService]) returns Future.successful(Valid({}))
    val fileSystem = new ProtectionAwareFileSystem(new FileSystemImpl)
    val allowMultiService = new AllowMultiService {
      override def allowMulti: Boolean = false
    }
    val checkinActionGenerator = new CheckinActionGeneratorImpl(ownerService, allowMultiService)
    val throttler = new ThreadPoolThrottler(2)
    val mp3Encoder = new Mp3Encoder {
      override def encode(source: Path, target: Path): Unit = {
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
      }
    }
    val singleCheckinService = new SingleCheckinServiceImpl(throttler, fileSystem, changeDao, mp3Encoder, repositories)
    val checkinService = new CheckinServiceImpl(singleCheckinService)
    val checkinCommand = new CheckinCommandImpl(checkinActionGenerator, checkinService)
    Services(fs, repositories, ownerService, changeDao, checkinCommand)
  }

}

case class Services(
                     fileSystem: JFS,
                     repositories: Repositories,
                     ownerService: OwnerService,
                     changeDao: ChangeDao,
                     checkinCommand: CheckinCommand)
