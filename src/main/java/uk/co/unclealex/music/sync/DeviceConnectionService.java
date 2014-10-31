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

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.sync.scsi.ScsiId;

import com.google.common.collect.Multimap;

/**
 * An interface for classes that can find which devices are currently connected
 * and also mount and unmount them.
 * 
 * @author alex
 * 
 */
public interface DeviceConnectionService {

  /**
   * Find which devices are currently connected.
   * 
   * @return A map of users with their connected devices.
   * @throws IOException
   */
  public Multimap<User, Device> listConnectedDevices() throws IOException;

  /**
   * Mount a device if it is not already mounted.
   * 
   * @param uuid
   *          The uuid of the device to mount.
   * @return The path where the device is mounted if it is already mounted,
   *         otherwise the newly created directory where it was mounted.
   * @throws IOException
   */
  public Path mount(String uuid) throws IOException;

  /**
   * Unmount a device and remove the path where it was mounted.
   * 
   * @param path
   *          The path where the device is mounted.
   * @throws IOException
   */
  public void unmount(Path path) throws IOException;

  /**
   * Find the SCSI id for a device with the given UUID.
   * 
   * @param uuid
   *          The UUID of the device to look for.
   * @return The SCSI ID of the device or null if it cannot be found.
   */
  public ScsiId findScsiIdForUuid(String uuid);

  /**
   * Find the mount point for a SCSI device.
   * 
   * @param scsiId
   *          The {@link ScsiId} of the device to find.
   * @return The path where the SCSI device is mounted or null if it is not.
   */
  public Path findMountPointForScsiId(ScsiId scsiId);
}
