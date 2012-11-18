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

import javax.inject.Inject;

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.DeviceVisitor;
import uk.co.unclealex.music.configuration.FileSystemDevice;
import uk.co.unclealex.music.configuration.IpodDevice;
import uk.co.unclealex.music.configuration.MtpDevice;
import uk.co.unclealex.music.configuration.User;

/**
 * The default implementation of {@link SynchroniserFactory}.
 * @author alex
 *
 */
public class SynchroniserFactoryImpl implements SynchroniserFactory<Device> {

  /**
   * A synchroniser factory used to exclusively create {@link Synchroniser}s for iPODs.
   */
  private final SynchroniserFactory<IpodDevice> ipodSynchroniserFactory;
  
  /**
   * A synchroniser factory used to exclusively create {@link Synchroniser}s for block devices.
   */
  private final SynchroniserFactory<FileSystemDevice> fileSystemSynchroniserFactory;
  
  /**
   * A synchroniser factory used to exclusively create {@link Synchroniser}s for mtp devices.
   */
  private final SynchroniserFactory<MtpDevice> mtpFileSystemSynchroniserFactory;
  
  /**
   * Instantiates a new synchroniser factory impl.
   *
   * @param ipodSynchroniserFactory the ipod synchroniser factory
   * @param fileSystemSynchroniserFactory the file system synchroniser factory
   * @param mtpFileSystemSynchroniserFactory the mtp file system synchroniser factory
   */
  @Inject
  public SynchroniserFactoryImpl(
      SynchroniserFactory<IpodDevice> ipodSynchroniserFactory,
      SynchroniserFactory<FileSystemDevice> fileSystemSynchroniserFactory,
      SynchroniserFactory<MtpDevice> mtpFileSystemSynchroniserFactory) {
    super();
    this.ipodSynchroniserFactory = ipodSynchroniserFactory;
    this.fileSystemSynchroniserFactory = fileSystemSynchroniserFactory;
    this.mtpFileSystemSynchroniserFactory = mtpFileSystemSynchroniserFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Synchroniser createSynchroniser(final User owner, Device device) {
    DeviceVisitor<Synchroniser> visitor = new DeviceVisitor.Default<Synchroniser>() {
      /**
       * {@inheritDoc}
       */
      @Override
      public Synchroniser visit(IpodDevice ipodDevice) {
        return getIpodSynchroniserFactory().createSynchroniser(owner, ipodDevice);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Synchroniser visit(FileSystemDevice fileSystemDevice) {
        return getFileSystemSynchroniserFactory().createSynchroniser(owner, fileSystemDevice);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Synchroniser visit(MtpDevice mtpDevice) {
        return getMtpFileSystemSynchroniserFactory().createSynchroniser(owner, mtpDevice);
      }
    };
    return device.accept(visitor);
  }


  /**
   * Gets the a synchroniser factory used to exclusively create {@link Synchroniser}s for iPODs.
   *
   * @return the a synchroniser factory used to exclusively create {@link Synchroniser}s for iPODs
   */
  public SynchroniserFactory<IpodDevice> getIpodSynchroniserFactory() {
    return ipodSynchroniserFactory;
  }

  /**
   * Gets the a synchroniser factory used to exclusively create {@link Synchroniser}s for block devices.
   *
   * @return the a synchroniser factory used to exclusively create {@link Synchroniser}s for block devices
   */
  public SynchroniserFactory<FileSystemDevice> getFileSystemSynchroniserFactory() {
    return fileSystemSynchroniserFactory;
  }

  /**
   * Gets the a synchroniser factory used to exclusively create {@link Synchroniser}s for mtp devices.
   *
   * @return the a synchroniser factory used to exclusively create {@link Synchroniser}s for mtp devices
   */
  public SynchroniserFactory<MtpDevice> getMtpFileSystemSynchroniserFactory() {
    return mtpFileSystemSynchroniserFactory;
  }
}
