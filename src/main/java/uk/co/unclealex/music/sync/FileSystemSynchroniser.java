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

import javax.inject.Inject;

import uk.co.unclealex.music.configuration.FileSystemDevice;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.devices.DeviceService;
import uk.co.unclealex.music.files.DirectoryService;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.files.FileUtils;
import uk.co.unclealex.music.message.MessageService;
import uk.co.unclealex.process.builder.ProcessRequestBuilder;
import uk.co.unclealex.process.packages.PackagesRequired;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.assistedinject.Assisted;

/**
 * The {@link Synchroniser} for {@link FileSystemDevice}s.
 * 
 * @author alex
 * 
 */
@PackagesRequired("pmount")
public class FileSystemSynchroniser extends AbstractSynchroniser<FileSystemDevice> {

  /**
   * The {@link FileUtils} used to add and remove files.
   */
  private final FileUtils fileUtils;

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
  @Inject
  public FileSystemSynchroniser(
      MessageService messageService,
      DirectoryService directoryService,
      DeviceService deviceService,
      ProcessRequestBuilder processRequestBuilder,
      FileUtils fileUtils,
      @Assisted User owner,
      @Assisted FileSystemDevice device) {
    super(messageService, directoryService, deviceService, processRequestBuilder, owner, device);
    this.fileUtils = fileUtils;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void initialise() throws IOException {
    // No initialisation needed.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Set<DeviceFile> listDeviceFiles() throws IOException {
    Function<FileLocation, DeviceFile> deviceFileFunction = new Function<FileLocation, DeviceFile>() {
      public DeviceFile apply(FileLocation fileLocation) {
        try {
          return new DeviceFile(null, fileLocation.getRelativePath().toString(), Files.getLastModifiedTime(
              fileLocation.resolve()).toMillis());
        }
        catch (IOException e) {
          throw new IllegalStateException("Cannot read the last modified time of " + fileLocation.resolve(), e);
        }
      }
    };
    return Sets.newHashSet(Iterables
        .transform(getDirectoryService().listFiles(calculateBasePath()), deviceFileFunction));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void remove(DeviceFile deviceFile) throws IOException {
    getFileUtils().remove(toFileLocation(Paths.get(deviceFile.getRelativePath())));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void add(FileLocation fileLocation) throws IOException {
    getFileUtils().copy(fileLocation, toFileLocation(fileLocation.getRelativePath()));
  }

  protected FileLocation toFileLocation(Path relativePath) {
    return new FileLocation(calculateBasePath(), relativePath, false);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void closeDevice() throws IOException {
    // No closing required.
  }

  /**
   * Gets the base path.
   * 
   * @return the base path
   */
  protected Path calculateBasePath() {
    Path mountPoint = getDevice().getMountPoint();
    Path relativeMusicPath = getDevice().getRelativeMusicPath();
    return relativeMusicPath == null ? mountPoint : mountPoint.resolve(relativeMusicPath);
  }

  /**
   * Gets the {@link FileUtils} used to add and remove files.
   * 
   * @return the {@link FileUtils} used to add and remove files
   */
  public FileUtils getFileUtils() {
    return fileUtils;
  }

}
