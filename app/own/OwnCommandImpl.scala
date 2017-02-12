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

package own

import javax.inject.Inject

import common.commands.CommandType
import common.commands.CommandType._
import common.configuration.User
import common.files.{DirectoryService, FlacFileChecker, StagedFlacFileLocation}
import common.message.MessageService
import common.music.TagsService
import common.owners.OwnerService

/**
 * Created by alex on 23/11/14.
 */
class OwnCommandImpl @Inject()(val ownerService: OwnerService, directoryService: DirectoryService)(implicit val flacFileChecker: FlacFileChecker, implicit val tagsService: TagsService) extends OwnCommand {

  override def changeOwnership(action: OwnAction, users: Seq[User], locations: Seq[StagedFlacFileLocation])(implicit messageService: MessageService): CommandType = synchronous {
    val allLocations = directoryService.listFiles(locations)
    val tags = allLocations.filter(_.isFlacFile).flatMap(_.readTags.toOption).toSet
    val changeOwnershipFunction: User => Unit = action match {
      case Own => user => ownerService.own(user, tags)
      case Unown => user => ownerService.unown(user, tags)
    }
    users.foreach(changeOwnershipFunction)
  }
}
