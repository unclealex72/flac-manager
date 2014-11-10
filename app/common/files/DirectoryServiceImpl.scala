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

import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor}

import common.configuration.Directories
import common.message._

import scala.collection.immutable.SortedSet
import scala.collection.mutable
import scala.util.Try
import common.files.FileLocationImplicits._

/**
 * @author alex
 *
 */
class DirectoryServiceImpl(implicit directories: Directories) extends DirectoryService {


  override def listStagedFiles(relativePaths: Traversable[Path])(implicit messageService: MessageService): SortedSet[StagedFlacFileLocation] = {
    listFiles[StagedFlacFileLocation](directories.stagingPath, relativePaths, path => StagedFlacFileLocation(path))
  }

  override def listFlacFiles(relativePaths: Traversable[Path])(implicit messageService: MessageService): SortedSet[FlacFileLocation] = {
    listFiles(directories.flacPath, relativePaths, path => FlacFileLocation(path))
  }

  def listFiles[FL <: FileLocation](basePath: Path, relativePaths: Traversable[Path], fileLocationFactory: Path => FL)(implicit messageService: MessageService): SortedSet[FL] = {
    val fileLocations = mutable.Buffer[FL]()
    relativePaths.map(basePath.resolve(_)).foreach { absolutePath =>
      walkFileTree(absolutePath) { path =>
        val fileLocation = fileLocationFactory(basePath.relativize(path))
        fileLocations += fileLocation
        messageService.printMessage(FOUND_FILE(fileLocation))
      }
    }
    fileLocations.to[SortedSet]
  }

  def walkFileTree(path: Path)(visitor: Path => Any): Unit = {
    val simpleFileVisitor = new SimpleFileVisitor[Path] {
      override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        visitor(file)
        super.visitFile(file, attrs)
      }
    }
    Files.walkFileTree(path, simpleFileVisitor)
  }
}
