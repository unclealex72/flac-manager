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

import uk.co.unclealex.music.DataObject;

/**
 * A class that encapsulates a location of a file within a repository of music
 * files. This is a reusable component because the general actions on files
 * require doing something to a file and then making sure that some sort of
 * change is then rippled up to the base of the repository.
 * 
 * @author alex
 * 
 */
public class FileLocation extends DataObject {

  /**
   * The base path of the repository.
   */
  private final Path basePath;
  
  /**
   * The location of the file relative to the base path.
   */
  private final Path relativePath;

  /**
   * Instantiates a new file location.
   *
   * @param basePath the base path
   * @param relativePath the relative path
   */
  public FileLocation(Path basePath, Path relativePath) {
    super();
    this.basePath = basePath;
    this.relativePath = relativePath;
  }

  /**
   * Resolve this file location to its absolute path.
   * @return The absolute path of the file identified by this class.
   */
  public Path resolve() {
    return getBasePath().resolve(getRelativePath());
  }
  
  /**
   * Gets the base path of the repository.
   *
   * @return the base path of the repository
   */
  public Path getBasePath() {
    return basePath;
  }

  /**
   * Gets the location of the file relative to the base path.
   *
   * @return the location of the file relative to the base path
   */
  public Path getRelativePath() {
    return relativePath;
  }
}
