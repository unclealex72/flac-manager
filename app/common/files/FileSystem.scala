/*
 * Copyright 2018 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.files

import common.message.MessageService

/**
 * Utilities for manipulating files and directories in ways not directly
 * supported by the JDK.
 *
 * @author alex
 *
 */
trait FileSystem {

  /**
    * Move a path from a source directory to a target directory using an atomic
    * file system move, creating any required directories. Any directories left
    * empty in the source base path due to the move operation will be removed.
    *
    * @param sourceFile The source file location
    * @param targetFile The target file location
    * @param messageService The [[MessageService]] used to log progress or report errors.
    */
  def move(sourceFile: File, targetFile: File)(implicit messageService: MessageService): Unit

  /**
    * Make a file readable by anybody.
    * @param file The file to make readable.
    * @param messageService The [[MessageService]] used to log progress or report errors.
    */
  def makeWorldReadable(file: File)(implicit messageService: MessageService): Unit
  /**
    * Copy a path from a source directory to a target directory using an atomic
    * file system copy, creating any required directories.
    *
    * @param sourceFile The source file location
    * @param targetFile The target file location
    * @param messageService The [[MessageService]] used to log progress or report errors.
    */
  def copy(sourceFile: File, targetFile: File)(implicit messageService: MessageService): Unit

  /**
    * Remove directories if they are empty and recurse up the directory tree.
    *
    * @param file The file location to remove.
    * @param messageService The [[MessageService]] used to log progress or report errors.
   */
  def remove(file: File)(implicit messageService: MessageService): Unit

  /**
    * Create a relative symbolic link from one file to another, creating any
    * required parent directories for the new link.
    *
    * @param file The location of the file to link to.
    * @param link The location of the new symbolic link.
    * @param messageService The [[MessageService]] used to log progress or report errors.
    */
  def link(file: File, link: File)(implicit messageService: MessageService): Unit

}
