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
package common.changes

import common.configuration.User
import org.joda.time.DateTime

/**
  * The data access object for keeping track of changes to the flac repository.
  *
  */
trait ChangeDao {
  /**
   * Count the number of added albums since a given date/time
   * @param user The user making the request.
   * @param since The earliest time to search for changes.
   * @return A count of all the changelog items since the given time.
   */
  def countChangelogSince(user: User, since: DateTime): Long

  /**
    * Persist a change.
    * @param change The [[Change]] to persist.
    * @return The persisted change.
    */
  def store(change: Change): Change

  /**
    * Get all the changes for a given user since a given time. Changes are ordered by deletions first and
    * then by artist, album and track number.
    * @param user The user making the request.
    * @param since The earliest time to search for changes.
    * @return A list of all changes.
    */
  def getAllChangesSince(user: User, since: DateTime): List[Change]

  /**
   * Count all changes.
   * @return A count of all changes.
   */
  def countChanges(): Long

  /**
    * Count all the changes for a given user since a given time.
    * @param user The user making the request.
    * @param since The earliest time to search for changes.
    * @return A count of all changes.
    */
  def countChangesSince(user: User, since: DateTime): Long

  /**
    * Get a list of changelog items for a given user since a given amount of time. A changelog item
    * indicates that an album has been added or removed.
    * @param user The user making the request.
    * @param since The earliest time to search for changes.
    * @return A list of changelog items.
    */
  def changelog(user: User, since: DateTime): List[ChangelogItem]

  /**
    * Count the total number of changelog items for a user.
    * @param user The user making the request.
    * @return The number of changelog items for that user.
    */
  def countChangelog(user: User): Long

}
