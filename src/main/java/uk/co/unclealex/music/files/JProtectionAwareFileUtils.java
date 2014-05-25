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
 * An implementation of {@link JFileUtils} that decorates another {@link JFileUtils} and is aware of whether {@link
 * JFileLocation}s should be left in a read only or writable state.
 * @author alex
 *
 */
public class JProtectionAwareFileUtils implements JFileUtils {

  /**
   * The {@link JProtectionService} used to protect and unprotect {@link JFileLocation}s.
   */
  private final JProtectionService protectionService;
  
  /**
   * The wrapped {@link JFileUtils}.
   */
  private final JFileUtils fileUtils;
  
  /**
   * Instantiates a new protection aware file utils.
   *
   * @param fileUtils the file utils
   * @param protectionService the protection service
   */
  @Inject
  public JProtectionAwareFileUtils(JFileUtils fileUtils, JProtectionService protectionService) {
    super();
    this.fileUtils = fileUtils;
    this.protectionService = protectionService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void move(JFileLocation sourceFileLocation, JFileLocation targetFileLocation) throws IOException {
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
  public void copy(JFileLocation sourceFileLocation, JFileLocation targetFileLocation) throws IOException {
    unprotect(targetFileLocation);
    try {
      getFileUtils().copy(sourceFileLocation, targetFileLocation);
    }
    finally {
      protect(targetFileLocation);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void remove(JFileLocation fileLocation) throws IOException {
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
  public void link(JFileLocation fileLocation, JFileLocation linkLocation) throws IOException {
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
  public void protect(JFileLocation... fileLocations) throws IOException {
    JProtectionService protectionService = getProtectionService();
    for (JFileLocation fileLocation : fileLocations) {
      protectionService.protect(fileLocation);
    }
  }

  /**
   * Unprotect.
   *
   * @param fileLocations the file locations
   * @throws IOException 
   */
  public void unprotect(JFileLocation... fileLocations) throws IOException {
    JProtectionService protectionService = getProtectionService();
    for (JFileLocation fileLocation : fileLocations) {
      protectionService.unprotect(fileLocation);
    }
  }

  /**
   * Gets the {@link JProtectionService} used to protect and unprotect {@link JFileLocation}s.
   *
   * @return the {@link JProtectionService} used to protect and unprotect {@link JFileLocation}s
   */
  public JProtectionService getProtectionService() {
    return protectionService;
  }
  
  /**
   * Gets the wrapped {@link JFileUtils}.
   *
   * @return the wrapped {@link JFileUtils}
   */
  public JFileUtils getFileUtils() {
    return fileUtils;
  }
}
