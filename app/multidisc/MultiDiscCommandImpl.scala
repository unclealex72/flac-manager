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

import javax.inject.Inject

import common.files.StagedFlacFileLocation
import common.message.MessageService
import json.MultiAction
import json.MultiAction.{Join, Split}

import scala.concurrent.{ExecutionContext, Future}

/**
  * The default implementation of [[MultiDiscCommand]]
  *
  * @param multiDiscService The service used to mutate multi-disc albums.
  * @param ec The execution context used to execute the command.
  **/
class MultiDiscCommandImpl @Inject()(val multiDiscService: MultiDiscService)
                                    (implicit val ec: ExecutionContext) extends MultiDiscCommand {
  /**
    * Either join or split a multi-album
    *
    * @param stagedFlacFileLocations The directories containing the tracks to split or join.
    * @param multiAction             Either join or split.
    * @param messageService          The message service used to report progress and errors.
    * @return A command execution that will split or join a multi-disc album.
    */
  override def mutateMultiDiscAlbum(stagedFlacFileLocations: Seq[StagedFlacFileLocation], multiAction: MultiAction)
                                   (implicit messageService: MessageService): Future[_] = Future {
    multiAction match {
      case Split =>
        multiDiscService.createAlbumWithExtras(stagedFlacFileLocations)
      case Join =>
        multiDiscService.createSingleAlbum(stagedFlacFileLocations)
    }
  }
}