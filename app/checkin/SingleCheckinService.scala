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

import common.configuration.User
import common.files.{FlacFileLocation, StagedFlacFileLocation}
import common.message.MessageService
import common.music.Tags

import scala.concurrent.Future

/**
  * A service that can checkin a single staged flac file.
  **/
trait SingleCheckinService {

  /**
    * Check in a file in the staging repository.
    * @param stagedFileLocation The file to check in.
    * @param flacFileLocation The location where it will end up.
    * @param tags The tags for the file.
    * @param owners The owners of the file.
    * @param messagingService The messaging service used to report errors and progress.
    * @return Eventually nothing.
    */
  def encode(stagedFileLocation: StagedFlacFileLocation,
             flacFileLocation: FlacFileLocation,
             tags: Tags,
             owners: Set[User])(implicit messagingService: MessageService): Future[_]


  /**
    * Remove a file in the staging repository that is not a flac file.
    * @param stagedFlacFileLocation The location of the file to remove.
    * @param messagingService The messaging service used to report errors and progress.
    * @return Eventually nothing.
    */
  def remove(stagedFlacFileLocation: StagedFlacFileLocation)(implicit messagingService: MessageService): Future[_]

}
