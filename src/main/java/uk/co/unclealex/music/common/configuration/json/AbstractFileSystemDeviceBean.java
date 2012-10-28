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

package uk.co.unclealex.music.common.configuration.json;

import java.nio.file.Path;

import javax.validation.constraints.NotNull;

import uk.co.unclealex.music.common.DataObject;
import uk.co.unclealex.music.common.configuration.Device;

/**
 * A base bean for {@link Device}s that present their contents as part of the UNIX file system.
 * @author alex
 *
 */
public abstract class AbstractFileSystemDeviceBean extends DataObject implements Device {

  /**
   * The {@link Path} where this device is mounted.
   */
  @NotNull
	private final Path mountPoint;

	/**
	 * 
	 * @param mountPoint
	 */
  public AbstractFileSystemDeviceBean(Path mountPoint) {
    super();
    this.mountPoint = mountPoint;
  }

  /**
   * Gets the {@link Path} where this device is mounted.
   *
   * @return the {@link Path} where this device is mounted
   */
  public Path getMountPoint() {
    return mountPoint;
  }
}
