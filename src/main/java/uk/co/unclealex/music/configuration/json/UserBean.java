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

import java.util.List;

import uk.co.unclealex.music.DataObject;
import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A bean version of {@link User}.
 * 
 * @author alex
 */
public class UserBean extends DataObject implements User {

  /**
   * The MusicBrainz user name for this user.
   */
  private final String userName;

  /**
   * The MusicBrainz password for this user.
   */
  private final String password;

  /**
   * The {@link Device}s owned by this user.
   */
  private final List<Device> devices;

  /**
   * 
   * @param userName
   * @param password
   * @param devices
   */
  @JsonCreator
  public UserBean(
      @JsonProperty("userName") String userName,
      @JsonProperty("password") String password,
      @JsonProperty("devices") List<Device> devices) {
    super();
    this.userName = userName;
    this.password = password;
    this.devices = devices;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getUserName() {
    return userName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPassword() {
    return password;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Device> getDevices() {
    return devices;
  }
}
