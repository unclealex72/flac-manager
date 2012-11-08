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

import java.io.IOException;
import java.nio.file.Path;
import java.util.SortedSet;

import uk.co.unclealex.music.exception.InvalidDirectoriesException;

/**
 * An interface for classes that resolve directories and find FLAC files under
 * them as well as checking that each directory is relative to a given
 * directory.
 * 
 * @author alex
 * 
 */
public interface DirectoryService {

  /**
   * List all FLAC files under a list of directories.
   * 
   * @param requiredBasePath
   *          The path that all found directories must be relative to.
   * @param flacDirectories
   *          The directories to search.
   * @throws InvalidDirectoriesException
   *           Thrown if any of the supplied directories were not directories or
   *           if any of the supplied directories were not relative to the
   *           required base path.
   * @return All FLAC files found under the given directories.
   * @throws IOException 
   */
  public SortedSet<FileLocation> listFiles(Path requiredBasePath, Iterable<Path> flacDirectories)
      throws InvalidDirectoriesException, IOException;
}
