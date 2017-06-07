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

import java.util.concurrent.TimeUnit
import javax.inject.Inject

import com.typesafe.scalalogging.StrictLogging
import common.async.CommandExecutionContext
import common.changes.{Change, ChangeDao}
import common.configuration.{Directories, User}
import common.files.Extension.MP3
import common.files._
import common.message.Messages.{ENCODE, EXCEPTION}
import common.message.{MessageService, Messaging}
import common.music.{Tags, TagsService}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * The default implementation of [[SingleCheckinService]]
  * @param throttler The throttler used to make sure file system actions are atomic.
  * @param fileSystem The underlying file system.
  * @param changeDao The DAO used to store changes.
  * @param fileLocationExtensions Give [[FileLocation]]s path-like functionality.
  * @param directories The locations of the different repositories.
  * @param mp3Encoder The encoder used to generate MP3 files.
  * @param tagsService The service used to write tags to audio files.
  * @param ec The execution context used to move between actions.
  */
class SingleCheckinServiceImpl @Inject() (val throttler: Throttler,
                                          val fileSystem: FileSystem)
                                         (implicit val changeDao: ChangeDao,
                                          val fileLocationExtensions: FileLocationExtensions,
                                          val directories: Directories,
                                          val mp3Encoder: Mp3Encoder,
                                          val tagsService: TagsService,
                                          val commandExecutionContext: CommandExecutionContext) extends SingleCheckinService
  with ThrottlerOps with StrictLogging with Messaging {

  override def encode(stagedFileLocation: StagedFlacFileLocation,
                      flacFileLocation: FlacFileLocation,
                      tags: Tags,
                      owners: Set[User])
                     (implicit messagingService: MessageService): Future[_] = safely {
    for {
      temporaryFileLocation <- encodeFile(stagedFileLocation, flacFileLocation, tags)
      _ <- moveAndLink(
        temporaryFileLocation,
        stagedFileLocation,
        flacFileLocation,
        tags,
        owners)
    } yield {}
  }

  /**
    * Encode a file to a temporary location.
    * @param stagedFlacFileLocation The file to encode.
    * @param flacFileLocation The location when the flac file will end up.
    * @param tags The audio tags of the flac file.
    * @param messageService The service used to report progress and errors.
    * @return The location of the encoded mp3 file.
    */
  def encodeFile(stagedFlacFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags)
                (implicit messageService: MessageService): Future[TemporaryFileLocation] = parallel {
    val tempEncodedLocation = TemporaryFileLocation.create(MP3)
    val encodedFileLocation = flacFileLocation.toEncodedFileLocation
    log(ENCODE(stagedFlacFileLocation, encodedFileLocation))
    stagedFlacFileLocation.encodeTo(tempEncodedLocation)
    tempEncodedLocation.writeTags(tags)
    tempEncodedLocation
  }

  /**
    * Move the staged flac file to the flac repository and the encoded mp3 file to the encoded repository as well
    * as linking to it from within the devices repository.
    * @param temporaryFileLocation The location of the mp3 file.
    * @param stagedFileLocation The source flac file.
    * @param flacFileLocation The target location for the flac file.
    * @param tags The tags of the flac file.
    * @param owners The owners of the flac file.
    * @param messageService The service used to report progress and errors.
    * @return Eventually nothing.
    */
  def moveAndLink(
                   temporaryFileLocation: TemporaryFileLocation,
                   stagedFileLocation: StagedFlacFileLocation,
                   flacFileLocation: FlacFileLocation,
                   tags: Tags,
                   owners: Set[User])(implicit messageService: MessageService): Future[_] = sequential {
    val encodedFileLocation = flacFileLocation.toEncodedFileLocation
    fileSystem.move(temporaryFileLocation, encodedFileLocation)
    owners.foreach { user =>
      val deviceFileLocation = encodedFileLocation.toDeviceFileLocation(user)
      fileSystem.link(encodedFileLocation, deviceFileLocation)
      Await.result(changeDao.store(Change.added(deviceFileLocation)), Duration.apply(1, TimeUnit.HOURS))
    }
    fileSystem.move(stagedFileLocation, flacFileLocation)
  }

  override def remove(stagedFlacFileLocation: StagedFlacFileLocation)
                     (implicit messagingService: MessageService): Future[_] = safely {
    sequential {
      fileSystem.remove(stagedFlacFileLocation)
    }
  }

  private def safely(block: => Future[_])(implicit messageService: MessageService): Future[_] = {
    block.recover {
      case e: Exception => log(EXCEPTION(e))
    }
  }
}
