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

import uk.co.unclealex.music.configuration.JDevice;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.devices.JDeviceService;
import uk.co.unclealex.music.files.JDirectoryService;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.message.JMessageService;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * The base class for {@link JSynchroniser}s that looks after deciding which
 * files need to be added, kept and removed but delegates the actual adding and
 * removing to its subclasses.
 * 
 * @param <D>
 *          The type of the device being synchronised.
 * @author alex
 */
public abstract class JAbstractSynchroniser<D extends JDevice> implements JSynchroniser {

  /**
   * The number of milliseconds in an hour.
   */
  private static final long MILLISECONDS_IN_HOUR = 60 * 60 * 1000;

  /**
   * The logger for this class.
   */
  private final Logger log = LoggerFactory.getLogger(JAbstractSynchroniser.class);

  /**
   * The {@link uk.co.unclealex.music.message.JMessageService} used to relay messages to the user.
   */
  private final JMessageService messageService;

  /**
   * The {@link uk.co.unclealex.music.files.JDirectoryService} used to search for MP3 files.
   */
  private final JDirectoryService directoryService;

  /**
   * The {@link uk.co.unclealex.music.devices.JDeviceService} used to find the device's repository.
   */
  private final JDeviceService deviceService;

  /**
   * The {@link JDeviceConnectionService} used to connect and disconnect devices.
   */
  private final JDeviceConnectionService deviceConnectionService;

  /**
   * The owner of the device.
   */
  private final JUser owner;

  /**
   * The device being synchronised.
   */
  private final D device;

