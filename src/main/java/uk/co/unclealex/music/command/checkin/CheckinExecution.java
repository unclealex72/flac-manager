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

package uk.co.unclealex.music.command.checkin;

import java.io.IOException;

import javax.inject.Inject;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.command.Execution;
import uk.co.unclealex.music.files.Extension;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.files.FileLocationFactory;
import uk.co.unclealex.music.files.FilenameService;

/**
 * Generate {@link Actions} for checking in a FLAC file.
 * @author alex
 * 
 */
public class CheckinExecution implements Execution {

  /**
   * The {@link FilenameService} used to calculate file names from {@link MusicFile}s.
   */
  private final FilenameService filenameService;

  /**
   * The {@link FileLocationFactory} used to generate {@link FileLocation}s.
   */
  private final FileLocationFactory fileLocationFactory;

  /**
   * Instantiates a new checkin execution.
   *
   * @param actions the actions
   * @param filenameService the filename service
   * @param fileLocationFactory the file location factory
   * @param artworkSearchingService the artwork searching service
   */
  @Inject
  public CheckinExecution(
      FilenameService filenameService,
      FileLocationFactory fileLocationFactory) {
    super();
    this.filenameService = filenameService;
    this.fileLocationFactory = fileLocationFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Actions execute(Actions actions, FileLocation flacFileLocation, MusicFile musicFile) throws IOException {
    FilenameService filenameService = getFilenameService();
    FileLocationFactory fileLocationFactory = getFileLocationFactory();
    if (musicFile.getCoverArt() == null) {
      actions = actions.coverArt(flacFileLocation);
    }
    FileLocation encodedFileLocation =
        fileLocationFactory.createEncodedFileLocation(filenameService.toPath(musicFile, Extension.MP3));
    FileLocation newFlacFileLocation =
        fileLocationFactory.createFlacFileLocation(filenameService.toPath(musicFile, Extension.FLAC));
    return actions
        .unprotect(encodedFileLocation)
        .encode(flacFileLocation, encodedFileLocation, musicFile)
        .protect(encodedFileLocation)
        .unprotect(newFlacFileLocation)
        .move(flacFileLocation, newFlacFileLocation)
        .protect(newFlacFileLocation);
  }

  /**
   * Gets the {@link FilenameService} used to calculate file names from {@link MusicFile}s.
   *
   * @return the {@link FilenameService} used to calculate file names from {@link MusicFile}s
   */
  public FilenameService getFilenameService() {
    return filenameService;
  }

  /**
   * Gets the {@link FileLocationFactory} used to generate {@link FileLocation}s.
   *
   * @return the {@link FileLocationFactory} used to generate {@link FileLocation}s
   */
  public FileLocationFactory getFileLocationFactory() {
    return fileLocationFactory;
  }
}
