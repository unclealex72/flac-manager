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
import common.configuration.{Directories, User, UserDao}
import common.files.{DeviceFileLocation, DirectoryService, FileLocationExtensions}
import common.message.Messages._
import common.message.{MessageService, Messaging}
import common.music.TagsService

import scala.collection.SortedSet
import scala.concurrent.{ExecutionContext, Future}

/**
  * The default implementation of [[InitialiseCommand]]. This implementation scans all user repositories and
  * adds each file found as an addition at the time the file was last modified.
  *
  * @param userDao The [[UserDao]] used to list users.
  * @param directoryService The [[DirectoryService]] used to read directories.
  * @param tagsService The [[TagsService]] used to read audio tags.
  * @param changeDao The [[ChangeDao]] used to update the changes in the database.
  * @param directories The [[Directories]] pointing to all the repositories.
  * @param fileLocationExtensions The typeclass used to give [[java.nio.file.Path]]-like functionality to
  *                               [[common.files.FileLocation]]s.
  * @param collectionDao The [[CollectionDao]] used to change a user's collection.
  * @param ec The execution context used to execute the command.
  */
class InitialiseCommandImpl @Inject()(
                                       val userDao: UserDao,
                                       val directoryService: DirectoryService,
                                       val tagsService: TagsService)
                           (implicit val changeDao: ChangeDao,
                            val directories: Directories,
                            val fileLocationExtensions: FileLocationExtensions,
                            val collectionDao: CollectionDao,
                            val ec: ExecutionContext) extends InitialiseCommand with Messaging with StrictLogging {

  /**
    * @inheritdoc
    */
  override def initialiseDb(implicit messageService: MessageService): Future[_] = Future {
    case class InitialFile(deviceFileLocation: DeviceFileLocation, own: InitialOwn)
    case class InitialOwn(releaseId: String, user: User)

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
          InitialOwn(tags.albumId, user))
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

  /**
    * List files for a user.
    * @param user The user who's files have been requested.
    * @param messageService The [[MessageService]] used to report progress and log errors.
    * @return All the files in the user's device repository.
    */
  def listFiles(user: User)(implicit messageService: MessageService): SortedSet[DeviceFileLocation] = {
    val root = DeviceFileLocation(user)
    directoryService.listFiles(Some(root))
  }
}
