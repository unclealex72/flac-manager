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
class LsscsiScsiServiceSpec extends Specification {

  trait fs extends TempFileSystem {
    override def before(rootDirectory: Path): Unit = {}
  }

  "The lsscsi SCSI service" should {
    "correctly parse the contents of the lsscsi command" in new fs {
      Seq("sda", "sda1", "sda2", "sdb", "sr0").foreach { dir =>
        Files.createDirectories(rootDirectory.resolve(dir))
      }
      val scsiService = new LsscsiScsiService {
        override def generateLines: Seq[String] = Seq(
          s"[0:0:0:0]    disk    ATA      INTEL SSDSC2MH12 PPG4  $rootDirectory/sda",
          s"[1:0:0:0]    disk    ATA      INTEL SSDSC2MH12 PPG4  $rootDirectory/sdb",
          s"[2:0:0:0]    cd/dvd  Slimtype DS8A5SH          XP91  $rootDirectory/sr0")
      }
      val scsi0: ScsiId = new ScsiId(0, 0, 0, 0)
      val scsi1: ScsiId = new ScsiId(1, 0, 0, 0)
      val scsi2: ScsiId = new ScsiId(2, 0, 0, 0)
      scsiService.listDevicePathsByScsiIds must contain(exactly(scsi0 -> rootDirectory.resolve("sda1")))
    }
  }
}