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

package uk.co.unclealex.music.configuration.json;

import uk.co.unclealex.music.JDataObject;
import uk.co.unclealex.music.configuration.JDevice;

/**
 * A base bean for {@link uk.co.unclealex.music.configuration.JDevice}s that present their contents as part of the
 * UNIX file system.
 * 
 * @author alex
 * 
 */
public abstract class JAbstractFileSystemDeviceBean extends JDataObject implements JDevice {

  /**
   * The UUID of the device.
   */
  private final String uuid;

  /**
   * Instantiates a new abstract file system device bean.
   * 
   * @param uuid
   *          the uuid
   */
  public JAbstractFileSystemDeviceBean(final String uuid) {
    super();
    this.uuid = uuid;
  }

  /**
   * Gets the UUID of the device.
   * 
   * @return the UUID of the device
   */
  public String getUuid() {
    return uuid;
  }
}
