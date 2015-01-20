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
import checkin.Mp3Encoder
import checkin.actors.Messages._
import common.configuration.Directories
import common.files.{FileLocationExtensions, MP3, TemporaryFileLocation}
import common.message.MessageTypes.ENCODE
import common.message.Messaging
import common.music.TagsService
import scaldi.Injector
import scaldi.akka.AkkaInjectable

/**
 * Created by alex on 11/01/15.
 */
class EncodingActor(implicit inj: Injector) extends Actor with AkkaInjectable with Messaging {

  implicit val mp3Encoder = inject[Mp3Encoder]
  implicit val fileLocationExtensions = inject[FileLocationExtensions]
  implicit val directories = inject[Directories]
  implicit val tagsService = inject[TagsService]

  def receive = {
    case EncodeFlacFileLocation(stagedFlacFileLocation, flacFileLocation, tags, owners, messageService) => {
      implicit val _messageService = messageService
      val tempEncodedLocation = TemporaryFileLocation.create(MP3)
      val encodedFileLocation = flacFileLocation.toEncodedFileLocation
      log(ENCODE(stagedFlacFileLocation, encodedFileLocation))
      stagedFlacFileLocation.encodeTo(tempEncodedLocation)
      tempEncodedLocation.writeTags(tags)
      sender ! LinkAndMoveFileLocations(tempEncodedLocation, encodedFileLocation, stagedFlacFileLocation, flacFileLocation, owners, messageService)
    }
    case DeleteFileLocation(stagedFlacFileLocation, messageService) => {
      sender ! DeleteFileLocation(stagedFlacFileLocation, messageService)
    }

  }
}