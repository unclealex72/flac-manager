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
 * http://www.apache.org/licenses/LICENSE-2.0
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

package common.files

import common.message.MessageService

import scala.util.Try

/**
 * Utilities for manipulating files and directories in ways not directly
 * supported by the JDK.
 *
 * @author alex
 *
 */
trait FileUtils {

  /**
   * Move a path from a source directory to a target directory using an atomic
   * file system move, creating any required directories. Any directories left
   * empty in the source base path due to the move operation will be removed.
   *
   * @param sourceFileLocation
   * The source file location.
   * @param targetFileLocation
   * The target file location.
   * @throws IOException
   */
  def move(sourceFileLocation: FileLocation, targetFileLocation: FileLocation)(implicit messageService: MessageService): Unit

  /**
   * Copy a path from a source directory to a target directory using an atomic
   * file system copy, creating any required directories.
   *
   * @param sourceFileLocation
   * The source file location.
   * @param targetFileLocation
   * The target file location.
   * @throws IOException
   */
  def copy(sourceFileLocation: FileLocation, targetFileLocation: FileLocation)(implicit messageService: MessageService): Unit

  /**
   * Remove directories if they are empty and recurse up the directory tree.
   *
   * @param fileLocation
   * The location of the current file to remove if empty.
   * @throws IOException
   */
  def remove(fileLocation: FileLocation)(implicit messageService: MessageService): Unit

  /**
   * Create a relative symbolic link from one file to another, creating any
   * required parent directories for the new link.
   *
   * @param fileLocation
   * The location of the file to link to.
   * @param linkLocation
   * The location of the new symbolic link.
   * @throws IOException
   */
  def link(fileLocation: FileLocation, linkLocation: FileLocation)(implicit messageService: MessageService): Unit

  /**
   * Return true if the file location points to a directory, false otherwise.
   * @param fileLocation
   * @return
   */
  def isDirectory(fileLocation: FileLocation): Boolean
}
