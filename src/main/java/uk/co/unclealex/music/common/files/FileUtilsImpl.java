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
 *   http://www.apache.org/licenses/LICENSE-2.0
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

package uk.co.unclealex.music.common.files;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of {@link FileUtils}.
 * 
 * @author alex
 * 
 */
public class FileUtilsImpl implements FileUtils {

  private static final Logger log = LoggerFactory.getLogger(FileUtilsImpl.class);

  /**
   * {@inheritDoc}
   */
  @Override
  public void alterWriteable(Path basePath, Path relativePath, boolean allowWrites) throws IOException {
    Path currentPath = basePath.resolve(relativePath);
    Path terminatingPath = basePath.getParent();
    while (currentPath != null && !currentPath.equals(terminatingPath)) {
      if (Files.exists(currentPath)) {
        Set<PosixFilePermission> posixFilePermissions = Files.getPosixFilePermissions(currentPath);
        if (allowWrites) {
          log.debug("Setting " + currentPath + " to read and write.");
          posixFilePermissions.add(PosixFilePermission.OWNER_WRITE);
        }
        else {
          log.debug("Setting " + currentPath + " to read only.");
          posixFilePermissions.removeAll(Arrays.asList(
              PosixFilePermission.OWNER_WRITE,
              PosixFilePermission.GROUP_WRITE,
              PosixFilePermission.OTHERS_WRITE));
        }
        Files.setPosixFilePermissions(currentPath, posixFilePermissions);
      }
      currentPath = currentPath.getParent();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void move(Path sourceBasePath, Path relativePath, Path targetBasePath) throws IOException {
    Path sourcePath = sourceBasePath.resolve(relativePath);
    Path targetPath = targetBasePath.resolve(relativePath);
    Files.createDirectories(targetPath.getParent());
    Files.move(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE);
    Path currentDirectory = sourcePath.getParent();
    remove(sourceBasePath, currentDirectory);
  }

  @Override
  public void remove(Path basePath, Path currentPath) throws IOException {
    if (Files.isSameFile(basePath, currentPath)) {
      return;
    }
    else if (Files.isDirectory(currentPath)) {
      try (DirectoryStream<Path> dir = Files.newDirectoryStream(currentPath)) {
        boolean directoryIsEmpty = !dir.iterator().hasNext();
        if (directoryIsEmpty) {
          Files.delete(currentPath);
          remove(basePath, currentPath.getParent());
        }
      }
    }
    else {
      Files.delete(currentPath);
      remove(basePath, currentPath.getParent());
    }
  }

}
