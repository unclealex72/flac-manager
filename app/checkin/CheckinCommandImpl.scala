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

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import checkin.Action._
import common.async.CommandExecutionContext
import common.files.Directory.StagingDirectory
import common.files.StagingFile
import common.message._
import javax.inject.Inject

import scala.collection.SortedSet
import scala.concurrent.Future

/**
  * The default implementation of [[CheckinCommand]]
  * @param actionGenerator The class that will generate actions from flac files.
  * @param checkinService The checkin service used to check in the flac files.
  * @param commandExecutionContext The execution context used to execute the command.
  */
class CheckinCommandImpl @Inject()(
                                    val actionGenerator: CheckinActionGenerator,
                                    val checkinService: CheckinService)
                                  (implicit val commandExecutionContext: CommandExecutionContext) extends CheckinCommand with Messaging {

  /**
    * @inheritdoc
    */
  override def checkin(directories: SortedSet[StagingDirectory],
                       allowUnowned: Boolean)(implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]] = {
    val fileLocations: SortedSet[StagingFile] = directories.flatMap(_.list)
    actionGenerator.generate(fileLocations.toSeq, allowUnowned).flatMap {
      case Valid(actions) =>
        checkinService.checkin(actions.sorted).map(_ => Valid({}))
      case iv @ Invalid(_) => Future.successful(iv)
    }
  }
}