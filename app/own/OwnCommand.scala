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

package own

import common.configuration.User
import common.files.{FlacFileLocation, StagedFlacFileLocation}
import common.message.MessageService
import enumeratum._

import scala.collection.immutable
import scala.concurrent.Future

/**
  * The own command.
  */
trait OwnCommand {

  /**
    * Change the ownership of a list of either flac or staged files.
    * @param action Whether to own or unown.
    * @param users The users who will be owning or unowning the files.
    * @param directoryLocations The locations of the directories to own or unown.
    * @param messageService The [[MessageService]] used to report progress and errors.
    * @return A command to be executed.
    */
  def changeOwnership(action: OwnAction, users: Seq[User], directoryLocations: Seq[Either[StagedFlacFileLocation, FlacFileLocation]])
                     (implicit messageService: MessageService): Future[_]

}

/**
  * Actions for owning and unowning tracks.
  */
sealed trait OwnAction extends EnumEntry

/**
  * Contains the different own actions.
  */
object OwnAction extends Enum[OwnAction] {

  /**
    * The action for adding albums to a user's collection.
    */
  object Own extends OwnAction

  /**
    * The action for removing albums from a user's collection.
    */
  object Unown extends OwnAction

  /**
    * @inheritdoc
    */
  override def values: immutable.IndexedSeq[OwnAction] = findValues
}
