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

package uk.co.unclealex.music.devices;

import java.nio.file.Path;

import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.files.JFileLocation;

/**
 * An interface for classes that understand {@link uk.co.unclealex.music.configuration.JDevice}s.
 * @author alex
 *
 */
public interface JDeviceService {

  /**
   * Get the location of a link for a given file location and owner.
   * @param owner The {@link uk.co.unclealex.music.configuration.JUser} who owns the device.
   * @param encodedLocation The {@link uk.co.unclealex.music.files.JFileLocation} of the target encoded file.
   * @return The location of the symbolic link for a device owned by the owner.
   */
  public JFileLocation getLinkLocation(JUser owner, JFileLocation encodedLocation);
  
  /**
   * Get the base path of the device repository for an owner.
   * @param owner The {@link uk.co.unclealex.music.configuration.JUser} who owns the device.
   * @return The base path of the device repository.
   */
  public Path getDeviceRepositoryBase(JUser owner);
}
