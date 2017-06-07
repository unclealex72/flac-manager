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

package common.owners

import java.time.{Clock, Instant}
import javax.inject.Inject

import cats.data.NonEmptyList
import common.async.CommandExecutionContext
import common.changes.{Change, ChangeDao}
import common.collections.CollectionDao
import common.configuration.{User, UserDao}
import common.files._
import common.message.Messages.{ADD_OWNER, REMOVE_OWNER}
import common.message.{MessageService, Messaging}
import common.music.{Tags, TagsService}
import own.OwnAction
import own.OwnAction._

import scala.concurrent.Future

/**
  * The default implementation of [[OwnerService]]
  * @param collectionDao          The [[CollectionDao]] used to read and alter user's collections.
  * @param fileSystem             The [[FileSystem]] used to link and remove device files.
  * @param clock                  The [[java.time.Clock]] used to get the current time.
  * @param changeDao              The [[ChangeDao]] used to tell devices that encoded files need to be added or removed.
  * @param fileLocationExtensions The [[FileLocationExtensions]] used to enrich file locations.
  * @param userDao                The [[UserDao]] used to list the current users.
  */
class OwnerServiceImpl @Inject()(
                                  val collectionDao: CollectionDao,
                                  val fileSystem: FileSystem,
                                  val clock: Clock,
                                  val userDao: UserDao)
                                (implicit val changeDao: ChangeDao,
                                 val fileLocationExtensions: FileLocationExtensions,
                                 val commandExecutionContext: CommandExecutionContext, val tagsService: TagsService) extends OwnerService with Messaging {

  /**
    * @inheritdoc
    */
  override def listOwners(): Future[Map[String, Set[User]]] = {
    val usersByName = userDao.allUsers().groupBy(_.name)
    collectionDao.allOwnersByRelease().map { ownersByRelease =>
      ownersByRelease.mapValues(usernames => usernames.flatMap(username => usersByName.getOrElse(username, Set.empty)).toSet)
    }
  }

  /**
    * @inheritdoc
    */
  override def changeStagedOwnership(
                                      user: User,
                                      action: OwnAction,
                                      stagedFlacFileLocations: NonEmptyList[StagedFlacFileLocation])
                                    (implicit messageService: MessageService): Future[Unit] = {
    changeOwnership(user, action, stagedFlacFileLocations, Seq.empty)
  }

  /**
    * @inheritdoc
    */
  override def changeFlacOwnership(
                                    user: User,
                                    action: OwnAction,
                                    flacFileLocations: NonEmptyList[FlacFileLocation])
                                  (implicit messageService: MessageService): Future[Unit] = {
    changeOwnership(
      user,
      action,
      flacFileLocations,
      flacFileLocations.toList.map(_.toEncodedFileLocation))
  }

  /**
    * @inheritdoc
    */
  override def ownDeviceFile(user: User,
                             deviceFileLocation: DeviceFileLocation)
                            (implicit messageService: MessageService): Future[Unit] = {
    changeOwnership(user, Own, NonEmptyList.of(deviceFileLocation), Seq.empty)
  }

  def changeOwnership[FL <: FileLocation](
                       user: User,
                       action: OwnAction,
                       fileLocations: NonEmptyList[FL],
                       encodedFileLocations: Seq[EncodedFileLocation])
                     (implicit messageService: MessageService): Future[Unit] = {
    // TODO think of a way that tags only need to be read once.
    val tags = fileLocations.head.readTags.toOption.get
    action match {
      case Own =>
        encodedFileLocations.foldLeft(addRelease(user, tags)) { (acc, encodedFileLocation) =>
          val deviceFileLocation = encodedFileLocation.toDeviceFileLocation(user)
          fileSystem.link(encodedFileLocation, deviceFileLocation)
          acc.flatMap(_ => changeDao.store(Change.added(deviceFileLocation))).map(_ => {})
        }
      case Unown =>
        encodedFileLocations.foldLeft(removeRelease(user, tags)) { (acc, encodedFileLocation) =>
          val deviceFileLocation = encodedFileLocation.toDeviceFileLocation(user)
          fileSystem.remove(deviceFileLocation)
          acc.flatMap(_ => changeDao.store(Change.removed(deviceFileLocation, Instant.now(clock)))).map(_ => {})
        }
    }
  }

  private def addRelease(user: User, tags: Tags)(implicit messageService: MessageService): Future[Unit] = {
    log(ADD_OWNER(user, tags.artist, tags.album))
    collectionDao.addRelease(username = user.name, releaseId = tags.albumId, artist = tags.artist, album = tags.album).map(_ => {})
  }

  private def removeRelease(user: User, tags: Tags)(implicit messageService: MessageService): Future[Unit] = {
    log(REMOVE_OWNER(user, tags.artist, tags.album))
    collectionDao.removeRelease(username = user.name, releaseId = tags.albumId).map(_ => {})
  }

  /**
    * @inheritdoc
    */
  override def unown(user: User, tags: Set[Tags])(implicit messageService: MessageService): Future[Unit] = {
    val empty: Future[Unit] = Future.successful({})
    tags.foldLeft(empty){ (acc, myTags) =>
      acc.flatMap(_ => removeRelease(user, myTags))
    }
  }

}
