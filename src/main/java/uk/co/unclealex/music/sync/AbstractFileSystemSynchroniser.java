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

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.devices.DeviceService;
import uk.co.unclealex.music.files.DirectoryService;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.files.FileUtils;
import uk.co.unclealex.music.message.MessageService;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * The {@link Synchroniser} for devices whose music files are in directories
 * that can be accessed as standard files.
 * 
 * @author alex
 * 
 */
public abstract class AbstractFileSystemSynchroniser<D extends Device> extends AbstractSynchroniser<D> {

  /**
   * The {@link FileUtils} used to add and remove files.
   */
  private final FileUtils fileUtils;

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
  public AbstractFileSystemSynchroniser(
      final MessageService messageService,
      final DirectoryService directoryService,
      final DeviceService deviceService,
      final DeviceConnectionService deviceConnectionService,
      final FileUtils fileUtils,
      final User owner,
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
  protected Set<DeviceFile> listDeviceFiles() throws IOException {
    final Function<FileLocation, DeviceFile> deviceFileFunction = new Function<FileLocation, DeviceFile>() {
      @Override
      public DeviceFile apply(final FileLocation fileLocation) {
        try {
          return new DeviceFile(null, fileLocation.getRelativePath().toString(), Files.getLastModifiedTime(
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
  protected void remove(final DeviceFile deviceFile) throws IOException {
    getFileUtils().remove(toFileLocation(Paths.get(deviceFile.getRelativePath())));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void add(final FileLocation fileLocation) throws IOException {
    getFileUtils().copy(fileLocation, toFileLocation(fileLocation.getRelativePath()));
  }

  protected FileLocation toFileLocation(final Path relativePath) {
    return new FileLocation(getBaseMusicPath(), relativePath, false);
  }

  /**
   * Gets the {@link FileUtils} used to add and remove files.
   * 
   * @return the {@link FileUtils} used to add and remove files
   */
  public FileUtils getFileUtils() {
    return fileUtils;
  }

  public Path getBaseMusicPath() {
    return baseMusicPath;
  }

  public void setBaseMusicPath(final Path baseMusicPath) {
    this.baseMusicPath = baseMusicPath;
  }

}
