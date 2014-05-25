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
import java.util.Set;

import javax.inject.Inject;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.command.JCheckinCommandLine;
import uk.co.unclealex.music.command.JExecution;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.files.JExtension;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.files.JFileLocationFactory;
import uk.co.unclealex.music.files.JFilenameService;
import uk.co.unclealex.music.musicbrainz.JOwnerService;

/**
 * Generate {@link uk.co.unclealex.music.action.JActions} for checking in a FLAC file.
 * 
 * @author alex
 * 
 */
public class JCheckinExecution implements JExecution<JCheckinCommandLine> {

  /**
   * The {@link uk.co.unclealex.music.files.JFilenameService} used to calculate file names from
   * {@link uk.co.unclealex.music.JMusicFile}s.
   */
  private final JFilenameService filenameService;

  /**
   * The {@link uk.co.unclealex.music.files.JFileLocationFactory} used to generate {@link uk.co.unclealex.music.files.JFileLocation}s.
   */
  private final JFileLocationFactory fileLocationFactory;

  /**
   * The {@link uk.co.unclealex.music.musicbrainz.JOwnerService} that is used to find out who owns which files.
   */
  private final JOwnerService ownerService;

  /**
   * Instantiates a new checkin execution.
   * 
   * @param filenameService
   *          the filename service
   * @param fileLocationFactory
   *          the file location factory
   * @param ownerService
   *          the owner service
   */
  @Inject
  public JCheckinExecution(
          JFilenameService filenameService,
          JFileLocationFactory fileLocationFactory,
          JOwnerService ownerService) {
    super();
    this.filenameService = filenameService;
    this.fileLocationFactory = fileLocationFactory;
    this.ownerService = ownerService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions initialise(JActions actions, JCheckinCommandLine commandLine) {
    return actions;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public JActions execute(
      JActions actions,
      JFileLocation flacFileLocation,
      JMusicFile musicFile) throws IOException {
    JFilenameService filenameService = getFilenameService();
    JFileLocationFactory fileLocationFactory = getFileLocationFactory();
    if (musicFile.getCoverArt() == null) {
      actions = actions.coverArt(flacFileLocation);
    }
    JFileLocation encodedFileLocation =
        fileLocationFactory.createEncodedFileLocation(filenameService.toPath(musicFile, JExtension.MP3));
    JFileLocation newFlacFileLocation =
        fileLocationFactory.createFlacFileLocation(filenameService.toPath(musicFile, JExtension.FLAC));
    Set<JUser> owners = getOwnerService().getOwnersForMusicFile(musicFile);
    return actions
        .encode(flacFileLocation, encodedFileLocation, musicFile)
        .link(encodedFileLocation, owners)
        .move(flacFileLocation, newFlacFileLocation);
  }

  /**
   * Gets the {@link uk.co.unclealex.music.files.JFilenameService} used to calculate file names from
   * {@link uk.co.unclealex.music.JMusicFile}s.
   * 
   * @return the {@link uk.co.unclealex.music.files.JFilenameService} used to calculate file names from
   *         {@link uk.co.unclealex.music.JMusicFile}s
   */
  public JFilenameService getFilenameService() {
    return filenameService;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.files.JFileLocationFactory} used to generate {@link uk.co.unclealex.music.files.JFileLocation}
   * s.
   * 
   * @return the {@link uk.co.unclealex.music.files.JFileLocationFactory} used to generate
   *         {@link uk.co.unclealex.music.files.JFileLocation}s
   */
  public JFileLocationFactory getFileLocationFactory() {
    return fileLocationFactory;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.musicbrainz.JOwnerService} that is used to find out who owns which
   * files.
   * 
   * @return the {@link uk.co.unclealex.music.musicbrainz.JOwnerService} that is used to find out who owns which
   *         files
   */
  public JOwnerService getOwnerService() {
    return ownerService;
  }
}
