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

package uk.co.unclealex.music.sync.drive;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;

import javax.annotation.PostConstruct;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @author alex
 * 
 */
public class DriveUuidServiceImpl implements DriveUuidService {

  /**
   * A map of drive paths by their UUID.
   */
  private final BiMap<String, Path> drivesByUuid = HashBiMap.create();

  @PostConstruct
  protected void initialise() throws IOException {
    final BiMap<String, Path> drivesByUuid = getDrivesByUuid();
    final FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        final String uuid = file.getFileName().toString();
        final Path path = file.toFile().getCanonicalFile().toPath();
        drivesByUuid.put(uuid, path);
        return FileVisitResult.CONTINUE;
      }
    };
    Files.walkFileTree(getBasePath(), new HashSet<FileVisitOption>(), 1, visitor);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BiMap<String, Path> listDrivesByUuid() {
    return getDrivesByUuid();
  }

  protected Path getBasePath() {
    return Paths.get("/dev", "disk", "by-uuid");
  }

  public BiMap<String, Path> getDrivesByUuid() {
    return drivesByUuid;
  }
}
