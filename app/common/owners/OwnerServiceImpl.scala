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
import common.configuration.{User, Users}
import common.message.{MessageService, Messaging}
import common.music.Tags

import scala.concurrent.duration._

/**
 * Created by alex on 15/11/14.
 */
class OwnerServiceImpl @Inject()(val musicBrainzClient: CollectionDao, val users: Users) extends OwnerService with Messaging {

  val timeout: FiniteDuration = 1000000 seconds

  override def listCollections(): Tags => Set[User] = {
    val collectionsByUser = users().map { user =>
      val collection = musicBrainzClient.releasesForOwner(user)
      user -> collection
    }
    tags => collectionsByUser.filter(_._2.exists(id => id == tags.albumId)).map(_._1)

  }

  override def own(user: User, tags: Set[Tags])(implicit messageService: MessageService): Unit = {
    changeOwnership(user, tags, musicBrainzClient.addReleases)
  }

  override def unown(user: User, tags: Set[Tags])(implicit messageService: MessageService): Unit = {
    changeOwnership(user, tags, musicBrainzClient.removeReleases)
  }

  def changeOwnership(user: User, tags: Set[Tags], block: (User, Set[String]) => Unit): Unit = {
    val albumIds = tags.map(_.albumId)
    block(user, albumIds)
  }

}
