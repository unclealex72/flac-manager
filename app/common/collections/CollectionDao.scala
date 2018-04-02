/*
 * Copyright 2018 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.collections

import common.configuration.User

import scala.concurrent.Future

/**
 * <p>
 * The interface for keeping track of which albums are owned by which users.
 * </p>
 *
 * @author alex
 *
 */
trait CollectionDao {

  /**
   * Get all the releases and who owns them.
   *
   * @return All map of owners keyed by the album ID that they own.
   */
  def allOwnersByRelease(): Future[Map[String, Seq[User]]]

  /**
   * Add a release to an owner's collection.
    *
    * @param user The name of the user whose collection needs changing.
   * @param releaseId The new release to add to the user's collection.
   * @param artist The name of the artist being added.
   * @param album The name of the album being added.
   */
  def addRelease(user: User, releaseId: String, artist: String, album: String): Future[Unit]

  /**
   * Remove a release from an owner's collection.
    *
    * @param user The name of the user whose collection needs changing.
   * @param releaseId The release to remove from the user's collection.
   */
  def removeRelease(user: User, releaseId: String): Future[Unit]
}
