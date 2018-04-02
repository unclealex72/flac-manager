/*
 * Copyright 2018 Alex Jones
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

import common.message.MessageService

import scala.concurrent.Future

/**
  * A trait to checkin flac files into the flac repository.
  */
trait CheckinService {

  /**
    * Check in a set of flac files.
    * @param actions The [[Action]]s that need to be performed to check in flac files.
    * @param messagingService The [[MessageService]] used to report progress and errors.
    * @return A [[Future]] that will perform the checkin actions.
    */
  def checkin(actions: Seq[Action])(implicit messagingService: MessageService): Future[Unit]

}
