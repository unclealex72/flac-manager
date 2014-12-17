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

import common.changes.{Change, ChangeDao}
import common.configuration.{Directories, User}
import common.files._
import common.message.MessageTypes._
import common.message.{MessageService, Messaging}
import common.music.{Tags, TagsService}

/**
 * Created by alex on 16/11/14.
 */
class CheckinServiceImpl(val fileSystem: FileSystem)
                        (implicit val changeDao: ChangeDao, val fileLocationExtensions: FileLocationExtensions, val directories: Directories, val tagsService: TagsService, val mp3Encoder: Mp3Encoder)
  extends CheckinService with Messaging {

  def delete(location: StagedFlacFileLocation)(implicit messageService: MessageService): Unit = {
    fileSystem.remove(location)
  }

  def encode(stagedFlacFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags, users: Set[User])(implicit messageService: MessageService): Unit = {
    val tempEncodedLocation = TemporaryFileLocation.create(MP3)
    val encodedFileLocation = flacFileLocation.toEncodedFileLocation
    log(ENCODE(stagedFlacFileLocation, encodedFileLocation))
    stagedFlacFileLocation.encodeTo(tempEncodedLocation)
    tempEncodedLocation.writeTags(tags)
    fileSystem.move(tempEncodedLocation, encodedFileLocation)
    users.foreach { user =>
      val deviceFileLocation = encodedFileLocation.toDeviceFileLocation(user)
      fileSystem.link(encodedFileLocation, deviceFileLocation)
      Change.added(deviceFileLocation).store
    }
    fileSystem.move(stagedFlacFileLocation, flacFileLocation)
  }

  override def checkin(action: Action)(implicit messagingService: MessageService): Unit = {
    action match {
      case Delete(stagedFlacFileLocation) =>
        delete(stagedFlacFileLocation)
      case Encode(stagedFlacFileLocation, flacFileLocation, tags, owners) =>
        encode(stagedFlacFileLocation, flacFileLocation, tags, owners)
    }
  }
}
