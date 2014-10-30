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

package tempfs

import java.io.File
import java.nio.file.{Files, Path}

import org.specs2.mutable.After


trait TempFileSystem extends After {

  lazy val rootDirectory: Path = {
    val rootDirectory = Files.createTempDirectory("flac-manager-")
    before(rootDirectory)
    rootDirectory
  }

  def before(rootDirectory: Path): Unit

  def after = {
    def removeRecursively(f: File) {
      f.setWritable(true)
      if (f.isDirectory) {
        f.listFiles().foreach(removeRecursively)
      }
      f.delete
    }
    removeRecursively(rootDirectory.toFile)
  }
}
