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
 * The data access object for {@link Game}s.
 *
 * @author alex
 */
trait ChangeDao {
  /**
   * Count the number of added albums since a given date/time
   * @param user
   * @param since
   * @return
   */
  def countChangelogSince(user: User, since: DateTime): Long

  /**
   * Persist a change
   */
  def store(change: Change): Change

  def getAllChangesSince(user: User, since: DateTime): List[Change]

  /**
   * Count all changes.
   * @return
   */
  def countChanges(): Long

  def countChangesSince(user: User, since: DateTime): Long

  def changelog(user: User, pageNumber: Int, limit: Int): List[ChangelogItem]

  def countChangelog(user: User): Long

}
