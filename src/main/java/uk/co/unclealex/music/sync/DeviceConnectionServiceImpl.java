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

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.sync.drive.DriveUuidService;
import uk.co.unclealex.music.sync.drive.MountedDriveService;
import uk.co.unclealex.music.sync.drive.ScsiService;
import uk.co.unclealex.music.sync.scsi.ScsiId;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * The default implementation of {@link DeviceConnectionService}.
 * 
 * @author alex
 */
public class DeviceConnectionServiceImpl implements DeviceConnectionService {

  /**
   * The {@link MounterService} used to mount and unmount devices.
   */
  private final MounterService mounterService;

  /**
   * The {@link ScsiService} used to find SCSI devices.
   */
  private final ScsiService scsiService;

  /**
   * The {@link MountedDriveService} used to find devices on the Linux file
   * system.
   */
  private final MountedDriveService mountedDriveService;

  /**
   * The {@link DriveUuidService} used to find the UUIDs of connected devices.
   */
  private final DriveUuidService driveUuidService;

  /**
   * A list of all known users.
   */
  private final List<User> users;

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
  public DeviceConnectionServiceImpl(
      final ScsiService scsiService,
      final MounterService mounterService,
      final MountedDriveService mountedDriveService,
      final DriveUuidService driveUuidService,
      final List<User> users) {
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
  public Multimap<User, Device> listConnectedDevices() throws IOException {
    final Multimap<User, Device> connectedDevices = HashMultimap.create();
    final Map<String, Path> drivesByUuid = getDriveUuidService().listDrivesByUuid();
    for (final User user : getUsers()) {
      for (final Device device : user.getDevices()) {
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
  public ScsiId findScsiIdForUuid(final String uuid) {
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
  public Path findMountPointForScsiId(final ScsiId scsiId) {
    final Path devicePath = getScsiService().listDevicePathsByScsiIds().get(scsiId);
    if (devicePath == null) {
      return null;
    }
    else {
      return getMountedDriveService().listDevicesByMountPoint().inverse().get(devicePath);
    }
  }

  /**
   * Gets the {@link ScsiService} used to find SCSI devices.
   * 
   * @return the {@link ScsiService} used to find SCSI devices
   */
  public ScsiService getScsiService() {
    return scsiService;
  }

  /**
   * Gets the {@link MountedDriveService} used to find devices on the Linux file
   * system.
   * 
   * @return the {@link MountedDriveService} used to find devices on the Linux
   *         file system
   */
  public MountedDriveService getMountedDriveService() {
    return mountedDriveService;
  }

  /**
   * Gets the a list of all known users.
   * 
   * @return the a list of all known users
   */
  public List<User> getUsers() {
    return users;
  }

  /**
   * Gets the {@link DriveUuidService} used to find the UUIDs of connected
   * devices.
   * 
   * @return the {@link DriveUuidService} used to find the UUIDs of connected
   *         devices
   */
  public DriveUuidService getDriveUuidService() {
    return driveUuidService;
  }

  /**
   * Gets the {@link MounterService} used to mount and unmount devices.
   * 
   * @return the {@link MounterService} used to mount and unmount devices
   */
  public MounterService getMounterService() {
    return mounterService;
  }

}
