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

import common.configuration.User

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
   * Get all the releases owned by a user.
   *
   * @param user
   * The user who is doing the searching.
   * @return A list of all the MusicBrainz releases owned by the user.
   */
  def releasesForOwner(user: User): Traversable[String]

  /**
   * Add releases to an owner's collection.
   * @param user The user whose collection needs changing.
   * @param newReleaseIds The new releases to add to the user's collection.
   */
  def addReleases(user: User, newReleaseIds: Set[String]): Unit

  /**
   * Remove releases from an owner's collection.
   * @param user The user whose collection needs changing.
   * @param oldReleaseIds The old releases to remove from the user's collection.
   */
  def removeReleases(user: User, oldReleaseIds: Set[String]): Unit
}
