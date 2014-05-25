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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import uk.co.unclealex.music.configuration.JDevice;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.devices.JDeviceService;
import uk.co.unclealex.music.files.JDirectoryService;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.files.JFileUtils;
import uk.co.unclealex.music.message.JMessageService;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * The {@link JSynchroniser} for devices whose music files are in directories
 * that can be accessed as standard files.
 * 
 * @author alex
 * 
 */
public abstract class JAbstractFileSystemSynchroniser<D extends JDevice> extends JAbstractSynchroniser<D> {

  /**
   * The {@link uk.co.unclealex.music.files.JFileUtils} used to add and remove files.
   */
  private final JFileUtils fileUtils;

  /**
   * The path where music files can be found on the device.
   */
  private Path baseMusicPath;

  /**
   * Instantiates a new file system synchroniser.
   * 
   * @param messageService
   *          the message service
   * @param directoryService
   *          the directory service
   * @param deviceService
   *          the device service
   * @param processRequestBuilder
   *          the process request builder
   * @param fileUtils
   *          the file utils
   * @param owner
   *          the owner
   * @param device
   *          the device
   */
  public JAbstractFileSystemSynchroniser(
          final JMessageService messageService,
          final JDirectoryService directoryService,
          final JDeviceService deviceService,
          final JDeviceConnectionService deviceConnectionService,
          final JFileUtils fileUtils,
          final JUser owner,
          final D device) {
    super(messageService, directoryService, deviceService, deviceConnectionService, owner, device);
    this.fileUtils = fileUtils;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void afterMount(final Path mountPath) throws IOException {
    setBaseMusicPath(calculateBasePath(mountPath));
  }

  /**
   * Calculate, given a mount point, where music files are stored on the device.
   * 
   * @param mountPath
   *          The path where the device is mounted.
   * @return The path where music files can be found.
   * @throws IOException
   */
  protected abstract Path calculateBasePath(Path mountPath) throws IOException;

  /**
   * {@inheritDoc}
   */
  @Override
  protected Set<JDeviceFile> listDeviceFiles() throws IOException {
    final Function<JFileLocation, JDeviceFile> deviceFileFunction = new Function<JFileLocation, JDeviceFile>() {
      @Override
      public JDeviceFile apply(final JFileLocation fileLocation) {
        try {
          return new JDeviceFile(null, fileLocation.getRelativePath().toString(), Files.getLastModifiedTime(
              fileLocation.resolve()).toMillis());
        }
        catch (final IOException e) {
          throw new IllegalStateException("Cannot read the last modified time of " + fileLocation.resolve(), e);
        }
      }
    };
    return Sets
        .newHashSet(Iterables.transform(getDirectoryService().listFiles(getBaseMusicPath()), deviceFileFunction));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void remove(final JDeviceFile deviceFile) throws IOException {
    getFileUtils().remove(toFileLocation(Paths.get(deviceFile.getRelativePath())));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void add(final JFileLocation fileLocation) throws IOException {
    getFileUtils().copy(fileLocation, toFileLocation(fileLocation.getRelativePath()));
  }

  protected JFileLocation toFileLocation(final Path relativePath) {
    return new JFileLocation(getBaseMusicPath(), relativePath, false);
  }

  /**
   * Gets the {@link uk.co.unclealex.music.files.JFileUtils} used to add and remove files.
   * 
   * @return the {@link uk.co.unclealex.music.files.JFileUtils} used to add and remove files
   */
  public JFileUtils getFileUtils() {
    return fileUtils;
  }

  public Path getBaseMusicPath() {
    return baseMusicPath;
  }

  public void setBaseMusicPath(final Path baseMusicPath) {
    this.baseMusicPath = baseMusicPath;
  }

}
