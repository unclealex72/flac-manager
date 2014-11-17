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

package common.files

import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission

import com.typesafe.scalalogging.StrictLogging

import scala.collection.JavaConversions._

/**
 * An implementation of {@link FileUtils} that decorates another {@link FileUtils} and is aware of whether {@link
 * FileLocation}s should be left in a read only or writable state.
 * @author alex
 *
 */
class ProtectionAwareFileSystem(override val delegate: FileSystem)(override implicit val fileLocationUtils: FileLocationExtensions) extends DecoratingFileSystem(delegate) with StrictLogging {

  def before(fileLocations: Seq[FileLocation]): Unit = alterWritable(_ => true, fileLocations)

  def after(fileLocations: Seq[FileLocation]): Unit = alterWritable(!_.readOnly, fileLocations)

  def alterWritable(writable: FileLocation => Boolean, fileLocations: Seq[FileLocation]): Unit = {
    fileLocations foreach alterWritableSingular(writable)
  }

  def alterWritableSingular: (FileLocation => Boolean) => FileLocation => Unit = { allowWritesFactory => fileLocation =>
    var currentPath = fileLocation.toPath
    val terminatingPath = fileLocation.basePath.getParent
    while (currentPath != null && !currentPath.equals(terminatingPath)) {
      if (Files.exists(currentPath)) {
        val posixFilePermissions = Files.getPosixFilePermissions(currentPath);
        if (allowWritesFactory(fileLocation)) {
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
