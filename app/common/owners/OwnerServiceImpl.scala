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

import common.changes.{Change, ChangeDao}
import common.collections.CollectionDao
import common.configuration.{User, UserDao}
import common.files._
import common.message.{MessageService, Messaging}
import common.music.Tags
import own.OwnAction
import own.OwnAction._

import scala.concurrent.{ExecutionContext, Future}

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
                                 val executionContext: ExecutionContext) extends OwnerService with Messaging {

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
                                      albumId: String,
                                      stagedFlacFileLocations: Seq[StagedFlacFileLocation])
                                    (implicit messageService: MessageService): Future[Unit] = {
    changeOwnership(user, action, albumId, Seq.empty)
  }

  /**
    * @inheritdoc
    */
  override def changeFlacOwnership(
                                    user: User,
                                    action: OwnAction,
                                    albumId: String,
                                    flacFileLocations: Seq[FlacFileLocation])
                                  (implicit messageService: MessageService): Future[Unit] = {
    changeOwnership(user, action, albumId, flacFileLocations.map(_.toEncodedFileLocation))
  }

  def changeOwnership(
                       user: User,
                       action: OwnAction,
                       albumId: String,
                       encodedFileLocations: Seq[EncodedFileLocation])
                     (implicit messageService: MessageService): Future[Unit] = {
    action match {
      case Own =>
        encodedFileLocations.foldLeft(collectionDao.addReleases(user, Set(albumId))) { (acc, encodedFileLocation) =>
          val deviceFileLocation = encodedFileLocation.toDeviceFileLocation(user)
          fileSystem.link(encodedFileLocation, deviceFileLocation)
          acc.flatMap(_ => changeDao.store(Change.added(deviceFileLocation))).map(_ => {})
        }
      case Unown =>
        encodedFileLocations.foldLeft(collectionDao.removeReleases(user, Set(albumId))) { (acc, encodedFileLocation) =>
          val deviceFileLocation = encodedFileLocation.toDeviceFileLocation(user)
          fileSystem.remove(deviceFileLocation)
          acc.flatMap(_ => changeDao.store(Change.removed(deviceFileLocation, Instant.now(clock)))).map(_ => {})
        }
    }
  }

  /**
    * @inheritdoc
    */
  override def unown(user: User, tags: Set[Tags])(implicit messageService: MessageService): Future[Unit] = {
    val albumIds = tags.map(_.albumId)
    collectionDao.removeReleases(user, albumIds)
  }

}
