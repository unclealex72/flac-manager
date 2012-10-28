/**
 * Copyright 2011 Alex Jones
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

/**
 * Utilities for manipulating files and directories in ways not directly
 * supported by the JDK.
 * 
 * @author alex
 * 
 */
public interface FileUtils {

  /**
   * Alter a file and its parent directories that that they are either writeable
   * or not.
   * 
   * @param basePath
   *          The limiting base path such that its parents will not be
   *          traversed.
   * @param relativePath
   *          The path, relative to the base path that will be made writeable or
   *          not.
   * @param allowWrites
   *          True if files and directories should be made writeable, false if
   *          not.
   * @throws IOException
   */
  public void alterWriteable(Path basePath, Path relativePath, boolean allowWrites) throws IOException;

  /**
   * Move a path from a source directory to a target directory using an atomic file system move, creating any
   * required directories. Any directories left empty in the source base path
   * due to the move operation will be removed.
   * 
   * @param sourceBasePath
   *          The source path.
   * @param relativePath
   *          The path, relative to the source base path, to move.
   * @param targetBasePath
   *          The path where directories will be created and the file moved to.
   * @throws IOException
   */
  public void move(Path sourceBasePath, Path relativePath, Path targetBasePath) throws IOException;

}
