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

import java.nio.file.Path
import java.time.Instant

import common.configuration.User
import common.files.{DeviceFile, Extension}
import enumeratum._

import scala.collection.immutable
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
                   parentRelativePath: Option[Path],
                   relativePath: Path,
                   at: Instant,
                   user: User,
                   extension: Extension,
                   action: ChangeType)

/**
  * A type safe enumeration for change types.
  */
sealed case class ChangeType(action: String) extends EnumEntry

object ChangeType extends Enum[ChangeType] {
  object Added extends ChangeType("added")
  object Removed extends ChangeType("removed")
  override def values: immutable.IndexedSeq[ChangeType] = findValues
}
/**
  * The "added" type of change.
  */
object AddedChange extends ChangeType("added")

/**
  * The removed type of change.
  */
object RemovedChange extends ChangeType("removed")

/**
  * Used to create [[Change]] instances.
  */
object Change {

  /**
    * Create a change that indicates a file has been added, using the last modified time of the file as the time of addition.
    * @param deviceFile The location of the device file.
    * @return A change timed at when the file was last modified.
    */
  def added(deviceFile: DeviceFile): Change =
    added(deviceFile, deviceFile.lastModified)

  /**
    * Create a change that indicates a file has been added.
    * @param deviceFile The location of the device file.
    * @param at The time the file was added.
    * @return A change timed at when the file was last modified.
    */
  def added(deviceFile: DeviceFile, at: Instant): Change =
    create(AddedChange, deviceFile, storeParent = true, at)

  /**
    * Create a change that indicates a file has been removed.
    * @param deviceFile The location of the device file that was removed.
    * @param at The time when the file was removed.
    * @return A new removal change.
    */
  def removed(deviceFile: DeviceFile, at: Instant): Change = create(RemovedChange, deviceFile, storeParent = false, at)

  private def create(action: ChangeType, deviceFile: DeviceFile, storeParent: Boolean, at: Instant): Change = {
    val relativePath: Path = deviceFile.relativePath
    val parentPath: Option[Path] = if (storeParent) Some(relativePath.getParent) else None
    Change(0, parentPath, relativePath, at, deviceFile.user, deviceFile.extension, action)
  }
}