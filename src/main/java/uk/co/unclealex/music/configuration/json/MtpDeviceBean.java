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

import uk.co.unclealex.music.DataObject;
import uk.co.unclealex.music.configuration.DeviceVisitor;
import uk.co.unclealex.music.configuration.MtpDevice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A bean version of {@link MtpDevice}.
 * @author alex
 *
 */
public class MtpDeviceBean extends DataObject implements MtpDevice {

  /**
   * The name of this device.
   */
  private final String name;

  /**
   * The USB ID of this device.
   */
  private final String usbId;
  
  /**
   * 
   * @param name
   */
  @JsonCreator
  public MtpDeviceBean(@JsonProperty("name") String name, @JsonProperty("usbId") String usbId) {
    super();
    this.name = name;
    this.usbId = usbId;
  }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> R accept(DeviceVisitor<R> deviceVisitor) {
		return deviceVisitor.visit((MtpDevice) this);
	}

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return name;
  }

  public String getUsbId() {
    return usbId;
  }
}
