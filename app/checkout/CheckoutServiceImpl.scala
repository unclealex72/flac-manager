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

import javax.inject.Inject

import common.changes.{Change, ChangeDao}
import common.configuration.{Directories, User, UserDao}
import common.files._
import common.message.MessageService
import common.music.{Tags, TagsService}
import common.now.NowService
import common.owners.OwnerService

import scala.collection.{SortedMap, SortedSet}

/**
  * The default implementation of [[CheckoutService]]
  * @param fileSystem The [[FileSystem]] used to manipulate files.
  * @param userDao The dao used to get all users.
  * @param ownerService The service used to find who owns which flac files.
  * @param nowService The service used to get the current time.
  * @param changeDao The dao used to store repository changes.
  * @param directories The locations of the repositories.
  * @param fileLocationExtensions A typeclass to give [[java.nio.file.Path]] like functionality to [[FileLocation]]s.
  * @param tagsService The service used to read tags from audio files.
  */
class CheckoutServiceImpl @Inject()(
                                     val fileSystem: FileSystem,
                                     val userDao: UserDao,
                                     val ownerService: OwnerService,
                                     val nowService: NowService)
                         (implicit val changeDao: ChangeDao,
                          val directories: Directories,
                          val fileLocationExtensions: FileLocationExtensions,
                          val tagsService: TagsService)
  extends CheckoutService {

  /**
    * @inheritdoc
    */
  override def checkout(
                         flacFileLocationsByParent: SortedMap[FlacFileLocation, SortedSet[FlacFileLocation]],
                         unown: Boolean)(implicit messageService: MessageService): Unit = {
    val tagsForUsers: Map[User, Set[Tags]] =
      flacFileLocationsByParent.foldLeft(Map.empty[User, Set[Tags]])(findTagsAndDeleteFiles)
    if (unown) {
      tagsForUsers.foreach { case (user, tagsSet) =>
        ownerService.unown(user, tagsSet)
      }
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
                              fls: (FlacFileLocation, SortedSet[FlacFileLocation]))
                            (implicit messageService: MessageService): Map[User, Set[Tags]] = {
    val directory = fls._1
    val flacFileLocations = fls._2
    flacFileLocations.find(_ => true) match {
      case Some(firstFlacFileLocation) => findTagsAndDeleteFiles(tagsForUsers, directory, firstFlacFileLocation, flacFileLocations)
      case None => tagsForUsers
    }
  }

  /**
    * Here be dragons!
    * @param tagsForUsers The currently known tags for users
    * @param directory A directory containing flac files.
    * @param firstFlacFileLocation The first flac file in the directory.
    * @param flacFileLocations The flac files in the directory.
    * @param messageService The [[MessageService]] used to report progress and log errors.
    * @return A map of users and the tags of the files they one.
    */
  def findTagsAndDeleteFiles(
                              tagsForUsers: Map[User, Set[Tags]], directory: FlacFileLocation,
                              firstFlacFileLocation: FlacFileLocation, flacFileLocations: SortedSet[FlacFileLocation])(implicit messageService: MessageService): Map[User, Set[Tags]] = {
    val owners = quickOwners(firstFlacFileLocation)
    val tags = firstFlacFileLocation.readTags.toOption
    flacFileLocations.foreach { flacFileLocation =>
      val encodedFileLocation = flacFileLocation.toEncodedFileLocation
      val deviceFileLocations: Set[DeviceFileLocation] = owners.map { user => encodedFileLocation.toDeviceFileLocation(user)}
      deviceFileLocations.foreach { deviceFileLocation =>
        Change.removed(deviceFileLocation, nowService.now()).store
      }
      deviceFileLocations.foreach(fileSystem.remove(_))
      fileSystem.remove(encodedFileLocation)
      fileSystem.move(flacFileLocation, flacFileLocation.toStagedFlacFileLocation)
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
   * @param flacFileLocation A flac file for the album.
   * @return A list of users who own the album.
   */
  def quickOwners(flacFileLocation: FlacFileLocation): Set[User] = userDao.allUsers().filter { user =>
    flacFileLocation.toEncodedFileLocation.toDeviceFileLocation(user).exists
  }
}
