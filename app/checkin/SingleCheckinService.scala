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
import common.files.{FlacFile, StagingFile}
import common.message.MessageService

import scala.concurrent.Future

/**
  * A service that can checkin a single staged flac file.
  **/
trait SingleCheckinService {

  /**
    * Check in a file in the staging repository.
    * @param stagingFile The file to check in.
    * @param flacFile The location where it will end up.
    * @param owners The owners of the file.
    * @param messagingService The messaging service used to report errors and progress.
    * @return Eventually nothing.
    */
  def encode(stagingFile: StagingFile,
             flacFile: FlacFile,
             owners: Set[User])(implicit messagingService: MessageService): Future[Unit]


  /**
    * Remove a file in the staging repository that is not a flac file.
    * @param stagingFile The location of the file to remove.
    * @param messagingService The messaging service used to report errors and progress.
    * @return Eventually nothing.
    */
  def remove(stagingFile: StagingFile)(implicit messagingService: MessageService): Future[Unit]

}
