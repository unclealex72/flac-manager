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

import java.nio.file.Path;

/**
 * A configuration interface that is used to hold where the various directories are.
 * @author alex
 *
 */
public interface Directories {

  /**
   * Gets the top level path where FLAC files are stored.
   *
   * @return the top level path where FLAC files are stored
   */
  public abstract Path getFlacPath();

  /**
   * Gets the top level path where symbolic links for devices are created.
   *
   * @return the top level path where symbolic links for devices are created
   */
  public abstract Path getDevicesPath();

  /**
   * Gets the top level path where encoded files are stored.
   *
   * @return the top level path where encoded files are stored
   */
  public abstract Path getEncodedPath();

  /**
   * Gets the top level path where new and altered FLAC files are staged.
   *
   * @return the top level path where new and altered FLAC files are staged
   */
  public abstract Path getStagingPath();

}