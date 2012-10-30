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

import java.util.List;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * A configuration item for a user. Users must also have a MusicBrainz login and can own
 * a number of {@link Device}s.
 * @author alex
 *
 */
public interface User {

  /**
   * Gets the MusicBrainz user name for this user.
   *
   * @return the MusicBrainz user name for this user
   */
  @NotEmpty
  public abstract String getUserName();

  /**
   * Gets the MusicBrainz password for this user.
   *
   * @return the MusicBrainz password for this user
   */
  @NotEmpty
  public abstract String getPassword();

  /**
   * Gets the {@link Device}s owned by this user.
   *
   * @return the {@link Device}s owned by this user
   */
  @NotEmpty
  @Valid
  public abstract List<Device> getDevices();

}