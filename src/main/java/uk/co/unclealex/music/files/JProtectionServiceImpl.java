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

package uk.co.unclealex.music.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of {@link JProtectionService}.
 * @author alex
 *
 */
public class JProtectionServiceImpl implements JProtectionService {

  private static final Logger log = LoggerFactory.getLogger(JProtectionService.class);
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void protect(JFileLocation fileLocation) throws IOException {
    if (fileLocation.isReadOnly()) {
      alterWriteable(fileLocation, false);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unprotect(JFileLocation fileLocation) throws IOException {
    alterWriteable(fileLocation, true);
  }

  protected void alterWriteable(JFileLocation fileLocation, boolean allowWrites) throws IOException {
    Path currentPath = fileLocation.resolve();
    Path terminatingPath = fileLocation.getBasePath().getParent();
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


}
