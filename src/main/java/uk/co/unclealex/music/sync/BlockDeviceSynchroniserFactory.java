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
import java.io.PrintStream;
import java.nio.file.Path;

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.devices.DeviceService;
import uk.co.unclealex.process.builder.ProcessRequestBuilder;
/**
 * A base class for {@link SynchroniserFactory}s that create a.
 *
 * @param <D> the generic type
 * {@link Synchroniser} that can just use <code>rysnc</code>.
 * @author alex
 */
public abstract class BlockDeviceSynchroniserFactory<D extends Device> extends MountedDeviceSynchroniserFactory<D> {

  /**
   * The {@link DeviceService} used to find the device's repository.
   */
  private final DeviceService deviceService;

  /**
   * The stream to which any messages should be sent.
   */
  private final PrintStream stdout;

  /**
   * The stream to which any errors should be sent.
   */
  private final PrintStream stderr;

  /**
   * Instantiates a new block device synchroniser factory.
   *
   * @param processRequestBuilder the process request builder
   * @param deviceService the device service
   * @param stdout the stdout
   * @param stderr the stderr
   */
  public BlockDeviceSynchroniserFactory(
      ProcessRequestBuilder processRequestBuilder,
      DeviceService deviceService,
      PrintStream stdout,
      PrintStream stderr) {
    super(processRequestBuilder);
    this.deviceService = deviceService;
    this.stdout = stdout;
    this.stderr = stderr;
  }

  /**
   * Get the directory where music is stored on the device.
   * 
   * @param device
   *          The device.
   * @return The relative path where music is stored.
   */
  protected abstract Path getMusicDirectoryRelativeToDevice(D device);

  /**
   * {@inheritDoc}
   */
  @Override
  public Synchroniser createSynchroniser(User owner, D device) {
    return new BlockDeviceSynchroniser(owner, device);
  }

  /**
   * The Class BlockDeviceSynchroniser.
   */
  class BlockDeviceSynchroniser implements Synchroniser {

    /**
     * The {@link User} who owns the device.
     */
    private final User owner;

    /**
     * The {@link Device} to synchronise.
     */
    private final D device;

    /**
     * Instantiates a new block device synchroniser.
     *
     * @param owner the owner
     * @param device the device
     */
    public BlockDeviceSynchroniser(User owner, D device) {
      super();
      this.device = device;
      this.owner = owner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void synchronise() throws IOException {
      D device = getDevice();
      Path mountPoint = createOrFindMountPoint(device);
      Path targetDirectory = mountPoint.resolve(getMusicDirectoryRelativeToDevice(device));
      Path sourceDirectory = getDeviceService().getDeviceRepositoryBase(getOwner());
      try {
        getProcessRequestBuilder()
            .forCommand("rsync")
            .withArguments("-avrL", "--delete-before", sourceDirectory.toString() + "/", targetDirectory.toString())
            .withStandardOutput(getStdout())
            .withStandardError(getStderr())
            .executeAndWait();
      }
      finally {
        unmount(mountPoint);
      }
    }

    /**
     * Gets the {@link Device} to synchronise.
     *
     * @return the {@link Device} to synchronise
     */
    public D getDevice() {
      return device;
    }

    /**
     * Gets the {@link User} who owns the device.
     *
     * @return the {@link User} who owns the device
     */
    public User getOwner() {
      return owner;
    }
  }

  /**
   * Gets the {@link DeviceService} used to find the device's repository.
   *
   * @return the {@link DeviceService} used to find the device's repository
   */
  public DeviceService getDeviceService() {
    return deviceService;
  }

  /**
   * Gets the stream to which any messages should be sent.
   *
   * @return the stream to which any messages should be sent
   */
  public PrintStream getStdout() {
    return stdout;
  }

  /**
   * Gets the stream to which any errors should be sent.
   *
   * @return the stream to which any errors should be sent
   */
  public PrintStream getStderr() {
    return stderr;
  }
}
