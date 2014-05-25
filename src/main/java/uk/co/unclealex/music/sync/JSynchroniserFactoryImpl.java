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

import uk.co.unclealex.music.configuration.*;
import uk.co.unclealex.music.configuration.JDeviceVisitor;

/**
 * The default implementation of {@link JSynchroniserFactory}.
 * 
 * @author alex
 * 
 */
public class JSynchroniserFactoryImpl implements JSynchroniserFactory<JDevice> {

  /**
   * A synchroniser factory used to exclusively create {@link JSynchroniser}s for
   * iPODs.
   */
  private final JSynchroniserFactory<JIpodDevice> ipodSynchroniserFactory;

  /**
   * A synchroniser factory used to exclusively create {@link JSynchroniser}s for
   * block devices.
   */
  private final JSynchroniserFactory<JFileSystemDevice> fileSystemSynchroniserFactory;

  /**
   * A synchroniser factory used to exclusively create {@link JSynchroniser}s for
   * Cowon X7 devices.
   */
  private final JSynchroniserFactory<JCowonX7Device> cowonX7SynchroniserFactory;

  /**
   * Instantiates a new synchroniser factory impl.
   * 
   * @param ipodSynchroniserFactory
   *          the ipod synchroniser factory
   * @param fileSystemSynchroniserFactory
   *          the file system synchroniser factory
   * @param cowonX7SynchroniserFactory
   *          the cowon x7 synchroniser factory
   */
  @Inject
  public JSynchroniserFactoryImpl(
          final JSynchroniserFactory<JIpodDevice> ipodSynchroniserFactory,
          final JSynchroniserFactory<JFileSystemDevice> fileSystemSynchroniserFactory,
          final JSynchroniserFactory<JCowonX7Device> cowonX7SynchroniserFactory) {
    super();
    this.ipodSynchroniserFactory = ipodSynchroniserFactory;
    this.fileSystemSynchroniserFactory = fileSystemSynchroniserFactory;
    this.cowonX7SynchroniserFactory = cowonX7SynchroniserFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JSynchroniser createSynchroniser(final JUser owner, final JDevice device) {
    final JDeviceVisitor<JSynchroniser> visitor = new JDeviceVisitor.Default<JSynchroniser>() {
      /**
       * {@inheritDoc}
       */
      @Override
      public JSynchroniser visit(final JIpodDevice ipodDevice) {
        return getIpodSynchroniserFactory().createSynchroniser(owner, ipodDevice);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public JSynchroniser visit(final JFileSystemDevice fileSystemDevice) {
        return getFileSystemSynchroniserFactory().createSynchroniser(owner, fileSystemDevice);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public JSynchroniser visit(final JCowonX7Device cowonX7Device) {
        return getCowonX7SynchroniserFactory().createSynchroniser(owner, cowonX7Device);
      }
    };
    return device.accept(visitor);
  }

  /**
   * Gets the a synchroniser factory used to exclusively create.
   * 
   * @return the a synchroniser factory used to exclusively create
   *         {@link JSynchroniser}s for iPODs. {@link JSynchroniser}s for iPODs
   */
  public JSynchroniserFactory<JIpodDevice> getIpodSynchroniserFactory() {
    return ipodSynchroniserFactory;
  }

  /**
   * Gets the a synchroniser factory used to exclusively create.
   * 
   * @return the a synchroniser factory used to exclusively create
   *         {@link JSynchroniser}s for block devices. {@link JSynchroniser}s for
   *         block devices
   */
  public JSynchroniserFactory<JFileSystemDevice> getFileSystemSynchroniserFactory() {
    return fileSystemSynchroniserFactory;
  }

  /**
   * Gets the a synchroniser factory used to exclusively create
   * {@link JSynchroniser}s for Cowon X7 devices.
   * 
   * @return the a synchroniser factory used to exclusively create
   *         {@link JSynchroniser}s for Cowon X7 devices
   */
  public JSynchroniserFactory<JCowonX7Device> getCowonX7SynchroniserFactory() {
    return cowonX7SynchroniserFactory;
  }
}