  /**
   * Instantiates a new abstract synchroniser.
   * 
   * @param messageService
   *          the message service
   * @param directoryService
   *          the directory service
   * @param deviceService
   *          the device service
   * @param deviceConnectionService
   *          the device connection service
   * @param owner
   *          the owner
   * @param device
   *          the device
   */
  public JAbstractSynchroniser(
          final JMessageService messageService,
          final JDirectoryService directoryService,
          final JDeviceService deviceService,
          final JDeviceConnectionService deviceConnectionService,
          final JUser owner,
          final D device) {
    super();
    this.messageService = messageService;
    this.directoryService = directoryService;
    this.deviceService = deviceService;
    this.deviceConnectionService = deviceConnectionService;
    this.owner = owner;
    this.device = device;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void synchronise(final Multimap<JUser, JFileLocation> deviceFilesByOwner) throws IOException {
    beforeMount();
    final Path mountPath = findOrMount();
    afterMount(mountPath);
    try {
      final Function<JDeviceFile, String> deviceRelativePathFunction = new Function<JDeviceFile, String>() {
        @Override
        public String apply(final JDeviceFile deviceFile) {
          return deviceFile.getRelativePath();
        }
      };
      final Set<JDeviceFile> deviceFiles = listDeviceFiles();
      final Map<String, JDeviceFile> deviceFilesByRelativePath =
          Maps.newHashMap(Maps.uniqueIndex(deviceFiles, deviceRelativePathFunction));
      final Function<JFileLocation, String> fileLocationRelativePathFunction = new Function<JFileLocation, String>() {
        @Override
        public String apply(final JFileLocation fileLocation) {
          return fileLocation.getRelativePath().toString();
        }
      };
      final Map<String, JFileLocation> fileLocationsByRelativePath =
          Maps.uniqueIndex(deviceFilesByOwner.get(getOwner()), fileLocationRelativePathFunction);
      final SortedSet<JDeviceFile> deviceFilesToRemove = Sets.newTreeSet();
      final SortedSet<JFileLocation> localFilesToAdd = Sets.newTreeSet();
      for (final Map.Entry<String, JFileLocation> entry : fileLocationsByRelativePath.entrySet()) {
        final String relativePath = entry.getKey();
        final JFileLocation fileLocation = entry.getValue();
        final JDeviceFile deviceFile = deviceFilesByRelativePath.get(relativePath);
        if (deviceFile == null
            || laterThan(Files.getLastModifiedTime(fileLocation.resolve()).toMillis(), deviceFile.getLastModified())) {
          if (deviceFile != null) {
            deviceFilesToRemove.add(deviceFile);
            deviceFilesByRelativePath.remove(relativePath);
          }
          localFilesToAdd.add(fileLocation);
        }
        else if (deviceFile != null) {
          printMessage(JMessageService.SYNC_KEEP, deviceFile.getRelativePath());
          deviceFilesByRelativePath.remove(relativePath);
        }
      }
      deviceFilesToRemove.addAll(deviceFilesByRelativePath.values());
      for (final JDeviceFile deviceFile : deviceFilesToRemove) {
        printMessage(JMessageService.SYNC_REMOVE, deviceFile.getRelativePath());
        remove(deviceFile);
      }
      for (final JFileLocation fileLocation : localFilesToAdd) {
        printMessage(JMessageService.SYNC_ADD, fileLocation.getRelativePath());
        add(fileLocation);
      }
      beforeUnmount();
    }
    catch (final RuntimeException e) {
      log.error("There was an unexpected error trying to synchronise " + getDevice(), e);
    }
    finally {
      unmount(mountPath);
      afterUnmount();
    }
  }

  /**
   * Prints the message.
   * 
   * @param template
   *          the template
   * @param parameter
   *          the parameter
   */
  protected void printMessage(final String template, final Object parameter) {
    getMessageService().printMessage(template, parameter, getOwner().getName(), getDevice().getName());
  }

  /**
   * Subclasses need to override this method to contain any device based logic
   * that needs to be executed before the device is mounted.
   * 
   * @throws IOException
   */
  protected abstract void beforeMount() throws IOException;

  protected Path findOrMount() throws IOException {
    return getDeviceConnectionService().mount(getDevice().getUuid());
  }

  /**
   * Subclasses need to override this method to contain any device based logic
   * that needs to be executed after the device is mounted but before any file
   * searching or copying takes place.
   * 
   * @param mountPath
   *          The path where the device has been mounted.
   * @throws IOException
   */
  protected abstract void afterMount(Path mountPath) throws IOException;

  /**
   * List all the files currently on the device.
   * 
   * @return A set of {@link JDeviceFile}s that represent the files currently on
   *         the device.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected abstract Set<JDeviceFile> listDeviceFiles() throws IOException;

  /**
   * Remove a file from the device.
   * 
   * @param deviceFile
   *          The device file to remove.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected abstract void remove(JDeviceFile deviceFile) throws IOException;

  /**
   * Add a new file to the device.
   * 
   * @param fileLocation
   *          The location of the file to add.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected abstract void add(JFileLocation fileLocation) throws IOException;

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
  protected boolean laterThan(final long lhs, final long rhs) {
    if (Math.abs(lhs - rhs) == MILLISECONDS_IN_HOUR) {
      return false;
    }
    else {
      return lhs > rhs;
    }
  }

  /**
   * Subclasses need to override this method to include any device logic that
   * needs to be executed after the device has been synchronised but before the
   * device is unmounted.
   * 
   * @throws IOException
   */
  public abstract void beforeUnmount() throws IOException;

  /**
   * Unmount the device and remove the mount point if it still exists.
   * 
   * @param mountPath
   *          The path where the device is mounted.
   * @throws IOException
   */
  public void unmount(final Path mountPath) throws IOException {
    getDeviceConnectionService().unmount(mountPath);
  }

  /**
   * Subclasses need to extend this method to include any logic that needs to be
   * executed after the device has been unmounted.
   * 
   * @throws IOException
   */
  public abstract void afterUnmount() throws IOException;

  /**
   * Gets the owner of the device.
   * 
   * @return the owner of the device
   */
  public JUser getOwner() {
    return owner;
  }

  /**
   * Gets the device being synchronised.
   * 
   * @return the device being synchronised
   */
  public D getDevice() {
    return device;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.message.JMessageService} used to relay messages to the user.
   * 
   * @return the {@link uk.co.unclealex.music.message.JMessageService} used to relay messages to the user
   */
  public JMessageService getMessageService() {
    return messageService;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.files.JDirectoryService} used to search for MP3 files.
   * 
   * @return the {@link uk.co.unclealex.music.files.JDirectoryService} used to search for MP3 files
   */
  public JDirectoryService getDirectoryService() {
    return directoryService;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.devices.JDeviceService} used to find the device's repository.
   * 
   * @return the {@link uk.co.unclealex.music.devices.JDeviceService} used to find the device's repository
   */
  public JDeviceService getDeviceService() {
    return deviceService;
  }

  public JDeviceConnectionService getDeviceConnectionService() {
    return deviceConnectionService;
  }

}
