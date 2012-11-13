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

package uk.co.unclealex.music.files;

import java.nio.file.Path;

import uk.co.unclealex.music.configuration.Directories;

/**
 * An inteface for classes that can create {@link FileLocation}s for files known
 * to the {@link Directories} configuration class.
 * 
 * @author alex
 * 
 */
public interface FileLocationFactory {

  /**
   * Create a {@link FileLocation} in the main FLAC repository.
   * 
   * @param relativeFlacFile
   *          The relative file location.
   * @return A {@link FileLocation} in the main FLAC repository.
   */
  public FileLocation createFlacFileLocation(Path relativeFlacFile);

  /**
   * Create a {@link FileLocation} in the main MP3 repository.
   * 
   * @param relativeEncodedFile
   *          The relative file location.
   * @return A {@link FileLocation} in the main MP3 repository.
   */
  public FileLocation createEncodedFileLocation(Path relativeEncodedFile);

  /**
   * Create a {@link FileLocation} in the main staging repository.
   * 
   * @param relativeFlacFile
   *          The relative file location.
   * @return A {@link FileLocation} in the main staging repository.
   */
  public FileLocation createStagingFileLocation(Path relativeFlacFile);
}
