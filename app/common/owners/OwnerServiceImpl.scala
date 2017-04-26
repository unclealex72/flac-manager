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

import common.collections.CollectionDao
import common.configuration.{User, UserDao}
import common.message.{MessageService, Messaging}
import common.music.Tags

import scala.concurrent.duration._

/**
  * The default implementation of [[OwnerService]]
  * @param collectionDao The [[CollectionDao]] used to read and alter user's collections.
  * @param userDao The [[UserDao]] used to list the current users.
  */
class OwnerServiceImpl @Inject()(
                                  val collectionDao: CollectionDao,
                                  val userDao: UserDao) extends OwnerService with Messaging {

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
  override def own(user: User, tags: Set[Tags])(implicit messageService: MessageService): Unit = {
    changeOwnership(user, tags, collectionDao.addReleases)
  }

  /**
    * @inheritdoc
    */
  override def unown(user: User, tags: Set[Tags])(implicit messageService: MessageService): Unit = {
    changeOwnership(user, tags, collectionDao.removeReleases)
  }

  private def changeOwnership(user: User, tags: Set[Tags], block: (User, Set[String]) => Unit): Unit = {
    val albumIds = tags.map(_.albumId)
    block(user, albumIds)
  }

}
