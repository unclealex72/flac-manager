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

package uk.co.unclealex.music.checkout;

import java.io.IOException;
import java.nio.file.Path;
import java.util.SortedSet;

import javax.inject.Inject;

import uk.co.unclealex.music.common.command.Execution;
import uk.co.unclealex.music.common.configuration.Configuration;
import uk.co.unclealex.music.common.configuration.Directories;
import uk.co.unclealex.music.common.exception.InvalidDirectoriesException;
import uk.co.unclealex.music.common.files.FileUtils;
import uk.co.unclealex.music.common.files.FlacDirectoryService;

/**
 * An interface for classes that contain the checkout logic.
 * 
 * @author alex
 * 
 */
public class CheckoutExecution implements Execution {

  /**
   * The {@link FlacDirectoryService} used to find FLAC files.
   */
  private final FlacDirectoryService flacDirectoryService;
  
  /**
   * The {@link Configuration} for this command.
   */
  private final Configuration configuration;
  
  /**
   * The {@link FileUtils} used to manipulate files.
   */
  private final FileUtils fileUtils;

  
  /**
   * Instantiates a new checkout execution.
   *
   * @param flacDirectoryService the flac directory service
   * @param configuration the configuration
   * @param fileUtils the file utils
   */
  @Inject
  public CheckoutExecution(FlacDirectoryService flacDirectoryService, Configuration configuration, FileUtils fileUtils) {
    super();
    this.flacDirectoryService = flacDirectoryService;
    this.configuration = configuration;
    this.fileUtils = fileUtils;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(Iterable<Path> paths) throws IOException, InvalidDirectoriesException {
    Directories directories = getConfiguration().getDirectories();
    Path flacPath = directories.getFlacPath();
    SortedSet<Path> flacFiles = getFlacDirectoryService().listFlacFiles(flacPath, paths);
    FileUtils fileUtils = getFileUtils();
    Path stagingPath = directories.getStagingPath();
    for (Path flacFile : flacFiles) {
      Path relativeFlacFile = flacPath.relativize(flacFile);
      fileUtils.alterWriteable(flacPath, relativeFlacFile, true);
      fileUtils.move(flacPath, relativeFlacFile, stagingPath, relativeFlacFile);
      fileUtils.alterWriteable(flacPath, relativeFlacFile.getParent(), false);
    }
  }
  
  /**
   * Gets the {@link FlacDirectoryService} used to find FLAC files.
   *
   * @return the {@link FlacDirectoryService} used to find FLAC files
   */
  public FlacDirectoryService getFlacDirectoryService() {
    return flacDirectoryService;
  }

  /**
   * Gets the {@link Configuration} for this command.
   *
   * @return the {@link Configuration} for this command
   */
  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Gets the {@link FileUtils} used to manipulate files.
   *
   * @return the {@link FileUtils} used to manipulate files
   */
  public FileUtils getFileUtils() {
    return fileUtils;
  }
  
  
}
