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

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.command.CheckoutCommandLine;
import uk.co.unclealex.music.command.Execution;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.devices.DeviceService;
import uk.co.unclealex.music.files.Extension;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.files.FileLocationFactory;
import uk.co.unclealex.music.files.FilenameService;
import uk.co.unclealex.music.musicbrainz.OwnerService;

/**
 * @author alex
 * 
 */
public class CheckoutExecution implements Execution<CheckoutCommandLine> {

  /**
   * The {@link FileLocationFactory} used to create {@link FileLocation}s.
   */
  private final FileLocationFactory fileLocationFactory;

  /**
   * The {@link FilenameService} used to calculate filenames from
   * {@link MusicFile}s.
   */
  private final FilenameService filenameService;

  /**
   * The {@link DeviceService} used to create device file paths.
   */
  private final DeviceService deviceService;

  /**
   * The {@link OwnerService} used to keep track of file ownership.
   */
  private final OwnerService ownerService;

  @Inject
  public CheckoutExecution(
      FileLocationFactory fileLocationFactory,
      FilenameService filenameService,
      DeviceService deviceService,
      OwnerService ownerService) {
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
  public Actions execute(
      CheckoutCommandLine checkoutCommandLine,
      Actions actions,
      FileLocation flacFileLocation,
      MusicFile musicFile) throws IOException {
    FilenameService filenameService = getFilenameService();
    FileLocationFactory fileLocationFactory = getFileLocationFactory();
    FileLocation encodedFileLocation =
        fileLocationFactory.createEncodedFileLocation(filenameService.toPath(musicFile, Extension.MP3));
    FileLocation targetFileLocation =
        fileLocationFactory.createStagingFileLocation(filenameService.toPath(musicFile, Extension.FLAC));
    Set<User> owners = getOwnerService().getOwnersForMusicFile(musicFile);
    return actions
        .move(flacFileLocation, targetFileLocation)
        .delete(encodedFileLocation)
        .unlink(encodedFileLocation, owners);
  }

  public FileLocationFactory getFileLocationFactory() {
    return fileLocationFactory;
  }

  public FilenameService getFilenameService() {
    return filenameService;
  }

  public DeviceService getDeviceService() {
    return deviceService;
  }

  public OwnerService getOwnerService() {
    return ownerService;
  }

}
