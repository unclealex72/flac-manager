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

import java.nio.file.Files
import java.nio.file.Path
import org.specs2.mutable._
import tempfs.TempFileSystem

/**
 * @author alex
 *
 */
class DriveUuidServiceImplSpec extends Specification {

  trait fs extends TempFileSystem {
    override def before(rootDirectory: Path): Unit = {}
  }

  "The drive UUID service" should {
    "identify the correct UUIDs" in new fs {
      val uuidBasePath = Files.createDirectory(rootDirectory.resolve("uuids"))
      val deviceBasePath = Files.createDirectory(rootDirectory.resolve("devices"))
      val driveUuidService: DriveUuidServiceImpl = new DriveUuidServiceImpl {
        protected override def getBasePath: Path = uuidBasePath
      }
      val uuids = Seq("one", "two", "three").map { uuid =>
        val devicePath: Path = Files.createDirectories(deviceBasePath.resolve(uuid + uuid))
        Files.createSymbolicLink(uuidBasePath.resolve(uuid), devicePath)
        uuid -> devicePath
      }
      driveUuidService.listDrivesByUuid must containTheSameElementsAs(uuids)
    }
  }
}