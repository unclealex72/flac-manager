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
import java.util.Set;

import javax.inject.Inject;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.command.JCheckoutCommandLine;
import uk.co.unclealex.music.command.JExecution;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.devices.JDeviceService;
import uk.co.unclealex.music.files.JExtension;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.files.JFileLocationFactory;
import uk.co.unclealex.music.files.JFilenameService;
import uk.co.unclealex.music.musicbrainz.JOwnerService;

/**
 * @author alex
 * 
 */
public class JCheckoutExecution implements JExecution<JCheckoutCommandLine> {

  /**
   * The {@link uk.co.unclealex.music.files.JFileLocationFactory} used to create {@link uk.co.unclealex.music.files.JFileLocation}s.
   */
  private final JFileLocationFactory fileLocationFactory;

  /**
   * The {@link uk.co.unclealex.music.files.JFilenameService} used to calculate filenames from
   * {@link uk.co.unclealex.music.JMusicFile}s.
   */
  private final JFilenameService filenameService;

  /**
   * The {@link uk.co.unclealex.music.devices.JDeviceService} used to create device file paths.
   */
  private final JDeviceService deviceService;

  /**
   * The {@link uk.co.unclealex.music.musicbrainz.JOwnerService} used to keep track of file ownership.
   */
  private final JOwnerService ownerService;

  @Inject
  public JCheckoutExecution(
          JFileLocationFactory fileLocationFactory,
          JFilenameService filenameService,
          JDeviceService deviceService,
          JOwnerService ownerService) {
    super();
    this.fileLocationFactory = fileLocationFactory;
    this.filenameService = filenameService;
    this.deviceService = deviceService;
    this.ownerService = ownerService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions initialise(JActions actions, JCheckoutCommandLine commandLine) {
    return actions;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions execute(JActions actions, JFileLocation flacFileLocation, JMusicFile musicFile) throws IOException {
    JFilenameService filenameService = getFilenameService();
    JFileLocationFactory fileLocationFactory = getFileLocationFactory();
    JFileLocation encodedFileLocation =
        fileLocationFactory.createEncodedFileLocation(filenameService.toPath(musicFile, JExtension.MP3));
    JFileLocation targetFileLocation =
        fileLocationFactory.createStagingFileLocation(filenameService.toPath(musicFile, JExtension.FLAC));
    Set<JUser> owners = getOwnerService().getOwnersForMusicFile(musicFile);
    return actions
        .move(flacFileLocation, targetFileLocation)
        .unlink(encodedFileLocation, owners)
        .delete(encodedFileLocation);
  }

  public JFileLocationFactory getFileLocationFactory() {
    return fileLocationFactory;
  }

  public JFilenameService getFilenameService() {
    return filenameService;
  }

  public JDeviceService getDeviceService() {
    return deviceService;
  }

  public JOwnerService getOwnerService() {
    return ownerService;
  }

}
