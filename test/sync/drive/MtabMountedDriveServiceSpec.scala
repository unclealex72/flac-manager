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

import java.nio.file.Paths

import org.specs2.mutable._

/**
 * @author alex
 *
 */
class MtabMountedDriveServiceSpec extends Specification {
  "The mtab mounted drive service" should {
    "correctly parse the /etc/mtab file" in {
      val mountedDriveService: MtabMountedDriveService = new MtabMountedDriveService {
        override def generateLines: Seq[String] = Seq(
          """/dev/sda1 /media/LEXAR\040MEDIA vfat rw,nosuid,nodev,quiet,shortname=mixed,uid=1000,gid=1000,umask=077,iocharset=utf8 0 0""",
          "/dev/sda2 / ext4 rw,errors=remount-ro 0 0", "proc /proc proc rw,noexec,nosuid,nodev 0 0",
          "sysfs /sys sysfs rw,noexec,nosuid,nodev 0 0",
          "none /sys/fs/fuse/connections fusectl rw 0 0",
          "none /sys/kernel/debug debugfs rw 0 0", "none /sys/kernel/security securityfs rw 0 0",
          "udev /dev devtmpfs rw,mode=0755 0 0",
          "devpts /dev/pts devpts rw,noexec,nosuid,gid=5,mode=0620 0 0",
          "tmpfs /run tmpfs rw,noexec,nosuid,size=10%,mode=0755 0 0",
          "none /run/lock tmpfs rw,noexec,nosuid,nodev,size=5242880 0 0",
          "none /run/shm tmpfs rw,nosuid,nodev 0 0",
          "none /run/user tmpfs rw,noexec,nosuid,nodev,size=104857600,mode=0755 0 0",
          "/dev/sdb1 /home ext4 rw 0 0",
          "binfmt_misc /proc/sys/fs/binfmt_misc binfmt_misc rw,noexec,nosuid,nodev 0 0",
          "rpc_pipefs /run/rpc_pipefs rpc_pipefs rw 0 0",
          "gvfsd-fuse /run/user/alex/gvfs fuse.gvfsd-fuse rw,nosuid,nodev,user=alex 0 0")
      }
      mountedDriveService.listDevicesByMountPoint must contain(exactly(
        Paths.get("/media", "LEXAR MEDIA") -> Paths.get("/dev", "sda1"),
        Paths.get("/") -> Paths.get("/dev", "sda2"),
        Paths.get("/home") -> Paths.get("/dev", "sdb1")))
    }
  }
}