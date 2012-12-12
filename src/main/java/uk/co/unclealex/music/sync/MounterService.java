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

/**
 * An interface for classes that can mount and unmount devices.
 * 
 * @author alex
 * 
 */
public interface MounterService {

  /**
   * Mount a device.
   * 
   * @param devicePath
   *          The path of the device to mount.
   * @param directoryName
   *          The directory name to supply to the <code>pmount</code> command.
   * @return The new mount point of the device.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public Path mount(final Path devicePath, final String directoryName) throws IOException;

  /**
   * Unmount a device.
   * 
   * @param path
   *          The path where the device is mounted.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void unmount(final Path path) throws IOException;

}
