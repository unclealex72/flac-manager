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

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import com.typesafe.scalalogging.StrictLogging
import common.async.CommandExecutionContext
import common.changes.{Change, ChangeDao}
import common.configuration.{Directories, User, UserDao}
import common.files._
import common.message.Messages._
import common.message.{Message, MessageService, Messaging}
import common.music.{Tags, TagsService}
import common.owners.OwnerService
import cats.implicits._
import scala.collection.SortedSet
import scala.concurrent.Future

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
                                       val ownerService: OwnerService, val changeDao: ChangeDao,
                                       val repositories: Repositories)
                           (implicit val commandExecutionContext: CommandExecutionContext) extends InitialiseCommand with Messaging with StrictLogging {

  case class InitialFile(deviceFile: DeviceFile, user: User)

  implicit val initialFileOrdering: Ordering[InitialFile] = Ordering.by(i => (i.user, i.deviceFile))
  private val emptyFuture: Future[ValidatedNel[Message, Unit]] = Future.successful(Valid({}))


  /**
    * @inheritdoc
    */
  override def initialiseDb(implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]] = {
    changeDao.countChanges().flatMap {
      case 0 =>
        log(INITIALISATION_STARTED)
        val empty: ValidatedNel[Message, Seq[InitialFile]] = Valid(Seq.empty)
        val validatedInitialFiles: ValidatedNel[Message, Seq[InitialFile]] = userDao.allUsers().foldLeft(empty) { (acc, user) =>
          val validatedInitialFilesForUser = listFiles(user).map(_.map(InitialFile(_, user)))
          (acc |@| validatedInitialFilesForUser).map(_ ++ _)
        }
        validatedInitialFiles match {
          case Valid(initialFiles) =>
            val releasesByUserAndDirectory =
              initialFiles.groupBy(i => (i.user, i.deviceFile.absolutePath.getParent)).mapValues(is => is.head.deviceFile).toSeq.map {
                case ((user, directory), deviceFileLocation) => (user, directory, deviceFileLocation)
              }.sortBy(udd => (udd._1.name, udd._2))
            releasesByUserAndDirectory.foldLeft(addDeviceFileChanges(initialFiles)) { (acc, userDirectoryAndFile) =>
              val (user, _, deviceFileLocation) = userDirectoryAndFile
              for {
                _ <- acc
                _ <- addRelease(user, deviceFileLocation)
              } yield Valid({})
            }
          case iv @ Invalid(_) => Future.successful(iv)
        }
      case _ => Future.successful(Invalid(DATABASE_NOT_EMPTY).toValidatedNel)
    }
  }

  def addDeviceFileChanges(initialFiles: Seq[InitialFile])(implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]] = {
    initialFiles.foldLeft(emptyFuture){ (acc, initialFile) =>
      val deviceFile = initialFile.deviceFile
      log(INITIALISING(deviceFile))
      for {
        _ <- acc
        _ <- changeDao.store(Change.added(deviceFile))
      } yield Valid({})
    }
  }

  def addRelease(user: User, deviceFile: DeviceFile)(implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]] = {
    ownerService.ownDeviceFile(user, deviceFile)
  }

  /**
    * List files for a user.
    * @param user The user who's files have been requested.
    * @param messageService The [[MessageService]] used to report progress and log errors.
    * @return All the files in the user's device repository.
    */
  def listFiles(user: User)(implicit messageService: MessageService): ValidatedNel[Message, SortedSet[DeviceFile]] = {
    repositories.device(user).root.map(_.list)
  }
}
