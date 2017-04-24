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

import javax.inject.Inject

import com.typesafe.scalalogging.StrictLogging
import common.changes.{Change, ChangeDao}
import common.collections.CollectionDao
import common.commands.CommandExecution
import common.commands.CommandExecution._
import common.configuration.{Directories, User, UserDao}
import common.files.{DeviceFileLocation, DirectoryService, FileLocationExtensions}
import common.message.Messages._
import common.message.{MessageService, Messaging}
import common.music.TagsService

import scala.collection.SortedSet

/**
 * Created by alex on 06/12/14.
 */
class InitialiseCommandImpl @Inject()(val userDao: UserDao, val directoryService: DirectoryService, val tagsService: TagsService)
                           (implicit val changeDao: ChangeDao,
                            val directories: Directories,
                            val fileLocationExtensions: FileLocationExtensions,
                            val collectionDao: CollectionDao) extends InitialiseCommand with Messaging with StrictLogging {

  /**
   * Initialise the database with all device files.
   */
  override def initialiseDb(implicit messageService: MessageService): CommandExecution = synchronous {
    if (changeDao.countChanges() != 0) {
      log(DATABASE_NOT_EMPTY)
    }
    else {
      val initialFiles: Seq[InitialFile] = for {
        user <- userDao.allUsers().toSeq
        deviceFileLocation <- listFiles(user)
      } yield {
        log(INITIALISING(deviceFileLocation))
        val path = deviceFileLocation.path
        val tags = tagsService.readTags(path)
        InitialFile(
          deviceFileLocation,
          InitialOwn(tags.artistId, user))
      }
      val deviceFileLocations = initialFiles.map(_.deviceFileLocation)
      val ownsByUser: Map[User, Set[InitialOwn]] = initialFiles.map(_.own).toSet.groupBy(_.user)
      val releasesByUser: Map[User, Set[String]] = ownsByUser.mapValues(_.map(_.releaseId))

      deviceFileLocations.foreach { deviceFileLocation =>
        Change.added(deviceFileLocation).store
      }

      releasesByUser.foreach {
        case (user, releaseIds) =>
          collectionDao.addReleases(user, releaseIds)
      }
      userDao.allUsers().foreach { user =>
        val root = DeviceFileLocation(user)
        val deviceFileLocations: SortedSet[DeviceFileLocation] = directoryService.listFiles(Some(root))
        deviceFileLocations.foreach { deviceFileLocation =>
          log(INITIALISING(deviceFileLocation))
          Change.added(deviceFileLocation).store
        }
      }
    }
  }

  def listFiles(user: User)(implicit messageService: MessageService): SortedSet[DeviceFileLocation] = {
    val root = DeviceFileLocation(user)
    directoryService.listFiles(Some(root))
  }

  case class InitialFile(deviceFileLocation: DeviceFileLocation, own: InitialOwn)
  case class InitialOwn(releaseId: String, user: User)
}
