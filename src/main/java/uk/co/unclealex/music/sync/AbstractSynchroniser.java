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
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.devices.DeviceService;
import uk.co.unclealex.music.files.DirectoryService;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.message.MessageService;
import uk.co.unclealex.process.builder.ProcessRequestBuilder;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The base class for {@link Synchroniser}s that looks after deciding which
 * files need to be added, kept and removed but delegates the actual adding and
 * removing to its subclasses.
 * 
 * @author alex
 * 
 */
public abstract class AbstractSynchroniser<D extends Device> implements Synchroniser {

  /**
   * The number of milliseconds in an hour.
   */
  private static final long MILLISECONDS_IN_HOUR = 60 * 60 * 1000;

  /**
   * The logger for this class.
   */
  private final Logger log = LoggerFactory.getLogger(AbstractSynchroniser.class);

  /**
   * The {@link MessageService} used to relay messages to the user.
   */
  private final MessageService messageService;

  /**
   * The {@link DirectoryService} used to search for MP3 files.
   */
  private final DirectoryService directoryService;

  /**
   * The {@link DeviceService} used to find the device's repository.
   */
  private final DeviceService deviceService;

  /**
   * The {@link ProcessRequestBuilder} used to run native commands.
   */
  private final ProcessRequestBuilder processRequestBuilder;

  /**
   * The owner of the device.
   */
  private final User owner;

  /**
   * The device being synchronised.
   */
  private final D device;

  public AbstractSynchroniser(
      MessageService messageService,
      DirectoryService directoryService,
      DeviceService deviceService,
      ProcessRequestBuilder processRequestBuilder,
      User owner,
      D device) {
    super();
    this.messageService = messageService;
    this.directoryService = directoryService;
    this.deviceService = deviceService;
    this.processRequestBuilder = processRequestBuilder;
    this.owner = owner;
    this.device = device;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void synchronise() throws IOException {
    initialise();
    MessageService messageService = getMessageService();
    try {
      Path deviceRepositoryBase = getDeviceService().getDeviceRepositoryBase(getOwner());
      Set<DeviceFile> deviceFiles = listDeviceFiles();
      SortedSet<FileLocation> fileLocations = getDirectoryService().listFiles(deviceRepositoryBase);
      Function<DeviceFile, String> deviceRelativePathFunction = new Function<DeviceFile, String>() {
        public String apply(DeviceFile deviceFile) {
          return deviceFile.getRelativePath();
        }
      };
      Map<String, DeviceFile> deviceFilesByRelativePath =
          Maps.newHashMap(Maps.uniqueIndex(deviceFiles, deviceRelativePathFunction));
      Function<FileLocation, String> fileLocationRelativePathFunction = new Function<FileLocation, String>() {
        public String apply(FileLocation fileLocation) {
          return fileLocation.getRelativePath().toString();
        }
      };
      Map<String, FileLocation> fileLocationsByRelativePath =
          Maps.uniqueIndex(fileLocations, fileLocationRelativePathFunction);
      SortedSet<DeviceFile> deviceFilesToRemove = Sets.newTreeSet();
      SortedSet<FileLocation> localFilesToAdd = Sets.newTreeSet();
      for (Map.Entry<String, FileLocation> entry : fileLocationsByRelativePath.entrySet()) {
        String relativePath = entry.getKey();
        FileLocation fileLocation = entry.getValue();
        DeviceFile deviceFile = deviceFilesByRelativePath.get(relativePath);
        if (deviceFile == null
            || laterThan(Files.getLastModifiedTime(fileLocation.resolve()).toMillis(), deviceFile.getLastModified())) {
          if (deviceFile != null) {
            deviceFilesToRemove.add(deviceFile);
            deviceFilesByRelativePath.remove(relativePath);
          }
          localFilesToAdd.add(fileLocation);
        }
        else if (deviceFile != null) {
          messageService.printMessage(MessageService.SYNC_KEEP, deviceFile.getRelativePath());
          deviceFilesByRelativePath.remove(relativePath);
        }
      }
      deviceFilesToRemove.addAll(deviceFilesByRelativePath.values());
      for (DeviceFile deviceFile : deviceFilesToRemove) {
        messageService.printMessage(MessageService.SYNC_REMOVE, deviceFile.getRelativePath());
        remove(deviceFile);
      }
      for (FileLocation fileLocation : localFilesToAdd) {
        messageService.printMessage(MessageService.SYNC_ADD, fileLocation.getRelativePath());
        add(fileLocation);
      }
    }
    catch (RuntimeException e) {
      log.error("There was an unexpected error trying to synchronise " + getDevice(), e);
    }
    finally {
      closeDevice();
    }
    getProcessRequestBuilder()
        .forCommand("pumount")
        .withArguments(getDevice().getMountPoint().toString())
        .executeAndWait();
  }

  /**
   * Initialise the device so that it is ready to synchronise.
   * 
   * @throws IOException
   */
  protected abstract void initialise() throws IOException;

  /**
   * List all the files currently on the device.
   * 
   * @return A set of {@link DeviceFile}s that represent the files currently on
   *         the device.
   * @throws IOException
   */
  protected abstract Set<DeviceFile> listDeviceFiles() throws IOException;

  /**
   * Remove a file from the device.
   * 
   * @param deviceFile
   *          The device file to remove.
   * @throws IOException
   */
  protected abstract void remove(DeviceFile deviceFile) throws IOException;

  /**
   * Add a new file to the device.
   * 
   * @param fileLocation
   *          The location of the file to add.
   * @throws IOException
   */
  protected abstract void add(FileLocation fileLocation) throws IOException;

  protected abstract void closeDevice() throws IOException;

  /**
   * Test if the lhs is later than the rhs but also that there is not exactly an
   * hour's difference.
   * 
   * @param lhs
   *          the lhs
   * @param rhs
   *          the rhs
   * @return true, if successful
   */
  protected boolean laterThan(long lhs, long rhs) {
    if (Math.abs(lhs - rhs) == MILLISECONDS_IN_HOUR) {
      return false;
    }
    else {
      return lhs > rhs;
    }
  }

  public User getOwner() {
    return owner;
  }

  public D getDevice() {
    return device;
  }

  public MessageService getMessageService() {
    return messageService;
  }

  public DirectoryService getDirectoryService() {
    return directoryService;
  }

  public DeviceService getDeviceService() {
    return deviceService;
  }

  public ProcessRequestBuilder getProcessRequestBuilder() {
    return processRequestBuilder;
  }

}
