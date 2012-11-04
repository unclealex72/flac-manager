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

package uk.co.unclealex.music.configuration;

import java.nio.file.Path;

import javax.validation.constraints.NotNull;

import uk.co.unclealex.validator.paths.CanRead;
import uk.co.unclealex.validator.paths.CanWrite;
import uk.co.unclealex.validator.paths.IsDirectory;

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
  @IsDirectory
  @CanRead
  @NotNull
  public Path getFlacPath();

  /**
   * Gets the top level path where symbolic links for devices are created.
   *
   * @return the top level path where symbolic links for devices are created
   */
  @IsDirectory
  @CanRead
  @CanWrite
  @NotNull
  public Path getDevicesPath();

  /**
   * Gets the top level path where encoded files are stored.
   *
   * @return the top level path where encoded files are stored
   */
  @IsDirectory
  @CanRead
  @CanWrite
  @NotNull
  public Path getEncodedPath();

  /**
   * Gets the top level path where new and altered FLAC files are staged.
   *
   * @return the top level path where new and altered FLAC files are staged
   */
  @IsDirectory
  @CanRead
  @CanWrite
  @NotNull
  public abstract Path getStagingPath();

}