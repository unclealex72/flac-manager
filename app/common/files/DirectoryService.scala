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

import common.files.FileLocationImplicits._
import common.message.MessageService

import scala.collection.{SortedMap, SortedSet}

/**
 * An interface for classes that resolve directories and find FLAC files under
 * them as well as checking that each directory is relative to a given
 * directory.
 *
 * @author alex
 *
 */
trait DirectoryService {

  /**
   * List all music files under a list of directories.
   *
   * The path that all found directories must be relative to.
   * @param relativePaths
   * The directories to search.
   * @throws InvalidDirectoriesException
   * Thrown if any of the supplied directories were not directories or
   * if any of the supplied directories were not relative to the
   * required base path.
   * @return All music files found under the given directories.
   * @throws IOException
   */
  def listFiles[FL <: FileLocation](directories: Traversable[FL])(implicit messageService: MessageService): SortedSet[FL] =
    groupFiles(directories).foldLeft(SortedSet.empty[FL])(_ ++ _._2)

  def groupFiles[FL <: FileLocation](directories: Traversable[FL])(implicit messageService: MessageService): SortedMap[FL, SortedSet[FL]]

}
