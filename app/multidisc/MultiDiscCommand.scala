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

package multidisc

import common.files.StagedFlacFileLocation
import common.message.MessageService
import json.MultiAction

import scala.concurrent.Future

/**
  * The multi command is used for handling albums that span more than one disc. Some MP3 systems do not cope with these
  * well and it can also be the case when a second disc just contains remixes and the like that the user does not
  * always want these played after the main album. This command allows multi-disc albums to be split into albums with
  * different titles and IDs or to be joined into one large album.
  **/
trait MultiDiscCommand {

  /**
    * Either join or split a multi-album
    * @param stagedFlacFileLocations The directories containing the tracks to split or join.
    * @param multiAction Either join or split.
    * @param messageService The message service used to report progress and errors.
    * @return A command execution that will split or join a multi-disc album.
    */
  def mutateMultiDiscAlbum(stagedFlacFileLocations: Seq[StagedFlacFileLocation], multiAction: MultiAction)
                          (implicit messageService: MessageService): Future[_]

}
