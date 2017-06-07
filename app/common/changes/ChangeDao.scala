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

import java.time.Instant

import common.configuration.User
import scala.concurrent.Future

/**
  * The data access object for keeping track of changes to the flac repository.
  *
  */
trait ChangeDao {

  /**
    * Persist a change.
    * @param change The [[Change]] to persist.
    * @return The persisted change.
    */
  def store(change: Change): Future[Unit]

  /**
    * Count the total number of changes.
    * @return The total number of changes.
    */
  def countChanges(): Future[Int]

  /**
    * Get all the changes for a given user since a given time. Changes are ordered by deletions first and
    * then by artist, album and track number.
    * @param user The user making the request.
    * @param since The earliest time to search for changes.
    * @return A list of all changes.
    */
  def getAllChangesSince(user: User, since: Instant): Future[Seq[Change]]

  /**
    * Get a list of changelog items for a given user since a given amount of time. A changelog item
    * indicates that an album has been added or removed.
    * @param user The user making the request.
    * @param since The earliest time to search for changes.
    * @return A list of changelog items.
    */
  def changelog(user: User, since: Instant): Future[Seq[ChangelogItem]]
}
