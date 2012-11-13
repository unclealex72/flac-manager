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

import com.google.inject.Inject;

/**
 * An implementation of {@link FileUtils} that decorates another {@link FileUtils} and is aware of whether {@link 
 * FileLocation}s should be left in a read only or writable state.
 * @author alex
 *
 */
public class ProtectionAwareFileUtils implements FileUtils {

  /**
   * The {@link ProtectionService} used to protect and unprotect {@link FileLocation}s.
   */
  private final ProtectionService protectionService;
  
  /**
   * The wrapped {@link FileUtils}.
   */
  private final FileUtils fileUtils;
  
  /**
   * Instantiates a new protection aware file utils.
   *
   * @param fileUtils the file utils
   * @param protectionService the protection service
   */
  @Inject
  public ProtectionAwareFileUtils(FileUtils fileUtils, ProtectionService protectionService) {
    super();
    this.fileUtils = fileUtils;
    this.protectionService = protectionService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void move(FileLocation sourceFileLocation, FileLocation targetFileLocation) throws IOException {
    unprotect(sourceFileLocation, targetFileLocation);
    try {
      getFileUtils().move(sourceFileLocation, targetFileLocation);
    }
    finally {
      protect(sourceFileLocation, targetFileLocation);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void remove(FileLocation fileLocation) throws IOException {
    unprotect(fileLocation);
    try {
      getFileUtils().remove(fileLocation);
    }
    finally {
      protect(fileLocation);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void link(FileLocation fileLocation, FileLocation linkLocation) throws IOException {
    unprotect(fileLocation, linkLocation);
    try {
      getFileUtils().link(fileLocation, linkLocation);
    }
    finally {
      protect(fileLocation, linkLocation);
    }
  }

  /**
   * Protect.
   *
   * @param fileLocations the file locations
   */
  public void protect(FileLocation... fileLocations) throws IOException {
    ProtectionService protectionService = getProtectionService();
    for (FileLocation fileLocation : fileLocations) {
      protectionService.protect(fileLocation);
    }
  }

  /**
   * Unprotect.
   *
   * @param fileLocations the file locations
   * @throws IOException 
   */
  public void unprotect(FileLocation... fileLocations) throws IOException {
    ProtectionService protectionService = getProtectionService();
    for (FileLocation fileLocation : fileLocations) {
      protectionService.unprotect(fileLocation);
    }
  }

  /**
   * Gets the {@link ProtectionService} used to protect and unprotect {@link FileLocation}s.
   *
   * @return the {@link ProtectionService} used to protect and unprotect {@link FileLocation}s
   */
  public ProtectionService getProtectionService() {
    return protectionService;
  }
  
  /**
   * Gets the wrapped {@link FileUtils}.
   *
   * @return the wrapped {@link FileUtils}
   */
  public FileUtils getFileUtils() {
    return fileUtils;
  }
}
