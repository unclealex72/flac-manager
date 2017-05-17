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
package common.collections

import javax.inject.Inject

import common.configuration.User
import common.db.FlacManagerSchema._
import common.db.SquerylEntryPoint._

/**
 * The Squeryl implementation of both GameDao and Transactional.
 * @author alex
 *
 */
class SquerylCollectionDao @Inject() extends CollectionDao {

  /**
    * Add releases to an owner's collection.
    *
    * @param user          The user whose collection needs changing.
    * @param newReleaseIds The new releases to add to the user's collection.
    */
  override def addReleases(user: User, newReleaseIds: Set[String]): Unit = inTransaction {
    val username = user.name
    newReleaseIds.foreach { newReleaseId =>
      val collectionItem = CollectionItem(0, username, newReleaseId)
      collectionItems.insertOrUpdate(collectionItem)
    }
  }

  /**
    * Remove releases from an owner's collection.
    *
    * @param user          The user whose collection needs changing.
    * @param oldReleaseIds The old releases to remove from the user's collection.
   */
  override def removeReleases(user: User, oldReleaseIds: Set[String]): Unit = inTransaction {
    val username = user.name
    oldReleaseIds.foreach { oldReleaseId =>
      collectionItems.deleteWhere(ci => ci.releaseId === oldReleaseId and ci.user === username)
    }
  }

  /**
    * Get all the releases owned by a user.
    *
    * @param user
    * The user who is doing the searching.
    * @return A list of all the [[http://www.musicbrainz.org MusicBrainz]] releases owned by the user.
    */
  override def releasesForOwner(user: User): Traversable[String] = inTransaction {
    val releases: List[String] = from(collectionItems)(c => where(user.name === c.user) select c.releaseId).distinct
    releases
  }
}