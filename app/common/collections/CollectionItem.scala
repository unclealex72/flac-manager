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

import common.files.{DeviceFileLocation, FileLocationExtensions}
import common.joda.JodaDateTime
import org.joda.time.DateTime
import org.squeryl.KeyedEntity

/**
 * A persistable unit that represents a change to a user's encoded repository.
 */
case class CollectionItem(
                           id: Long,
                           user: String,
                           releaseId: String) extends KeyedEntity[Long] {

  /**
   * Squeryl constructor
   */

  protected def this() = this(0, "", "")
}