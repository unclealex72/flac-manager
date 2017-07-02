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

import common.async.CommandExecutionContext
import common.message.{MessageService, Messaging}

import scala.concurrent.Future

/**
  * The [[CheckinService]] that delegates to a [[SingleCheckinService]]
  * @param singleCheckinService The service that will encode or remove files.
  * @param lossyEncoders The list of available lossy encoders.
  * @param ec An execution context used to fire off checkin actions.
  */
class CheckinServiceImpl @Inject()(singleCheckinService: SingleCheckinService)
                                  (implicit val commandExecutionContext: CommandExecutionContext) extends CheckinService with Messaging {

  /**
    * @inheritdoc
    */
  override def checkin(actions: Seq[Action])(implicit messagingService: MessageService): Future[Unit] = {
    val eventualActions: Seq[Future[Unit]] = actions.map(action => singleCheckin(action).map(_ => {}))
    Future.sequence(eventualActions).map(_ => {})
  }

  def singleCheckin(action: Action)(implicit messagingService: MessageService): Future[Unit] = action match {
    case Delete(stagingFile) =>
      singleCheckinService.remove(stagingFile)
    case Encode(stagingFile, flacFile, _, owners) =>
      singleCheckinService.encode(stagingFile, flacFile, owners)
  }
}
