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

import checkin.Action
import common.configuration.User
import common.files.{FlacFileLocation, EncodedFileLocation, TemporaryFileLocation, StagedFlacFileLocation}
import common.message.MessageService
import common.music.Tags

/**
 * An object that contains all the different messages that can be sent to the [[CheckinActor]] or the [[EncodingActor]].
  * Created by alex on 11/01/15.
 */
object Messages {

  /**
    * A trait that can be used to notify the outside world that checking in files has finished.
    */
  trait CompletionNotifier {

    /**
      * Notify the outside world that checking in files has finished.
      */
    def finished(): Unit
  }

  /**
    * A message that contains a sequence of [[Action]]s.
    * @param actions The actions to be executed.
    * @param messageService The [[MessageService]] used to report progress.
    * @param completionNotifier The completionNotifier used to tell the outside world that checking in has finished.
    */
  case class Actions(actions: Seq[Action], messageService: MessageService, completionNotifier: CompletionNotifier)

  /**
    * A message that signifies a flac file is to be encoded.
    * @param stagedFlacFileLocation The source flac file.
    * @param flacFileLocation The target flac file.
    * @param tags The audio tags for the flac file.
    * @param owners The flac file's owners.
    * @param messageService The [[MessageService]] used to report progress.
    * @param completionNotifier The completionNotifier used to tell the outside world that checking in has finished.
    */
  case class EncodeFlacFileLocation(
                                     stagedFlacFileLocation: StagedFlacFileLocation,
                                     flacFileLocation: FlacFileLocation,
                                     tags: Tags,
                                     owners: Set[User],
                                     messageService: MessageService,
                                     completionNotifier: CompletionNotifier)

  /**
    * A message that signifies a flac file is to be deleted.
    * @param stagedFlacFileLocation The source flac file.
    * @param messageService The [[MessageService]] used to report progress.
    * @param completionNotifier The completionNotifier used to tell the outside world that checking in has finished.
    */
  case class DeleteFileLocation(
                                 stagedFlacFileLocation: StagedFlacFileLocation,
                                 messageService: MessageService,
                                 completionNotifier: CompletionNotifier)

  /**
    * A message that signifies a staged flac file needs to be moved to the flac repository and an encoded temporary file
    * needs to be copied to the devices and encoded repositories.
     * @param tempEncodedLocation The source location of the encoded file.
    * @param encodedFileLocation The target location of the encoded file.
    * @param stagedFlacFileLocation The location of the source flac file.
    * @param flacFileLocation The location of the target file.
    * @param owners The flac file's owners.
    * @param messageService The [[MessageService]] used to report progress.
    * @param completionNotifier The completionNotifier used to tell the outside world that checking in has finished.
    */
  case class LinkAndMoveFileLocations(
                                       tempEncodedLocation: TemporaryFileLocation,
                                       encodedFileLocation: EncodedFileLocation,
                                       stagedFlacFileLocation: StagedFlacFileLocation,
                                       flacFileLocation: FlacFileLocation,
                                       owners: Set[User],
                                       messageService: MessageService,
                                       completionNotifier: CompletionNotifier)


  /**
    * A message that signifies a checkin has failed.
    * @param stagedFlacFileLocation The source file that could not be checked in.
    * @param e The exception thrown by the failure.
    * @param messageService The [[MessageService]] used to report progress.
    * @param completionNotifier The completionNotifier used to tell the outside world that checking in has finished.
    */
  case class CheckinFailed(
                            stagedFlacFileLocation: StagedFlacFileLocation,
                            e: Exception,
                            messageService: MessageService,
                            completionNotifier: CompletionNotifier)
}
