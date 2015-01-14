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
import checkin.actors.Messages._
import common.changes.{ChangeDao, Change}
import common.files.{FileLocationExtensions, FileSystem}
import scaldi.Injector
import scaldi.akka.AkkaInjectable

/**
 * Created by alex on 11/01/15.
 */
class FileSystemActor(implicit inj: Injector) extends Actor with AkkaInjectable {

  val fileSystem = inject[FileSystem]
  implicit val changeDao = inject[ChangeDao]
  implicit val fileLocationExtensions = inject[FileLocationExtensions]

  override def receive = {
    case DeleteFileLocation(stagedFlacFileLocation, messageService) => {
      implicit val _messageService = messageService
      fileSystem.remove(stagedFlacFileLocation)
      sender ! Completed
    }

    case LinkAndMoveFileLocations(tempEncodedLocation, encodedFileLocation, stagedFlacFileLocation, flacFileLocation, users, messageService) => {
      implicit val _messageService = messageService
      fileSystem.move(tempEncodedLocation, encodedFileLocation)
      users.foreach { user =>
        val deviceFileLocation = encodedFileLocation.toDeviceFileLocation(user)
        fileSystem.link(encodedFileLocation, deviceFileLocation)
        Change.added(deviceFileLocation).store
      }
      fileSystem.move(stagedFlacFileLocation, flacFileLocation)
      sender ! Completed
    }
  }
}
