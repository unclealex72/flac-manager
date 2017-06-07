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

import java.util.concurrent.TimeUnit
import javax.inject.Inject

import cats.data.NonEmptyList
import com.typesafe.scalalogging.StrictLogging
import common.async.CommandExecutionContext
import common.changes.{Change, ChangeDao}
import common.configuration.{Directories, User, UserDao}
import common.files.{DeviceFileLocation, DirectoryService, FileLocationExtensions}
import common.message.Messages._
import common.message.{MessageService, Messaging}
import common.music.{Tags, TagsService}
import common.owners.OwnerService
import own.OwnAction

import scala.collection.SortedSet
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

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
  * @param ec The execution context used to execute the command.
  */
class InitialiseCommandImpl @Inject()(
                                       val userDao: UserDao,
                                       val directoryService: DirectoryService,
                                       val tagsService: TagsService,
                                       val ownerService: OwnerService)
                           (implicit val changeDao: ChangeDao,
                            val directories: Directories,
                            val fileLocationExtensions: FileLocationExtensions,
                            val commandExecutionContext: CommandExecutionContext) extends InitialiseCommand with Messaging with StrictLogging {

  case class InitialFile(deviceFileLocation: DeviceFileLocation, tags: Tags, user: User)

  implicit val initialFileOrdering: Ordering[InitialFile] = Ordering.by(i => (i.user, i.deviceFileLocation))
  private val emptyFuture: Future[Unit] = Future.successful({})


  /**
    * @inheritdoc
    */
  override def initialiseDb(implicit messageService: MessageService): Future[_] = {
    changeDao.countChanges().flatMap {
      case 0 =>
        log(INITIALISATION_STARTED)
        val initialFiles: Seq[InitialFile] = for {
          user <- userDao.allUsers().toSeq
          deviceFileLocation <- listFiles(user)
        } yield {
          InitialFile(deviceFileLocation, tagsService.readTags(deviceFileLocation.path), user)
        }
        val releasesByUserAndAlbumId =
          initialFiles.sorted.groupBy(i => (i.user, i.tags.albumId)).mapValues(is => is.head.deviceFileLocation)
        releasesByUserAndAlbumId.foldLeft(addDeviceFileChanges(initialFiles)) { (acc, entry) =>
          val ((user, _), deviceFileLocation) = entry
          for {
            _ <- acc
            _ <- addRelease(user, deviceFileLocation)
          } yield {}
        }
      case _ => Future.successful(log(DATABASE_NOT_EMPTY))
    }
  }

  def addDeviceFileChanges(initialFiles: Seq[InitialFile])(implicit messageService: MessageService): Future[Unit] = {
    initialFiles.foldLeft(emptyFuture){ (acc, initialFile) =>
      val deviceFileLocation = initialFile.deviceFileLocation
      log(INITIALISING(deviceFileLocation))
      for {
        _ <- acc
        _ <- changeDao.store(Change.added(deviceFileLocation))
      } yield {}
    }
  }

  def addRelease(user: User, deviceFileLocation: DeviceFileLocation)(implicit messageService: MessageService): Future[Unit] = {
    ownerService.ownDeviceFile(user, deviceFileLocation)
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
