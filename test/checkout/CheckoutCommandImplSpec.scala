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

import java.nio.file.{Path, FileSystem => JFS}
import java.time.Clock

import cats.data.{NonEmptyList, ValidatedNel}
import cats.data.Validated.Valid
import common.async.{BackgroundExecutionContext, CommandExecutionContext, GlobalExecutionContext}
import common.changes.{Change, ChangeDao, ChangeMatchers}
import common.configuration.{User, UserDao}
import common.files.Directory.FlacDirectory
import common.files._
import common.message.{Message, MessageService}
import common.music.Tags
import common.owners.OwnerService
import org.specs2.matcher.Matcher
import org.specs2.mock.Mockito
import org.specs2.mutable._
import own.OwnAction

import scala.collection.SortedSet
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import cats.implicits._
import checkin.LossyEncoder
import common.files.Extension.{M4A, MP3}
import common.message.Messages.OVERWRITE

/**
 * Created by alex on 18/11/14.
 */
class CheckoutCommandImplSpec extends Specification with Mockito with ChangeMatchers with TestRepositories[Services] with RepositoryEntry.Dsl {

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

  val entriesBeforeCheckout = Repos(
    flac = Artists("Queen" -> Albums(A_KIND_OF_MAGIC, INNUENDO), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
    encoded = Artists("Queen" -> Albums(A_KIND_OF_MAGIC, INNUENDO), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
    devices = Users(
      "Freddie" -> Artists("Queen" -> Albums(A_KIND_OF_MAGIC), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
      "Brian" -> Artists("Queen" -> Albums(A_KIND_OF_MAGIC, INNUENDO))
    )
  )

  val expectedEntriesAfterCheckout = Repos(
    flac = Artists("Queen" -> Seq(A_KIND_OF_MAGIC), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
    staging = Artists("Queen" -> Albums(INNUENDO)),
    encoded = Artists("Queen" -> Albums(A_KIND_OF_MAGIC), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
    devices = Users(
      "Freddie" -> Artists("Queen" -> Albums(A_KIND_OF_MAGIC), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
      "Brian" -> Artists("Queen" -> Albums(A_KIND_OF_MAGIC))
    )
  )

  val conflictingEntries = Repos(
    flac = Artists("Queen" -> Albums(A_KIND_OF_MAGIC, INNUENDO), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
    staging = Artists("Queen" -> Albums(INNUENDO)),
    encoded = Artists("Queen" -> Albums(A_KIND_OF_MAGIC, INNUENDO), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
    devices = Users(
      "Freddie" -> Artists("Queen" -> Albums(A_KIND_OF_MAGIC), "Slayer" -> Albums(SOUTH_OF_HEAVEN)),
      "Brian" -> Artists("Queen" -> Albums(A_KIND_OF_MAGIC, INNUENDO))
    )
  )
  "Checking out but not unowning an album" should {
    "remove the album but keep it owned" in { services: Services =>
      val fs = services.fileSystem
      fs.add(entriesBeforeCheckout :_*)
      services.repositories.flac.directory(fs.getPath("Q/Queen/Innuendo")).toEither must beRight { flacDirectory: FlacDirectory =>
        Await.result(services.checkoutCommand.checkout(SortedSet(flacDirectory), unown = false), 1.hour)
        val entries = fs.entries
        entries must containTheSameElementsAs(fs.expected(expectedEntriesAfterCheckout :_*))
        there were noCallsTo(services.ownerService)
      }
    }
  }

  def hasAlbumId(albumId: String): Matcher[Tags] = be_===(albumId) ^^ { (t: Tags) => t.albumId }
  def hasOneElementThat[E](matcher: Matcher[E]): Matcher[Set[E]] = (be_===(1) ^^ ((es: Set[E]) => es.size)) and contain(matcher)

  "Checking out and unowning an album" should {
    "remove the album and unown in" in { services: Services =>
      val fs = services.fileSystem
      fs.add(entriesBeforeCheckout :_*)
      services.repositories.flac.directory(fs.getPath("Q/Queen/Innuendo")).toEither must beRight { flacDirectory: FlacDirectory =>
        Await.result(services.checkoutCommand.checkout(SortedSet(flacDirectory), unown = true), 1.hour)
        val entries = fs.entries
        entries must containTheSameElementsAs(fs.expected(expectedEntriesAfterCheckout :_*))
        there was one(services.ownerService).unown(
          be_===(User("Brian")),
          argThat(hasOneElementThat(hasAlbumId("Innuendo"))))(any[MessageService])
        there were noCallsTo(services.ownerService)
      }
    }
  }

  "Trying to checkout files that would overwrite files in the staging directory" should {
    "fail and report which files would be overwritten" in { services: Services =>
      val fs = services.fileSystem
      fs.add(conflictingEntries :_*)
      services.repositories.flac.directory(fs.getPath("Q/Queen/Innuendo")).toEither must beRight { flacDirectory: FlacDirectory =>
        Await.result(services.checkoutCommand.checkout(SortedSet(flacDirectory), unown = true), 1.hour).toEither must beLeft { messages: NonEmptyList[Message] =>
          val empty: ValidatedNel[Message, Seq[Message]] = Valid(Seq.empty[Message])
          val vExpectedOverwrites = Seq("01 Innuendo.flac", "02 Im Going Slightly Mad.flac", "03 Headlong.flac").foldLeft(empty) { (acc, track) =>
            val path = fs.getPath("Q", "Queen", "Innuendo", track)
            val vFlacFile = services.repositories.flac.file(path)
            val vStagingFile = services.repositories.staging.file(path)
            (acc |@| vFlacFile |@| vStagingFile).map { (messages, flacFile, stagingFile) =>
              messages :+ OVERWRITE(flacFile, stagingFile)
            }
          }
          vExpectedOverwrites.toEither must beRight { expectedOverwrites: Seq[Message] =>
            messages.toList must containTheSameElementsAs(expectedOverwrites)
          }
          fs.entries must containTheSameElementsAs(fs.expected(conflictingEntries :_*))
        }
      }

    }
  }

  override def generate(fs: JFS, repositories: Repositories): Services = {
    val userDao: UserDao = () => Set(User("Brian"), User("Freddie"))
    val ownerService = mock[OwnerService]
    val changeDao = mock[ChangeDao]
    changeDao.store(any[Change])(any[MessageService]) returns Future.successful({})
    ownerService.changeFlacOwnership(any[User], any[OwnAction], any[NonEmptyList[FlacFile]])(any[MessageService]) returns Future.successful(Valid({}))
    ownerService.changeStagingOwnership(any[User], any[OwnAction], any[NonEmptyList[StagingFile]])(any[MessageService]) returns Future.successful(Valid({}))
    ownerService.unown(any[User], any[Set[Tags]])(any[MessageService]) returns Future.successful(Valid({}))
    class NoOpLossyEncoder(extension: Extension) extends LossyEncoder {
      override def encode(source: Path, target: Path): Unit = {}
      override def encodesTo: Extension = extension
    }
    val lossyEncoders = Seq(new NoOpLossyEncoder(M4A), new NoOpLossyEncoder(MP3))
    val fileSystem = new ProtectionAwareFileSystem(new FileSystemImpl)
    val checkoutService = new CheckoutServiceImpl(fileSystem, userDao, ownerService, changeDao, lossyEncoders, Clock.systemDefaultZone())
    val checkoutCommand = new CheckoutCommandImpl(repositories, checkoutService)
    Services(fs, repositories, ownerService, changeDao, checkoutCommand)
  }

}

case class Services(
                     fileSystem: JFS,
                     repositories: Repositories,
                     ownerService: OwnerService,
                     changeDao: ChangeDao,
                     checkoutCommand: CheckoutCommand)
