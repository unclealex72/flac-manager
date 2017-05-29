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

import javax.inject.Inject

import akka.actor.Actor
import checkin.Mp3Encoder
import checkin.actors.Messages._
import common.configuration.Directories
import common.files.Extension.MP3
import common.files.{FileLocationExtensions, TemporaryFileLocation}
import common.message.Messages.{ENCODE, EXCEPTION}
import common.message.Messaging
import common.music.TagsService
import logging.ApplicationLogging

/**
  * An actor that encodes flac files to mp3.
  * @constructor
  * @param mp3Encoder An mp3 encoder.
  * @param tagsService The service used to read audio tags.
  * @param directories The locations of the different directories.
  * @param fileLocationExtensions The typeclass used to give [[common.files.FileLocation]]s path-like functionality.
 */
class EncodingActor @Inject()(implicit val mp3Encoder: Mp3Encoder,
                              val tagsService: TagsService,
                              val directories: Directories,
                              val fileLocationExtensions: FileLocationExtensions) extends Actor with Messaging with ApplicationLogging {


  /**
    * The actor's receive method. The message can be either an [[EncodeFlacFileLocation]] or a [[DeleteFileLocation]]
    * @return
    */
  def receive: PartialFunction[Any, Unit] = {
    case EncodeFlacFileLocation(stagedFlacFileLocation, flacFileLocation, tags, owners, messageService, completionNotifier) =>
      implicit val _messageService = messageService
      try {
        val tempEncodedLocation = TemporaryFileLocation.create(MP3)
        val encodedFileLocation = flacFileLocation.toEncodedFileLocation
        log(ENCODE(stagedFlacFileLocation, encodedFileLocation))
        stagedFlacFileLocation.encodeTo(tempEncodedLocation)
        tempEncodedLocation.writeTags(tags)
        sender ! LinkAndMoveFileLocations(tempEncodedLocation, encodedFileLocation, stagedFlacFileLocation, flacFileLocation, owners, messageService, completionNotifier)
      }
      catch {
        case e: Exception =>
          log(EXCEPTION(e))
          sender ! CheckinFailed(stagedFlacFileLocation, e, messageService, completionNotifier)
      }
    case DeleteFileLocation(stagedFlacFileLocation, messageService, completionNotifier) =>
      sender ! DeleteFileLocation(stagedFlacFileLocation, messageService, completionNotifier)

  }
}