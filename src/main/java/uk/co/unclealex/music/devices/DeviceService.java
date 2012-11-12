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

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.files.FileLocation;

/**
 * An interface for classes that understand {@link Device}s.
 * @author alex
 *
 */
public interface DeviceService {

  /**
   * Get the location of a link for a given file location and owner.
   * @param owner The {@link User} who owns the device.
   * @param encodedLocation The {@link FileLocation} of the target encoded file.
   * @return The location of the symbolic link for a device owned by the owner.
   */
  public FileLocation getLinkLocation(User owner, FileLocation encodedLocation);
}
