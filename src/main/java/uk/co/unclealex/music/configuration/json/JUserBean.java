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

import uk.co.unclealex.music.JDataObject;
import uk.co.unclealex.music.configuration.JDevice;
import uk.co.unclealex.music.configuration.JUser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A bean version of {@link uk.co.unclealex.music.configuration.JUser}.
 * 
 * @author alex
 */
public class JUserBean extends JDataObject implements JUser {

  /**
   * The name for this user.
   */
  private final String name;
  
  /**
   * The MusicBrainz user name for this user.
   */
  private final String musicBrainzUserName;

  /**
   * The MusicBrainz password for this user.
   */
  private final String musicBrainzPassword;

  /**
   * The {@link uk.co.unclealex.music.configuration.JDevice}s owned by this user.
   */
  private final List<JDevice> devices;

  /**
   * Instantiates a new user bean.
   *
   * @param musicBrainzUserName the music brainz user name
   * @param musicBrainzPassword the music brainz password
   * @param devices the devices
   */
  @JsonCreator
  public JUserBean(
          @JsonProperty("name") String name,
          @JsonProperty("musicBrainzUserName") String musicBrainzUserName,
          @JsonProperty("musicBrainzPassword") String musicBrainzPassword,
          @JsonProperty("devices") List<JDevice> devices) {
    super();
    this.name = name;
    this.musicBrainzUserName = musicBrainzUserName;
    this.musicBrainzPassword = musicBrainzPassword;
    this.devices = devices;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<JDevice> getDevices() {
    return devices;
  }


  /**
   * {@inheritDoc}
   */
  public String getMusicBrainzUserName() {
    return musicBrainzUserName;
  }


  /**
   * {@inheritDoc}
   */
  public String getMusicBrainzPassword() {
    return musicBrainzPassword;
  }

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return name;
  }
}
