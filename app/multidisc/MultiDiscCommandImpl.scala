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

import cats.data.ValidatedNel
import common.async.CommandExecutionContext
import common.files.Directory.StagingDirectory
import common.message.{Message, MessageService}
import javax.inject.Inject
import json.MultiAction
import json.MultiAction.{Join, Split}

import scala.collection.SortedSet
import scala.concurrent.Future

/**
  * The default implementation of [[MultiDiscCommand]]
  *
  * @param multiDiscService The service used to mutate multi-disc albums.
  * @param commandExecutionContext The execution context used to execute the command.
  **/
class MultiDiscCommandImpl @Inject()(val multiDiscService: MultiDiscService)
                                    (implicit val commandExecutionContext: CommandExecutionContext) extends MultiDiscCommand {
  /**
    * Either join or split a multi-album
    *
    * @param stagingDirectories The directories containing the tracks to split or join.
    * @param multiAction             Either join or split.
    * @param messageService          The message service used to report progress and errors.
    * @return A command execution that will split or join a multi-disc album.
    */
  override def mutateMultiDiscAlbum(stagingDirectories: SortedSet[StagingDirectory], multiAction: MultiAction)
                                   (implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]] = Future {
    multiAction match {
      case Split =>
        multiDiscService.createAlbumWithExtras(stagingDirectories.toSeq)
      case Join =>
        multiDiscService.createSingleAlbum(stagingDirectories.toSeq)
    }
  }
}
