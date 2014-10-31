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

package uk.co.unclealex.music.sync;

import java.io.IOException;
import java.nio.file.Path;

import javax.inject.Inject;

import uk.co.unclealex.music.configuration.FileSystemDevice;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.devices.DeviceService;
import uk.co.unclealex.music.files.DirectoryService;
import uk.co.unclealex.music.files.FileUtils;
import uk.co.unclealex.music.message.MessageService;

import com.google.inject.assistedinject.Assisted;

/**
 * The {@link Synchroniser} for {@link FileSystemDevice}s.
 * 
 * @author alex
 * 
 */
public class FileSystemSynchroniser extends AbstractFileSystemSynchroniser<FileSystemDevice> {

  @Inject
  public FileSystemSynchroniser(
      final MessageService messageService,
      final DirectoryService directoryService,
      final DeviceService deviceService,
      final DeviceConnectionService deviceConnectionService,
      final FileUtils fileUtils,
      @Assisted final User owner,
      @Assisted final FileSystemDevice device) {
    super(messageService, directoryService, deviceService, deviceConnectionService, fileUtils, owner, device);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Path calculateBasePath(final Path mountPath) throws IOException {
    final Path relativeMusicPath = getDevice().getRelativeMusicPath();
    return relativeMusicPath == null ? mountPath : mountPath.resolve(relativeMusicPath);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void beforeMount() throws IOException {
    // No extra logic needed.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void beforeUnmount() throws IOException {
    // No extra logic needed.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void afterUnmount() throws IOException {
    // No extra logic needed.
  }

}
