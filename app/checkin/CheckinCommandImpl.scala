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

import javax.inject.Inject

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import checkin.Action._
import common.commands.CommandExecution
import common.commands.CommandExecution._
import common.configuration.User
import common.files._
import common.message.Messages._
import common.message._
import common.music.{Tags, TagsService}
import common.owners.OwnerService
import cats.data._
import cats.implicits._

/**
  * The command that checks in flac files from the staging directory.
  * Created by alex on 12/11/14.
 */
class CheckinCommandImpl @Inject()(
                                    val directoryService: DirectoryService,
                                    val actionGenerator: CheckinActionGenerator,
                                    val checkinService: CheckinService) extends CheckinCommand with Messaging {

  override def checkin(locations: Seq[StagedFlacFileLocation])(implicit messageService: MessageService): CommandExecution = {
    val fileLocations = directoryService.listFiles(locations).toSeq
    actionGenerator.generate(fileLocations) match {
      case Valid(actions) =>
        checkinService.checkin(actions.sorted)
      case Invalid(messageTypes) => synchronous {
        messageTypes.toList.foreach { messageType =>
          log(messageType)
        }
      }
    }
  }
}