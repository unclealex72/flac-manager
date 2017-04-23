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

import common.files.{DeviceFileLocation, FileLocationExtensions}
import common.joda.JodaDateTime
import org.joda.time.DateTime
import org.squeryl.KeyedEntity

/**
 * A persistable unit that represents a change to a user's encoded repository.
 */
case class Change(
                   /**
                    * The primary key of the change.
                    */
                   id: Long,

                   /**
                    * The path of the parent directory for adds
                    */
                   parentRelativePath: Option[String],
                   /**
                    * The relative path of the file that changed.
                    */
                   relativePath: String,

                   /**
                    * The time the change occurred.
                    */
                   at: DateTime,

                   /**
                    * The name of the user whose repository changed.
                    */
                   user: String,

                   /**
                    * The action that happened to the file (added or removed).
                    */
                   action: String) extends KeyedEntity[Long] {

  /**
   * Squeryl constructor
   */

  protected def this() = this(0, None, "", new DateTime(), "", "")

  /**
    * Store this change in the database.
    * @param changeDao The [[ChangeDao]] that will
    */
  def store(implicit changeDao: ChangeDao): Unit = changeDao.store(this)
}

object Change {

  def added(deviceFileLocation: DeviceFileLocation)(implicit fileLocationExtensions: FileLocationExtensions): Change =
    apply("added", deviceFileLocation, storeParent = true, JodaDateTime(deviceFileLocation.lastModified))

  def removed(deviceFileLocation: DeviceFileLocation, at: DateTime): Change = apply("removed", deviceFileLocation, storeParent = false, at)

  private def apply(action: String, deviceFileLocation: DeviceFileLocation, storeParent: Boolean, at: DateTime): Change = {
    val relativePath = deviceFileLocation.relativePath
    val parentPath = if (storeParent) Some(relativePath.getParent.toString) else None
    Change(0, parentPath, relativePath.toString, at, deviceFileLocation.user, action)
  }
}