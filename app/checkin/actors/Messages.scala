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
 * Created by alex on 11/01/15.
 */
object Messages {

  case class Actions(actions: Seq[Action], messageService: MessageService)

  case class EncodeFlacFileLocation(stagedFlacFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags, owners: Set[User], messageService: MessageService)

  case class DeleteFileLocation(stagedFlacFileLocation: StagedFlacFileLocation, messageService: MessageService)

  case class LinkAndMoveFileLocations(
                                       tempEncodedLocation: TemporaryFileLocation,
                                       encodedFileLocation: EncodedFileLocation,
                                       stagedFlacFileLocation: StagedFlacFileLocation,
                                       flacFileLocation: FlacFileLocation, users: Set[User], messageService: MessageService)

  case class CheckinFailed(stagedFlacFileLocation: StagedFlacFileLocation, e: Exception, messageService: MessageService)

  object Completed
}
