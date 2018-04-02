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

package common.owners

import cats.data.{NonEmptyList, ValidatedNel}
import common.configuration.User
import common.files._
import common.message.{Message, MessageService}
import common.music.Tags
import own.OwnAction

import scala.concurrent.Future

/**
  * Allow the listing and changing of which albums should be included on a user's device.
  */
trait OwnerService {

  /**
    * Change the ownership of flac files in the staging repository.
    * @param user The user to add or remove.
    * @param action Add or remove.
    * @param stagingFiles The location of the album's tracks.
    * @param messageService A message service used to report progress and errors.
    */
  def changeStagingOwnership(
                             user: User,
                             action: OwnAction,
                             stagingFiles: NonEmptyList[StagingFile])
                            (implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]]

  /**
    * Change the ownership of flac files in the flac repository.
    * @param user The user to add or remove.
    * @param action Add or remove.
    * @param flacFiles The location of the album's tracks.
    * @param messageService A message service used to report progress and errors.
    */
  def changeFlacOwnership(
                           user: User,
                           action: OwnAction,
                           flacFiles: NonEmptyList[FlacFile])
                         (implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]]

  /**
    * Add an owner to a device file.
    * @param user The user to add or remove.
    * @param deviceFile The device file to own.
    * @param messageService A message service used to report progress and errors.
    */
  def ownDeviceFile(user: User,
                    deviceFile: DeviceFile)
                   (implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]]

  /**
    * List all collections.
    *
    * @return A function that, given [[Tags]], will return the set of [[User]]s who own them.
    */
  def listOwners(): Future[Map[String, Set[User]]]

  /**
    * Remove a set of albums from a user's collection.
    * @param user The user who's collection will be changed.
    * @param tags The tags containing the albums that will be removed.
    * @param messageService The [[MessageService]] used to report progress and log errors.
    */
  def unown(user: User, tags: Set[Tags])(implicit messageService: MessageService): Future[Unit]

}
