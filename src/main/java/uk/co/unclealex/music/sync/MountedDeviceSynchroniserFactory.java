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

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.process.ProcessRequest;
import uk.co.unclealex.process.builder.ProcessRequestBuilder;

/**
 * A superclass for {@link SynchroniserFactory}s who create {@link Synchroniser}
 * s for {@link Device}s that are mounted somewhere on the file system.
 * Currently, this is all of them.
 * 
 * @param <D>
 *          the generic type
 * @author alex
 */
public abstract class MountedDeviceSynchroniserFactory<D extends Device> implements SynchroniserFactory<D> {

  /**
   * The {@link ProcessRequestBuilder} used to create native processes.
   */
  private final ProcessRequestBuilder processRequestBuilder;

  /**
   * Instantiates a new mounted device synchroniser factory.
   * 
   * @param processRequestBuilder
   *          the process request builder
   */
  public MountedDeviceSynchroniserFactory(ProcessRequestBuilder processRequestBuilder) {
    super();
    this.processRequestBuilder = processRequestBuilder;
  }

  /**
   * Get the path where a {@link Device} is mounted or create it if need be.
   * 
   * @param device
   *          The device to check for.
   * @return The path where the {@link Device} is mounted.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected abstract Path createOrFindMountPoint(D device) throws IOException;

  /**
   * Unmount a device at the given path and then remove it.
   * 
   * @param path
   *          The path where the device is mounted.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void unmount(Path path) throws IOException {
    ProcessRequest processRequest =
        getProcessRequestBuilder().forCommand("pumount").withArguments(path.toAbsolutePath().toString());
    processRequest.executeAndWait();
    Files.deleteIfExists(path);
  }

  /**
   * Gets the {@link ProcessRequestBuilder} used to create native processes.
   *
   * @return the {@link ProcessRequestBuilder} used to create native processes
   */
  public ProcessRequestBuilder getProcessRequestBuilder() {
    return processRequestBuilder;
  }
}
