/*
 * Copyright 2015 Alex Jones
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

package checkin.actors

import javax.inject.{Inject, Named, Singleton}

import akka.actor.{Actor, ActorRef}
import checkin.actors.Messages._
import checkin.{Delete, Encode}
import common.changes.{Change, ChangeDao}
import common.files.{FileLocationExtensions, FileSystem}
import common.message.Messages.EXCEPTION
import common.message.{MessageService, Messaging}
import logging.ApplicationLogging


/**
  * An actor that checks in flac files by moving them as well as calling telling the [[EncodingActor]] to encode them.
  * @constructor
  * @param fileSystem The underlying file system.
  * @param encodingActor The actor who will encode flac files.
  * @param changeDao The DAO used to persist changes.
  * @param fileLocationExtensions The typeclass used to allow [[common.files.FileLocation]]s to have path-like methods.
  */
@Singleton
class CheckinActor @Inject()(
                              val fileSystem: FileSystem,
                              @Named("encoding-actor")
                              val encodingActor: ActorRef)
                            (implicit val changeDao: ChangeDao,
                             val fileLocationExtensions: FileLocationExtensions) extends Actor with ApplicationLogging with Messaging {


  private var numberOfFilesRemaining = 0

  /**
    * The actor's receive method. This may be either a [[Delete]] or an [[Encode]]
    * @return Nothing.
    */
  override def receive: PartialFunction[Any, Unit] = {
    case Actions(actions, messageService) =>
      numberOfFilesRemaining = actions.length

      actions.foreach {
        case Delete(stagedFlacFileLocation) =>
          encodingActor ! DeleteFileLocation(stagedFlacFileLocation, messageService)
        case Encode(stagedFileLocation, flacFileLocation, tags, users) =>
          encodingActor ! EncodeFlacFileLocation(stagedFileLocation, flacFileLocation, tags, users, messageService)
      }

    case DeleteFileLocation(stagedFlacFileLocation, messageService) =>
      implicit val _messageService = messageService
      safely {
        fileSystem.remove(stagedFlacFileLocation)
      }
      decreaseFileCount

    case LinkAndMoveFileLocations(tempEncodedLocation, encodedFileLocation, stagedFlacFileLocation, flacFileLocation, users, messageService) =>
      implicit val _messageService = messageService
      safely {
        fileSystem.move(tempEncodedLocation, encodedFileLocation)
        users.foreach { user =>
          val deviceFileLocation = encodedFileLocation.toDeviceFileLocation(user)
          fileSystem.link(encodedFileLocation, deviceFileLocation)
          Change.added(deviceFileLocation).store
        }
        fileSystem.move(stagedFlacFileLocation, flacFileLocation)
      }
      decreaseFileCount

    case CheckinFailed(_, _, messageService) =>
      implicit val _messageService = messageService
      decreaseFileCount
  }

  private def safely(block: => Unit)(implicit messageService: MessageService): Unit = {
    try {
      block
    }
    catch {
      case e: Exception => log(EXCEPTION(e))
    }
  }

  private def decreaseFileCount(implicit messageService: MessageService): Unit = {
    numberOfFilesRemaining -= 1
    if (numberOfFilesRemaining == 0) {
      messageService.finish()
    }
  }
}
