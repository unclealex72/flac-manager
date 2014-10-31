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

import common.message.{FOUND_FILE, MessageService}

import scala.collection.immutable.SortedSet
import scala.collection.mutable
import scala.util.Try
import common.files.FileLocationImplicits._

/**
 * @author alex
 *
 */
class DirectoryServiceImpl(messageService: MessageService) extends DirectoryService {

  /**
   * {@inheritDoc}
   *
   * @throws IOException
   */
  def listFiles(requiredBasePath: FileLocation, directories: Traversable[Path]): Try[SortedSet[FileLocation]] = Try {
    def absolute: Path => Path = _.toAbsolutePath
    val absoluteRequiredBasePath = absolute(requiredBasePath);
    val absoluteDirectories = directories map absolute
    val invalidPaths = directories.filterNot(path => Files.isDirectory(path) && path.startsWith(absoluteRequiredBasePath))
    if (!invalidPaths.isEmpty) {
      throw new InvalidDirectoriesException(
        s"The following paths either do not exist or are not a subpath of ${absoluteRequiredBasePath}: ${invalidPaths.mkString(", ")}", invalidPaths)
    }
    else {
      val allAbsoluteFiles = absoluteDirectories.map(findAllFiles)
      allAbsoluteFiles.flatten.map(path => requiredBasePath.resolve(absoluteRequiredBasePath.relativize(path))).to[SortedSet]
    }
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


  def findAllFiles: Path => Traversable[Path] = { path =>
    val files: mutable.Buffer[Path] = mutable.Buffer.empty;
    walkFileTree(path)(path => files += path)
    files
  }


  override def listFiles(basePath: Path): Try[SortedSet[FileLocation]] = Try {
    val fileLocations = mutable.Buffer[FileLocation]()
    walkFileTree(basePath) { path =>
      val fileLocation = FileLocation(basePath, basePath.relativize(path), Files.isWritable(path))
      fileLocations += fileLocation
      messageService.printMessage(FOUND_FILE(fileLocation))
    }
    fileLocations.to[SortedSet]
  }

}
