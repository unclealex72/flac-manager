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
import checkin.Action._
import common.files._
import common.message._

import scala.concurrent.{ExecutionContext, Future}

/**
  * The default implementation of [[CheckinCommand]]
  * @param directoryService The service used to list flac files.
  * @param actionGenerator The class that will generate actions from flac files.
  * @param checkinService The checkin service used to check in the flac files.
  * @param ec The execution context used to execute the command.
  */
class CheckinCommandImpl @Inject()(
                                    val directoryService: DirectoryService,
                                    val actionGenerator: CheckinActionGenerator,
                                    val checkinService: CheckinService)(implicit val ec: ExecutionContext) extends CheckinCommand with Messaging {

  /**
    * @inheritdoc
    */
  override def checkin(locations: Seq[StagedFlacFileLocation],
                       allowUnowned: Boolean)(implicit messageService: MessageService): Future[_] = {
    val fileLocations = directoryService.listFiles(locations).toSeq
    actionGenerator.generate(fileLocations, allowUnowned).flatMap {
      case Valid(actions) =>
        checkinService.checkin(actions.sorted)
      case Invalid(messageTypes) => Future {
        messageTypes.toList.foreach { messageType =>
          log(messageType)
        }
      }
    }
  }
}