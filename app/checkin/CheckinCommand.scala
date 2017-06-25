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

import cats.data.ValidatedNel
import common.files.Directory.StagingDirectory
import common.message.{Message, MessageService}

import scala.collection.SortedSet
import scala.concurrent.Future

/**
 * The command that checks in flac files from the staging directory.
 */
trait CheckinCommand {

  /**
    * Check in a set of staged flac files.
    * @param directories The files to check in
    * @param allowUnowned True if unowned files should be allowed to be checked in, false otherwise.
    * @param messageService The [[MessageService]] used to report progress.
    * @return A [[Future]] that checks in a list of flac files to the staging repository.
    */
  def checkin(directories: SortedSet[StagingDirectory],
              allowUnowned: Boolean)(implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]]

}
