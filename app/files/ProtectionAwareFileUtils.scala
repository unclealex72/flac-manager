/**
 * Copyright 2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 *
 * @author unclealex72
 *
 */

package files

import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission

import com.typesafe.scalalogging.StrictLogging

import scala.collection.JavaConversions._
import scala.util.Try

/**
 * An implementation of {@link FileUtils} that decorates another {@link FileUtils} and is aware of whether {@link
 * FileLocation}s should be left in a read only or writable state.
 * @author alex
 *
 */
class ProtectionAwareFileUtils(val delegate: FileUtils) extends FileUtils with StrictLogging {

  def wrap(block: => FileUtils => Try[Unit])(fileLocations: FileLocation*): Try[Unit] = {
    unprotect(fileLocations)
    val result = block(delegate)
    protect(fileLocations)
    result
  }

  override def move(sourceFileLocation: FileLocation, targetFileLocation: FileLocation): Try[Unit] =
    wrap(_.move(sourceFileLocation, targetFileLocation))(sourceFileLocation, targetFileLocation)

  override def copy(sourceFileLocation: FileLocation, targetFileLocation: FileLocation): Try[Unit] =
    wrap(_.copy(sourceFileLocation, targetFileLocation))(targetFileLocation)

  override def remove(fileLocation: FileLocation): Try[Unit] =
    wrap(_.remove(fileLocation))(fileLocation)

  override def link(fileLocation: FileLocation, linkLocation: FileLocation): Try[Unit] =
    wrap(_.link(fileLocation, linkLocation))(fileLocation, linkLocation)

  def protect(fileLocations: Seq[FileLocation]): Unit = alterWritable(false, fileLocations)

  def unprotect(fileLocations: Seq[FileLocation]): Unit = alterWritable(true, fileLocations)

  def alterWritable(writable: Boolean, fileLocations: Seq[FileLocation]): Unit = {
    fileLocations foreach alterWritableSingular(writable)
  }

  def alterWritableSingular: Boolean => FileLocation => Unit = { allowWrites => fileLocation =>
    var currentPath = fileLocation.resolve
    val terminatingPath = fileLocation.basePath.getParent
    while (currentPath != null && !currentPath.equals(terminatingPath)) {
      if (Files.exists(currentPath)) {
        val posixFilePermissions = Files.getPosixFilePermissions(currentPath);
        if (allowWrites) {
          logger.debug("Setting " + currentPath + " to read and write.");
          posixFilePermissions.add(PosixFilePermission.OWNER_WRITE);
        }
        else {
          logger.debug("Setting " + currentPath + " to read only.");
          posixFilePermissions.removeAll(Seq(
            PosixFilePermission.OWNER_WRITE,
            PosixFilePermission.GROUP_WRITE,
            PosixFilePermission.OTHERS_WRITE));
        }
        Files.setPosixFilePermissions(currentPath, posixFilePermissions);
      }
      currentPath = currentPath.getParent();
    }
  }
}
