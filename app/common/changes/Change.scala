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

import common.files.{DeviceFileLocation, FileLocationExtensions}

/**
  * A persistable unit that represents a change to a user's encoded repository.
  * @param id The primary key of the change.
  * @param parentRelativePath The path of the parent directory for adds.
  * @param relativePath The relative path of the file that changed.
  * @param at The time the change occurred.
  * @param user The name of the user whose repository changed.
  * @param action The action that happened to the file (added or removed).
  */
case class Change(
                   id: Long,
                   parentRelativePath: Option[String],
                   relativePath: String,
                   at: Instant,
                   user: String,
                   action: String)
/**
  * Used to create [[Change]] instances.
  */
object Change {

  /**
    * Create a change that indicates a file has been added, using the last modified time of the file as the time of addition.
    * @param deviceFileLocation The location of the device file.
    * @param fileLocationExtensions The typeclass to give [[java.nio.file.Path]] functionality
    *                               to [[common.files.FileLocation]]s
    * @return A change timed at when the file was last modified.
    */
  def added(deviceFileLocation: DeviceFileLocation)(implicit fileLocationExtensions: FileLocationExtensions): Change =
    create("added", deviceFileLocation, storeParent = true, deviceFileLocation.lastModified)

  /**
    * Create a change that indicates a file has been added.
    * @param deviceFileLocation The location of the device file.
    * @param at The time the file was added.
    * @param fileLocationExtensions The typeclass to give [[java.nio.file.Path]] functionality
    *                               to [[common.files.FileLocation]]s
    * @return A change timed at when the file was last modified.
    */
  def added(deviceFileLocation: DeviceFileLocation, at: Instant)(implicit fileLocationExtensions: FileLocationExtensions): Change =
    create("added", deviceFileLocation, storeParent = true, deviceFileLocation.lastModified)

  /**
    * Create a change that indicates a file has been removed.
    * @param deviceFileLocation The location of the device file that was removed.
    * @param at The time when the file was removed.
    * @return A new removal change.
    */
  def removed(deviceFileLocation: DeviceFileLocation, at: Instant): Change = create("removed", deviceFileLocation, storeParent = false, at)

  private def create(action: String, deviceFileLocation: DeviceFileLocation, storeParent: Boolean, at: Instant): Change = {
    val relativePath = deviceFileLocation.relativePath
    val parentPath = if (storeParent) Some(relativePath.getParent.toString) else None
    Change(0, parentPath, relativePath.toString, at, deviceFileLocation.user, action)
  }

  /**
    * Explicitly required for Slick.
    * @return A function to create a change from a tuple.
    */
  def tupled: ((Long, Option[String], String, Instant, String, String)) => Change = (Change.apply _).tupled

}