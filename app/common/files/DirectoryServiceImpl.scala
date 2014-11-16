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
import java.nio.file._

import common.configuration.Directories
import common.files.FileLocationImplicits._
import common.message.MessageTypes._
import common.message._

import scala.collection.JavaConversions._
import scala.collection.immutable.SortedSet
/**
 * @author alex
 *
 */
class DirectoryServiceImpl(implicit directories: Directories) extends DirectoryService with Messaging {


  override def listFiles[FL <: FileLocation](fileLocations: Traversable[FL])(implicit messageService: MessageService): SortedSet[FL] = {
    fileLocations.foldLeft(SortedSet.empty[FL]) { (allFileLocations, fl) =>
      val childFileLocations = Files.list(fl).iterator().toSeq.map(p => fl.extendTo(p.getFileName))
      val (childDirectories, childFiles) = childFileLocations.sorted.partition(fl => Files.isDirectory(fl))
      childFiles.foreach(fl => log(FOUND_FILE(fl)))
      allFileLocations ++ childFiles ++ listFiles(childDirectories)
    }

  }
}