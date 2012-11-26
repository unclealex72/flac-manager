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

/**
 * Utilities for manipulating files and directories in ways not directly
 * supported by the JDK.
 * 
 * @author alex
 * 
 */
public interface FileUtils {

  /**
   * Move a path from a source directory to a target directory using an atomic
   * file system move, creating any required directories. Any directories left
   * empty in the source base path due to the move operation will be removed.
   * 
   * @param sourceFileLocation
   *          The source file location.
   * @param targetFileLocation
   *          The target file location.
   * @throws IOException
   */
  public void move(FileLocation sourceFileLocation, FileLocation targetFileLocation) throws IOException;

  /**
   * Remove directories if they are empty and recurse up the directory tree.
   * 
   * @param fileLocation
   *          The location of the current file to remove if empty.
   * @throws IOException
   */
  public void remove(FileLocation fileLocation) throws IOException;

  /**
   * Create a relative symbolic link from one file to another, creating any required
   * parent directories for the new link.
   * 
   * @param fileLocation
   *          The location of the file to link to.
   * @param linkLocation
   *          The location of the new symbolic link.
   * @throws IOException
   */
  public void link(FileLocation fileLocation, FileLocation linkLocation) throws IOException;
}
