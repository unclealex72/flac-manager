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

import javax.inject.Inject

import common.changes.{Change, ChangeDao}
import common.collections.CollectionDao
import common.configuration.{User, UserDao}
import common.files._
import common.message.{MessageService, Messaging}
import common.music.Tags
import common.now.NowService
import own.OwnAction
import own.OwnAction._

/**
  * The default implementation of [[OwnerService]]
  * @param collectionDao The [[CollectionDao]] used to read and alter user's collections.
  * @param fileSystem The [[FileSystem]] used to link and remove device files.
  * @param nowService The [[NowService]] used to get the current time.
  * @param changeDao The [[ChangeDao]] used to tell devices that encoded files need to be added or removed.
  * @param fileLocationExtensions The [[FileLocationExtensions]] used to enrich file locations.
  * @param userDao The [[UserDao]] used to list the current users.
  */
class OwnerServiceImpl @Inject()(
                                  val collectionDao: CollectionDao,
                                  val fileSystem: FileSystem,
                                  val nowService: NowService,
                                  val userDao: UserDao)
                                (implicit val changeDao: ChangeDao,
                                 val fileLocationExtensions: FileLocationExtensions) extends OwnerService with Messaging {

  /**
    * @inheritdoc
    */
  override def listCollections(): Tags => Set[User] = {
    val collectionsByUser = userDao.allUsers().map { user =>
      val collection = collectionDao.releasesForOwner(user)
      user -> collection
    }
    tags => collectionsByUser.filter(_._2.exists(id => id == tags.albumId)).map(_._1)

  }

  /**
    * @inheritdoc
    */
  override def changeStagedOwnership(
                                      user: User,
                                      action: OwnAction,
                                      albumId: String,
                                      stagedFlacFileLocations: Seq[StagedFlacFileLocation])
                                    (implicit messageService: MessageService): Unit = {
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
                                  (implicit messageService: MessageService): Unit = {
    changeOwnership(user, action, albumId, flacFileLocations.map(_.toEncodedFileLocation))
  }

  def changeOwnership(
                       user: User,
                       action: OwnAction,
                       albumId: String,
                       encodedFileLocations: Seq[EncodedFileLocation])
                     (implicit messageService: MessageService): Unit = {
    action match {
      case Own =>
        collectionDao.addReleases(user, Set(albumId))
        encodedFileLocations.foreach { encodedFileLocation =>
          val deviceFileLocation = encodedFileLocation.toDeviceFileLocation(user)
          fileSystem.link(encodedFileLocation, deviceFileLocation)
          Change.added(deviceFileLocation).store
        }
      case Unown =>
        collectionDao.removeReleases(user, Set(albumId))
        encodedFileLocations.foreach { encodedFileLocation =>
          val deviceFileLocation = encodedFileLocation.toDeviceFileLocation(user)
          fileSystem.remove(deviceFileLocation)
          Change.removed(deviceFileLocation, nowService.now()).store
        }
    }
  }

  /**
    * @inheritdoc
    */
  override def unown(user: User, tags: Set[Tags])(implicit messageService: MessageService): Unit = {
    val albumIds = tags.map(_.albumId)
    collectionDao.removeReleases(user, albumIds)
  }

}
