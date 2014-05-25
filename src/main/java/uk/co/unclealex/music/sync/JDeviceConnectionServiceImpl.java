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
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import uk.co.unclealex.music.configuration.JDevice;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.sync.drive.JDriveUuidService;
import uk.co.unclealex.music.sync.drive.JMountedDriveService;
import uk.co.unclealex.music.sync.drive.JScsiService;
import uk.co.unclealex.music.sync.scsi.JScsiId;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * The default implementation of {@link JDeviceConnectionService}.
 * 
 * @author alex
 */
public class JDeviceConnectionServiceImpl implements JDeviceConnectionService {

  /**
   * The {@link JMounterService} used to mount and unmount devices.
   */
  private final JMounterService mounterService;

  /**
   * The {@link uk.co.unclealex.music.sync.drive.JScsiService} used to find SCSI devices.
   */
  private final JScsiService scsiService;

  /**
   * The {@link uk.co.unclealex.music.sync.drive.JMountedDriveService} used to find devices on the Linux file
   * system.
   */
  private final JMountedDriveService mountedDriveService;

  /**
   * The {@link uk.co.unclealex.music.sync.drive.JDriveUuidService} used to find the UUIDs of connected devices.
   */
  private final JDriveUuidService driveUuidService;

  /**
   * A list of all known users.
   */
  private final List<JUser> users;

  /**
   * Instantiates a new device connection service impl.
   * 
   * @param scsiService
   *          the scsi service
   * @param mounterService
   *          the mounter service
   * @param mountedDriveService
   *          the mounted drive service
   * @param driveUuidService
   *          the drive uuid service
   * @param users
   *          the users
   */
  @Inject
  public JDeviceConnectionServiceImpl(
          final JScsiService scsiService,
          final JMounterService mounterService,
          final JMountedDriveService mountedDriveService,
          final JDriveUuidService driveUuidService,
          final List<JUser> users) {
    super();
    this.scsiService = scsiService;
    this.mounterService = mounterService;
    this.mountedDriveService = mountedDriveService;
    this.driveUuidService = driveUuidService;
    this.users = users;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Multimap<JUser, JDevice> listConnectedDevices() throws IOException {
    final Multimap<JUser, JDevice> connectedDevices = HashMultimap.create();
    final Map<String, Path> drivesByUuid = getDriveUuidService().listDrivesByUuid();
    for (final JUser user : getUsers()) {
      for (final JDevice device : user.getDevices()) {
        final Path drivePath = drivesByUuid.get(device.getUuid());
        if (drivePath != null) {
          connectedDevices.put(user, device);
        }
      }
    }
    return connectedDevices;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path mount(final String uuid) throws IOException {
    final Path devicePath = getDriveUuidService().listDrivesByUuid().get(uuid);
    final Path mountPath = getMountedDriveService().listDevicesByMountPoint().inverse().get(devicePath);
    if (mountPath == null) {
      return getMounterService().mount(devicePath, uuid);
    }
    else {
      return mountPath;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unmount(final Path path) throws IOException {
    getMounterService().unmount(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JScsiId findScsiIdForUuid(final String uuid) {
    final Path drivePath = getDriveUuidService().listDrivesByUuid().get(uuid);
    if (drivePath == null) {
      return null;
    }
    else {
      return getScsiService().listDevicePathsByScsiIds().inverse().get(drivePath);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path findMountPointForScsiId(final JScsiId scsiId) {
    final Path devicePath = getScsiService().listDevicePathsByScsiIds().get(scsiId);
    if (devicePath == null) {
      return null;
    }
    else {
      return getMountedDriveService().listDevicesByMountPoint().inverse().get(devicePath);
    }
  }

  /**
   * Gets the {@link uk.co.unclealex.music.sync.drive.JScsiService} used to find SCSI devices.
   * 
   * @return the {@link uk.co.unclealex.music.sync.drive.JScsiService} used to find SCSI devices
   */
  public JScsiService getScsiService() {
    return scsiService;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.sync.drive.JMountedDriveService} used to find devices on the Linux file
   * system.
   * 
   * @return the {@link uk.co.unclealex.music.sync.drive.JMountedDriveService} used to find devices on the Linux
   *         file system
   */
  public JMountedDriveService getMountedDriveService() {
    return mountedDriveService;
  }

  /**
   * Gets the a list of all known users.
   * 
   * @return the a list of all known users
   */
  public List<JUser> getUsers() {
    return users;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.sync.drive.JDriveUuidService} used to find the UUIDs of connected
   * devices.
   * 
   * @return the {@link uk.co.unclealex.music.sync.drive.JDriveUuidService} used to find the UUIDs of connected
   *         devices
   */
  public JDriveUuidService getDriveUuidService() {
    return driveUuidService;
  }

  /**
   * Gets the {@link JMounterService} used to mount and unmount devices.
   * 
   * @return the {@link JMounterService} used to mount and unmount devices
   */
  public JMounterService getMounterService() {
    return mounterService;
  }

}
