/*
 * Copyright 2014 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.files

import common.files.FileLocationImplicits._
import common.message.MessageService

import scala.collection.{SortedMap, SortedSet}

/**
 * Recursively list [[FileLocation]]s in a directory.
 *
 */
trait DirectoryService {

  /**
    * List all music files under a list of directories.
    * @param directories The directories to search.
    * @param messageService The [[MessageService]] used to report progress and log errors.
    * @tparam FL The type of [[FileLocation]]s to list.
    * @return A sorted set of all the found file locations.
    */
  def listFiles[FL <: FileLocation](directories: Traversable[FL])
                                   (implicit messageService: MessageService): SortedSet[FL] =
    groupFiles(directories).foldLeft(SortedSet.empty[FL])(_ ++ _._2)

  /**
    * List and group all music files under a list of directories by directory.
    * @param directories The directories to search.
    * @param messageService The [[MessageService]] used to report progress and log errors.
    * @tparam FL The type of [[FileLocation]]s to list.
    * @return A sorted map of all the found file locations.
    */
  def groupFiles[FL <: FileLocation](directories: Traversable[FL])
                                    (implicit messageService: MessageService): SortedMap[FL, SortedSet[FL]]

}
