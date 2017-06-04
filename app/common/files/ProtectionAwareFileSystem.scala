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

package common.files

import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
import javax.inject.{Inject, Named}

import logging.ApplicationLogging

import scala.collection.JavaConverters._

/**
  * An implementation of [[FileSystem]] that decorates another [[FileSystem]] and is aware of whether [[FileSystem]]s
  * should be left in a read only or writable state.
  * @param delegate The file system to delegate to.
  * @param fileLocationExtensions The typeclass used to give [[FileLocation]]s [[java.nio.file.Path]]-like functionality.
  *
  */
class ProtectionAwareFileSystem @Inject() (@Named("rawFileSystem") val delegate: FileSystem)
                                          (override implicit val fileLocationExtensions: FileLocationExtensions)
  extends DecoratingFileSystem with ApplicationLogging {

  /**
    * Make all files that need to be writeable.
    * @param fileLocations The file locations to send to the original invocation.
    */
  def before(fileLocations: Seq[FileLocation]): Unit = alterWritable(_ => true, fileLocations)

  /**
    * Make all files that need to be unrwiteable.
    * @param fileLocations The file locations to send to the original invocation.
    */
  def after(fileLocations: Seq[FileLocation]): Unit = alterWritable(!_.readOnly, fileLocations)

  /**
    * Alter all files and their parents (up to the repository base) to be either writeable or unwriteable.
    * @param writable True if files should be made writeable, false otherwise.
    * @param fileLocations The file locations to send to the original invocation.
    */
  def alterWritable(writable: FileLocation => Boolean, fileLocations: Seq[FileLocation]): Unit = {
    fileLocations.foreach { fileLocation =>
      var currentPath = fileLocation.toPath
      val terminatingPath = fileLocation.basePath.getParent
      while (currentPath != null && !currentPath.equals(terminatingPath)) {
        if (Files.exists(currentPath)) {
          val posixFilePermissions = Files.getPosixFilePermissions(currentPath)
          if (writable(fileLocation)) {
            logger.debug("Setting " + currentPath + " to read and write.")
            posixFilePermissions.add(PosixFilePermission.OWNER_WRITE)
          }
          else {
            logger.debug("Setting " + currentPath + " to read only.")
            posixFilePermissions.removeAll(Seq(
              PosixFilePermission.OWNER_WRITE,
              PosixFilePermission.GROUP_WRITE,
              PosixFilePermission.OTHERS_WRITE).asJavaCollection)
          }
          Files.setPosixFilePermissions(currentPath, posixFilePermissions)
        }
        currentPath = currentPath.getParent
      }

    }
  }
}
