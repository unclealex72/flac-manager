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

import java.time.Clock
import java.util.concurrent.TimeUnit
import javax.inject.Inject

import com.typesafe.scalalogging.StrictLogging
import common.async.CommandExecutionContext
import common.changes.{Change, ChangeDao}
import common.configuration.User
import common.files._
import common.message.Messages.{ENCODE, ENCODE_DURATION}
import common.message.{MessageService, Messaging}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * The default implementation of [[SingleCheckinService]]
  * @param throttler The throttler used to make sure file system actions are atomic.
  * @param fileSystem The underlying file system.
  * @param changeDao The DAO used to store changes.
  * @param fileLocationExtensions Give [[FileLocation]]s path-like functionality.
  * @param directories The locations of the different repositories.
  * @param m4aEncoder The encoder used to generate MP3 files.
  * @param tagsService The service used to write tags to audio files.
  * @param ec The execution context used to move between actions.
  */
class SingleCheckinServiceImpl @Inject() (val throttler: Throttler,
                                          val fileSystem: FileSystem,
                                          val changeDao: ChangeDao,
                                          val lossyEncoders: Seq[LossyEncoder],
                                          val repositories: Repositories)
                                         (implicit val commandExecutionContext: CommandExecutionContext, clock: Clock) extends SingleCheckinService
  with ThrottlerOps with StrictLogging with Messaging {

  override def encode(stagingFile: StagingFile,
                      flacFile: FlacFile,
                      owners: Set[User])
                     (implicit messagingService: MessageService): Future[Unit] = safely {
    val eventualTempFilesByExtension = {
      val empty: Future[Map[Extension, TempFile]] = Future.successful(Map.empty)
      lossyEncoders.foldLeft(empty) { (eventualMap, lossyEncoder) =>
        for {
          map <- eventualMap
          tempFile <- encodeFile(stagingFile, flacFile, lossyEncoder)
        } yield {
          map + (lossyEncoder.encodesTo -> tempFile)
        }
      }
    }
    for {
      tempFilesByExtension <- eventualTempFilesByExtension
      _ <- moveAndLink(tempFilesByExtension, stagingFile, flacFile, owners)
    } yield {}
  }

  /**
    * Encode a file to a temporary location.
    * @param stagingFile The file to encode.
    * @param flacFile The location when the flac file will end up.
    * @param messageService The service used to report progress and errors.
    * @return The location of the encoded mp3 file.
    */
  def encodeFile(stagingFile: StagingFile, flacFile: FlacFile, lossyEncoder: LossyEncoder)
                (implicit messageService: MessageService): Future[TempFile] = parallel {
    time { duration =>
      val seconds: Double = duration.toMillis.toDouble / 1000d
      ENCODE_DURATION(flacFile, seconds)
    } {
      val encodedFile = flacFile.toEncodedFile(lossyEncoder.encodesTo)
      val tempFile = encodedFile.toTempFile
      log(ENCODE(stagingFile, encodedFile))
      lossyEncoder.encode(stagingFile.absolutePath, tempFile.absolutePath)
      if (!lossyEncoder.copiesTags) {
        tempFile.writeTags()
      }
      else {
        tempFile
      }
    }
  }

  /**
    * Move the staged flac file to the flac repository and the encoded mp3 file to the encoded repository as well
    * as linking to it from within the devices repository.
    * @param tempFilesByExtension The location of the temporary files.
    * @param stagingFile The source flac file.
    * @param flacFile The target location for the flac file.
    * @param owners The owners of the flac file.
    * @param messageService The service used to report progress and errors.
    * @return Eventually nothing.
    */
  def moveAndLink(
                   tempFilesByExtension: Map[Extension, TempFile],
                   stagingFile: StagingFile,
                   flacFile: FlacFile,
                   owners: Set[User])(implicit messageService: MessageService): Future[_] = sequential {
    tempFilesByExtension.toSeq.foreach {
      case (extension, tempFile) =>
        val encodedFile = flacFile.toEncodedFile(extension)
        fileSystem.move(tempFile, encodedFile)
        fileSystem.makeWorldReadable(encodedFile)
        owners.foreach { user =>
          val deviceFile = encodedFile.toDeviceFile(user)
          fileSystem.link(encodedFile, deviceFile)
          Await.result(changeDao.store(Change.added(deviceFile)), Duration.apply(1, TimeUnit.HOURS))
        }
    }
    fileSystem.move(stagingFile, flacFile)
    fileSystem.makeWorldReadable(flacFile)
  }

  override def remove(stagingFile: StagingFile)
                     (implicit messagingService: MessageService): Future[Unit] = safely {
    sequential {
      fileSystem.remove(stagingFile)
    }
  }

  private def safely(block: => Future[Unit])(implicit messageService: MessageService): Future[Unit] = {
    block.recover {
      case e: Exception => logException(e)
    }
  }
}
