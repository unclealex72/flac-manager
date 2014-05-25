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

import uk.co.unclealex.music.configuration.JCowonX7Device;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.devices.JDeviceService;
import uk.co.unclealex.music.files.JDirectoryService;
import uk.co.unclealex.music.files.JFileUtils;
import uk.co.unclealex.music.message.JMessageService;
import uk.co.unclealex.music.sync.scsi.JScsiId;
import uk.co.unclealex.music.sync.scsi.JScsiIdFactory;

import com.google.inject.assistedinject.Assisted;

/**
 * The {@link JSynchroniser} for {@link uk.co.unclealex.music.configuration.JCowonX7Device}s.
 * 
 * @author alex
 * 
 */
public class JCowonX7Synchroniser extends JAbstractFileSystemSynchroniser<JCowonX7Device> {

  /**
   * The {@link uk.co.unclealex.music.sync.scsi.JScsiIdFactory} used to create {@link uk.co.unclealex.music.sync.scsi.JScsiId}s.
   */
  private final JScsiIdFactory scsiIdFactory;

  /**
   * The path where the X7's flash drive is mounted or null if it is not.
   */
  private Path flashMountPoint;

  /**
   * Instantiates a new cowon x7 synchroniser.
   * 
   * @param messageService
   *          the message service
   * @param directoryService
   *          the directory service
   * @param deviceService
   *          the device service
   * @param deviceConnectionService
   *          the device connection service
   * @param fileUtils
   *          the file utils
   * @param scsiIdFactory
   *          the scsi id factory
   * @param owner
   *          the owner
   * @param device
   *          the device
   */
  @Inject
  public JCowonX7Synchroniser(
          final JMessageService messageService,
          final JDirectoryService directoryService,
          final JDeviceService deviceService,
          final JDeviceConnectionService deviceConnectionService,
          final JFileUtils fileUtils,
          final JScsiIdFactory scsiIdFactory,
          @Assisted final JUser owner,
          @Assisted final JCowonX7Device device) {
    super(messageService, directoryService, deviceService, deviceConnectionService, fileUtils, owner, device);
    this.scsiIdFactory = scsiIdFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void beforeMount() throws IOException {
    // The SCSI ID of the flash drive is always one more than the SCSI ID of the
    // hd.
    final JDeviceConnectionService deviceConnectionService = getDeviceConnectionService();
    final JScsiId hdScsiId = deviceConnectionService.findScsiIdForUuid(getDevice().getUuid());
    final JScsiId flashScsiId =
        getScsiIdFactory().create(
            hdScsiId.getHba(),
            hdScsiId.getChannel(),
            hdScsiId.getTargetId(),
            hdScsiId.getLun() + 1);
    setFlashMountPoint(deviceConnectionService.findMountPointForScsiId(flashScsiId));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Path calculateBasePath(final Path mountPath) throws IOException {
    return mountPath.resolve("Music");
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
    final Path flashMountPoint = getFlashMountPoint();
    if (flashMountPoint != null) {
      getDeviceConnectionService().unmount(flashMountPoint);
    }
  }

  /**
   * Gets the {@link uk.co.unclealex.music.sync.scsi.JScsiIdFactory} used to create {@link uk.co.unclealex.music.sync.scsi.JScsiId}s.
   * 
   * @return the {@link uk.co.unclealex.music.sync.scsi.JScsiIdFactory} used to create {@link uk.co.unclealex.music.sync.scsi.JScsiId}s
   */
  public JScsiIdFactory getScsiIdFactory() {
    return scsiIdFactory;
  }

  /**
   * Gets the path where the X7's flash drive is mounted or null if it is not.
   * 
   * @return the path where the X7's flash drive is mounted or null if it is not
   */
  public Path getFlashMountPoint() {
    return flashMountPoint;
  }

  /**
   * Sets the path where the X7's flash drive is mounted or null if it is not.
   * 
   * @param flashMountPoint
   *          the new path where the X7's flash drive is mounted or null if it
   *          is not
   */
  public void setFlashMountPoint(final Path flashMountPoint) {
    this.flashMountPoint = flashMountPoint;
  }

}
