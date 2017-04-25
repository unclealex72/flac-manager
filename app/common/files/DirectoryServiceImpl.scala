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
import javax.inject.Inject

import common.configuration.Directories
import common.files.FileLocationImplicits._
import common.message.Messages._
import common.message._

import scala.collection.{SortedMap, SortedSet}
/**
 * @author alex
 *
 */
class DirectoryServiceImpl @Inject()(implicit fileLocationExtensions: FileLocationExtensions, directories: Directories) extends DirectoryService with Messaging {

  override def groupFiles[FL <: FileLocation](fileLocations: Traversable[FL])(implicit messageService: MessageService): SortedMap[FL, SortedSet[FL]] = {
    fileLocations.foldLeft(SortedMap.empty[FL, SortedSet[FL]]) { (allFileLocations, fl) =>
      val files = fl.toPath.toFile.listFiles().map(_.toPath)
      val childFileLocations = files.map(p => fl.resolve(p.getFileName))
      val (childDirectories, childFiles) = childFileLocations.sorted.partition(_.isDirectory)
      childFiles.foreach(fl => log(FOUND_FILE(fl)))
      val sortedChildFiles = childFiles.foldLeft(SortedSet.empty[FL])(_ + _)
      (allFileLocations + (fl -> sortedChildFiles)) ++ groupFiles(childDirectories)
    }

  }
}
