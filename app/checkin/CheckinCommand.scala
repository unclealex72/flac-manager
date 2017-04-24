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

import common.commands.CommandExecution
import common.files.StagedFlacFileLocation
import common.message.MessageService

/**
 * The command that checks in flac files from the staging directory.
 * Created by alex on 09/11/14.
 */
trait CheckinCommand {

  /**
    * Check in a set of staged flac files.
    * @param locations The files to check in
    * @param messageService The [[MessageService]] used to report progress.
    * @return
    */
  def checkin(locations: Seq[StagedFlacFileLocation])(implicit messageService: MessageService): CommandExecution

}
