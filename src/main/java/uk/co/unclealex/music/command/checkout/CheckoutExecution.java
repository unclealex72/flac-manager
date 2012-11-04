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

package uk.co.unclealex.music.command.checkout;

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
 * @author alex
 * 
 */
public class CheckoutExecution implements Execution {

  /**
   * The initial {@link Actions} class.
   */
  private final Actions actions;

  /**
   * The {@link FileLocationFactory} used to create {@link FileLocation}s.
   */
  private final FileLocationFactory fileLocationFactory;

  /**
   * The {@link FilenameService} used to calculate filenames from
   * {@link MusicFile}s.
   */
  private final FilenameService filenameService;

  @Inject
  public CheckoutExecution(Actions actions, FileLocationFactory fileLocationFactory, FilenameService filenameService) {
    super();
    this.actions = actions;
    this.fileLocationFactory = fileLocationFactory;
    this.filenameService = filenameService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Actions execute(Actions actions, FileLocation flacFileLocation, MusicFile musicFile) throws IOException {
    FilenameService filenameService = getFilenameService();
    FileLocationFactory fileLocationFactory = getFileLocationFactory();
    FileLocation encodedFileLocation =
        fileLocationFactory.createEncodedFileLocation(filenameService.toPath(musicFile, Extension.MP3));
    FileLocation targetFileLocation =
        fileLocationFactory.createStagingFileLocation(filenameService.toPath(musicFile, Extension.FLAC));
    return getActions()
        .unprotect(flacFileLocation)
        .move(flacFileLocation, targetFileLocation)
        .protect(flacFileLocation)
        .unprotect(encodedFileLocation)
        .delete(encodedFileLocation)
        .protect(encodedFileLocation);
  }

  public Actions getActions() {
    return actions;
  }

  public FileLocationFactory getFileLocationFactory() {
    return fileLocationFactory;
  }

  public FilenameService getFilenameService() {
    return filenameService;
  }

}
