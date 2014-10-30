/*
 * Copyright 2014 Alex Jones
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
 */

package sync.drive

import java.nio.file.FileVisitOption
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.HashSet

import scala.collection.mutable.UnrolledBuffer

/**
 * @author alex
 *
 */
class DriveUuidServiceImpl extends DriveUuidService {

  /**
   * {@inheritDoc}
   */
  def listDrivesByUuid: Seq[(String, Path)] = {
    val drivesByUuid = UnrolledBuffer[(String, Path)]()
    val visitor: FileVisitor[Path] = new SimpleFileVisitor[Path] {
      override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        val uuid: String = file.getFileName.toString
        val path: Path = file.toFile.getCanonicalFile.toPath
        drivesByUuid += uuid -> path
        FileVisitResult.CONTINUE
      }
    }
    Files.walkFileTree(getBasePath, new HashSet[FileVisitOption], 1, visitor)
    drivesByUuid.toList
  }

  protected def getBasePath = Paths.get("/dev", "disk", "by-uuid")

}