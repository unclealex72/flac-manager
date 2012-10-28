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

package uk.co.unclealex.music.common.configuration;

import uk.co.unclealex.music.common.configuration.json.FileSystemDeviceBean;
import uk.co.unclealex.music.common.configuration.json.IpodDeviceBean;
import uk.co.unclealex.music.common.configuration.json.MtpDeviceBean;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

/**
 * An interface that represents a type of external music device.
 * @author alex
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=As.PROPERTY, property="type")
@JsonSubTypes({
      @JsonSubTypes.Type(value=IpodDeviceBean.class, name="ipod"),
      @JsonSubTypes.Type(value=FileSystemDeviceBean.class, name="hd"),
      @JsonSubTypes.Type(value=MtpDeviceBean.class, name="mtp")

  }) 
public interface Device {

  /**
   * Get the name of this device.
   * @return The name of this device.
   */
  public String getName();
  
  /**
   * Accept a {@link DeviceVisitor}
   * @param deviceVisitor The device visitor to accept.
   * @return The value returned by the {@link DeviceVisitor}s <code>visit()</code> method.
   */
  public <R> R accept(DeviceVisitor<R> deviceVisitor);

}
