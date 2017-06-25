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

package checkout

import java.time.{Clock, Instant}
import java.util.concurrent.TimeUnit
import javax.inject.Inject

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import common.async.CommandExecutionContext
import common.changes.{Change, ChangeDao}
import common.configuration.{User, UserDao}
import common.files.Directory.FlacDirectory
import common.files._
import common.message.{Message, MessageService}
import common.music.Tags
import common.owners.OwnerService

import scala.collection.{SortedMap, SortedSet}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * The default implementation of [[CheckoutService]]
  * @param fileSystem The [[FileSystem]] used to manipulate files.
  * @param userDao The dao used to get all users.
  * @param ownerService The service used to find who owns which flac files.
  * @param clock The clock used to get the current time.
  * @param changeDao The dao used to store repository changes.
  * @param directories The locations of the repositories.
  * @param fileLocationExtensions A typeclass to give [[java.nio.file.Path]] like functionality to [[FileLocation]]s.
  * @param tagsService The service used to read tags from audio files.
  * @param executionContext The execution context used to allocate threads to jobs.
  */
class CheckoutServiceImpl @Inject()(
                                     val fileSystem: FileSystem,
                                     val userDao: UserDao,
                                     val ownerService: OwnerService,
                                     val changeDao: ChangeDao,
                                     val clock: Clock)
                         (implicit val commandExecutionContext: CommandExecutionContext)
  extends CheckoutService {

  /**
    * @inheritdoc
    */
  override def checkout(
                         flacFileLocationsByParent: SortedMap[FlacDirectory, SortedSet[FlacFile]],
                         unown: Boolean)(implicit messageService: MessageService): Future[Unit] = {
    val eventualTagsForUsers = Future(flacFileLocationsByParent.foldLeft(Map.empty[User, Set[Tags]])(findTagsAndDeleteFiles))
    if (unown) {
      eventualTagsForUsers.flatMap { tagsForUsers =>
        val empty: Future[Unit] = Future.successful({})
        tagsForUsers.foldLeft(empty){ (acc, tagsForUser) =>
          val (user, tags) = tagsForUser
          acc.flatMap(_ => ownerService.unown(user, tags))
        }
      }
    }
    else {
      eventualTagsForUsers.map(_ => Valid({}))
    }
  }

  /**
    * I'm far too scared to change any of this!
    * @param tagsForUsers The currently known tags for users
    * @param fls A parent flac location and the flac files in that
    * @param messageService The [[MessageService]] used to report progress and log errors.
    * @return A map of users and the tags of the files they one.
    */
  def findTagsAndDeleteFiles(
                              tagsForUsers: Map[User, Set[Tags]],
                              fls: (FlacDirectory, SortedSet[FlacFile]))
                            (implicit messageService: MessageService): Map[User, Set[Tags]] = {
    val directory = fls._1
    val flacFileLocations = fls._2
    flacFileLocations.headOption match {
      case Some(firstFlacFileLocation) => findTagsAndDeleteFiles(tagsForUsers, directory, firstFlacFileLocation, flacFileLocations)
      case None => tagsForUsers
    }
  }

  /**
    * Here be dragons!
    * @param tagsForUsers The currently known tags for users
    * @param directory A directory containing flac files.
    * @param firstFlacFile The first flac file in the directory.
    * @param flacFiles The flac files in the directory.
    * @param messageService The [[MessageService]] used to report progress and log errors.
    * @return A map of users and the tags of the files they one.
    */
  def findTagsAndDeleteFiles(
                              tagsForUsers: Map[User, Set[Tags]], directory: FlacDirectory,
                              firstFlacFile: FlacFile, flacFiles: SortedSet[FlacFile])(implicit messageService: MessageService): Map[User, Set[Tags]] = {
    val owners = quickOwners(firstFlacFile)
    val tags = firstFlacFile.tags.read().toOption
    flacFiles.foreach { flacFile =>
      val encodedFile = flacFile.toEncodedFile
      val deviceFiles: Set[DeviceFile] = owners.map { user => encodedFile.toDeviceFile(user)}
      deviceFiles.foreach { deviceFile =>
        Await.result(changeDao.store(Change.removed(deviceFile, Instant.now(clock))), Duration.apply(1, TimeUnit.HOURS))
        fileSystem.remove(deviceFile)
      }
      fileSystem.remove(encodedFile)
      fileSystem.move(flacFile, flacFile.toStagingFile)
    }
    owners.foldLeft(tagsForUsers) { (tagsForUsers, owner) =>
      tagsForUsers.get(owner) match {
        case Some(tagsSet) => tagsForUsers + (owner -> (tagsSet ++ tags))
        case _ => tagsForUsers + (owner -> tags.toSet)
      }
    }
  }


  /**
   * Find out quickly who owns an album by looking for links in the users' device repository.
   * @param flacFile A flac file for the album.
   * @return A list of users who own the album.
   */
  def quickOwners(flacFile: FlacFile): Set[User] = userDao.allUsers().filter { user =>
    val deviceFile = flacFile.toEncodedFile.toDeviceFile(user)
    deviceFile.exists
  }
}
