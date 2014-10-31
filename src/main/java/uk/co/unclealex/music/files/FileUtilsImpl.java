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

package uk.co.unclealex.music.files;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * The default implementation of {@link FileUtils}.
 * 
 * @author alex
 * 
 */
public class FileUtilsImpl implements FileUtils {

  /**
   * {@inheritDoc}
   */
  @Override
  public void move(FileLocation sourceFileLocation, FileLocation targetFileLocation)
      throws IOException {
    Path sourcePath = sourceFileLocation.resolve();
    Path targetPath = targetFileLocation.resolve();
    Files.createDirectories(targetPath.getParent());
    Files.move(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE);
    Path currentDirectory = sourcePath.getParent();
    remove(sourceFileLocation.getBasePath(), currentDirectory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void copy(FileLocation sourceFileLocation, FileLocation targetFileLocation)
      throws IOException {
    Path sourcePath = sourceFileLocation.resolve();
    Path targetPath = targetFileLocation.resolve();
    Path parentTargetPath = targetPath.getParent();
    Files.createDirectories(parentTargetPath);
    Path tempPath = Files.createTempFile(parentTargetPath, "device-file-", ".tmp");
    Files.copy(sourcePath, tempPath, StandardCopyOption.REPLACE_EXISTING);
    Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    Path currentDirectory = sourcePath.getParent();
    remove(sourceFileLocation.getBasePath(), currentDirectory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void remove(FileLocation fileLocation) throws IOException {
    remove(fileLocation.getBasePath(), fileLocation.resolve());
  }
  
  protected void remove(Path basePath, Path currentPath) throws IOException {
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
      Files.deleteIfExists(currentPath);
      remove(basePath, currentPath.getParent());
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void link(FileLocation fileLocation, FileLocation linkLocation) throws IOException {
    Path target = fileLocation.resolve();
    Path link = linkLocation.resolve();
    Path parent = link.getParent();
    Files.createDirectories(parent);
    Path relativeTarget = parent.relativize(target);
    Files.createSymbolicLink(link, relativeTarget);
  }
}
