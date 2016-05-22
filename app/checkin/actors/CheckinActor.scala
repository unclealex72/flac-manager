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

import akka.actor.Actor
import akka.routing.RoundRobinPool
import akka.util.Timeout
import checkin.actors.Messages._
import checkin.{Delete, Encode}
import com.typesafe.scalalogging.StrictLogging
import common.changes.{Change, ChangeDao}
import common.files.{FileLocationExtensions, FileSystem}
import common.message.{MessageService, Messaging}
import common.message.MessageTypes.EXCEPTION
import scaldi.Injector
import scaldi.akka.AkkaInjectable

class CheckinActor(implicit inj: Injector) extends Actor with AkkaInjectable with StrictLogging with Messaging {

  val fileSystem = inject[FileSystem]
  implicit val changeDao = inject[ChangeDao]
  implicit val fileLocationExtensions = inject[FileLocationExtensions]

  val numberOfEncodingActors = inject[Int]('numberOfConcurrentEncoders)
  val encodingProps = injectActorProps[EncodingActor].withRouter(RoundRobinPool(numberOfEncodingActors))
  logger info s"Using $numberOfEncodingActors concurrent encoders"

  var numberOfFilesRemaining = 0

  override def receive = {
    case Actions(actions, messageService) =>
      numberOfFilesRemaining = actions.length
      val encodingActor = context.actorOf(encodingProps)

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

    case LinkAndMoveFileLocations(tempEncodedLocation, encodedFileLocation, stagedFlacFileLocation, flacFileLocation, users, messageService) => {
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
    }

    case CheckinFailed(e, stagedFlacFileLocation, messageService) =>
      implicit val _messageService = messageService
      decreaseFileCount
  }

  def safely(block: => Unit)(implicit messageService: MessageService): Unit = {
    try {
      block
    }
    catch {
      case e: Exception => log(EXCEPTION(e))
    }
  }

  def decreaseFileCount(implicit messageService: MessageService) = {
    numberOfFilesRemaining -= 1
    if (numberOfFilesRemaining == 0) {
      messageService.finish
    }
  }
}
