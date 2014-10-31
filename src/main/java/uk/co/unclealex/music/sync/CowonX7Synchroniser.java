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

import uk.co.unclealex.music.configuration.CowonX7Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.devices.DeviceService;
import uk.co.unclealex.music.files.DirectoryService;
import uk.co.unclealex.music.files.FileUtils;
import uk.co.unclealex.music.message.MessageService;
import uk.co.unclealex.music.sync.scsi.ScsiId;
import uk.co.unclealex.music.sync.scsi.ScsiIdFactory;

import com.google.inject.assistedinject.Assisted;

/**
 * The {@link Synchroniser} for {@link CowonX7Device}s.
 * 
 * @author alex
 * 
 */
public class CowonX7Synchroniser extends AbstractFileSystemSynchroniser<CowonX7Device> {

  /**
   * The {@link ScsiIdFactory} used to create {@link ScsiId}s.
   */
  private final ScsiIdFactory scsiIdFactory;

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
  public CowonX7Synchroniser(
      final MessageService messageService,
      final DirectoryService directoryService,
      final DeviceService deviceService,
      final DeviceConnectionService deviceConnectionService,
      final FileUtils fileUtils,
      final ScsiIdFactory scsiIdFactory,
      @Assisted final User owner,
      @Assisted final CowonX7Device device) {
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
    final DeviceConnectionService deviceConnectionService = getDeviceConnectionService();
    final ScsiId hdScsiId = deviceConnectionService.findScsiIdForUuid(getDevice().getUuid());
    final ScsiId flashScsiId =
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
   * Gets the {@link ScsiIdFactory} used to create {@link ScsiId}s.
   * 
   * @return the {@link ScsiIdFactory} used to create {@link ScsiId}s
   */
  public ScsiIdFactory getScsiIdFactory() {
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
