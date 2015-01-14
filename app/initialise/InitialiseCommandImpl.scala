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

package initialise

import common.changes.{Change, ChangeDao}
import common.configuration.{Directories, Users}
import common.files.{DeviceFileLocation, DirectoryService, FileLocationExtensions}
import common.message.MessageTypes._
import common.message.{MessageService, Messaging}

import scala.collection.SortedSet
import common.commands.CommandType
import common.commands.CommandType._

/**
 * Created by alex on 06/12/14.
 */
class InitialiseCommandImpl(val users: Users, val directoryService: DirectoryService)
                           (implicit val changeDao: ChangeDao, implicit val directories: Directories, val fileLocationExtensions: FileLocationExtensions) extends InitialiseCommand with Messaging {

  /**
   * Initialise the database with all device files.
   */
  override def initialiseDb(implicit messageService: MessageService): CommandType = synchronous {
    if (changeDao.countChanges() != 0) {
      log(DATABASE_NOT_EMPTY())
    }
    else {
      users.allUsers.foreach { user =>
        val root = DeviceFileLocation(user)
        val deviceFileLocations: SortedSet[DeviceFileLocation] = directoryService.listFiles(Some(root))
        deviceFileLocations.foreach { deviceFileLocation =>
          log(INITIALISING(deviceFileLocation))
          Change.added(deviceFileLocation).store
        }
      }
    }
  }
}
