/**
 * Copyright 2011 Alex Jones
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

;

import java.nio.file.{Files, Path, StandardCopyOption}

import scala.util.Try
;

/**
 * The default implementation of {@link FileUtils}.
 *
 * @author alex
 *
 */
class FileUtilsImpl extends FileUtils {

  override def move(sourceFileLocation: FileLocation, targetFileLocation: FileLocation): Try[Unit] = Try {
    val sourcePath = sourceFileLocation.resolve
    val targetPath = targetFileLocation.resolve
    Files.createDirectories(targetPath.getParent)
    Files.move(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE)
    val currentDirectory = sourcePath.getParent
    remove(sourceFileLocation.basePath, currentDirectory);
  }

  override def copy(sourceFileLocation: FileLocation, targetFileLocation: FileLocation): Try[Unit] = Try {
    val sourcePath = sourceFileLocation.resolve
    val targetPath = targetFileLocation.resolve
    val parentTargetPath = targetPath.getParent
    Files.createDirectories(parentTargetPath);
    val tempPath = Files.createTempFile(parentTargetPath, "device-file-", ".tmp");
    Files.copy(sourcePath, tempPath, StandardCopyOption.REPLACE_EXISTING);
    Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    val currentDirectory = sourcePath.getParent();
    remove(sourceFileLocation.basePath, currentDirectory);
  }

  override def remove(fileLocation: FileLocation): Try[Unit] = Try {
    remove(fileLocation.basePath, fileLocation.resolve);
  }

  def remove(basePath: Path, currentPath: Path): Try[Unit] = Try {
    if (Files.isSameFile(basePath, currentPath)) {
      // Do nothing
    }
    else if (Files.isDirectory(currentPath)) {
      val dir = Files.newDirectoryStream(currentPath)
      val directoryIsEmpty = !dir.iterator().hasNext()
      if (directoryIsEmpty) {
        Files.delete(currentPath);
        remove(basePath, currentPath.getParent());
      }
      dir.close
    }
    else {
      Files.deleteIfExists(currentPath);
      remove(basePath, currentPath.getParent());
    }
  }

  override def link(fileLocation: FileLocation, linkLocation: FileLocation): Try[Unit] = Try {
    val target = fileLocation.resolve;
    val link = linkLocation.resolve;
    val parent = link.getParent;
    Files.createDirectories(parent);
    val relativeTarget = parent.relativize(target);
    Files.createSymbolicLink(link, relativeTarget);
  }
}
