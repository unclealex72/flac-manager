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
 *   http://www.apache.org/licenses/LICENSE-2.0
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

package uk.co.unclealex.music.sync;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.configuration.json.FileSystemDeviceBean;
import uk.co.unclealex.music.configuration.json.IpodDeviceBean;
import uk.co.unclealex.music.configuration.json.MtpDeviceBean;
import uk.co.unclealex.music.configuration.json.UserBean;
import uk.co.unclealex.process.ProcessCallback;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;
import com.mycila.inject.internal.guava.collect.Maps;

/**
 * @author alex
 * 
 */
public class ConnectedDeviceServiceImplTest {

  List<String> lsusbOutput = Arrays.asList(
      "Bus 001 Device 002: ID 8087:0024 Intel Corp. Integrated Rate Matching Hub",
      "Bus 002 Device 002: ID 8087:0024 Intel Corp. Integrated Rate Matching Hub",
      "Bus 001 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub",
      "Bus 002 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub",
      "Bus 003 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub",
      "Bus 004 Device 001: ID 1d6b:0003 Linux Foundation 3.0 root hub",
      "Bus 001 Device 003: ID 05c8:030d Cheng Uei Precision Industry Co., Ltd (Foxlink)",
      "Bus 001 Device 004: ID 04f2:aff1 Chicony Electronics Co., Ltd");

  List<String> mtabOutput =
      Arrays
          .asList(
              "none /run/user tmpfs rw,noexec,nosuid,nodev,size=104857600,mode=0755 0 0",
              "/dev/sda7 /home ext4 rw 0 0",
              "binfmt_misc /proc/sys/fs/binfmt_misc binfmt_misc rw,noexec,nosuid,nodev 0 0",
              "rpc_pipefs /run/rpc_pipefs rpc_pipefs rw 0 0",
              "gvfsd-fuse /run/user/alex/gvfs fuse.gvfsd-fuse rw,nosuid,nodev,user=alex 0 0",
              "remote:/mnt/music /mnt/multimedia nfs rw,vers=4,addr=192.168.7.14,clientaddr=192.168.7.15 0 0",
              "/dev/sdb2 /media/brian/BRIAN'S\\040IPOD vfat rw,nosuid,nodev,uid=1000,gid=1000,shortname=mixed,dmask=0077,utf8=1,showexec,flush,uhelper=udisks2 0 0",
              "remote:/mnt/home /mnt/home nfs rw,vers=4,addr=192.168.7.14,clientaddr=192.168.7.15 0 0"

          );

  @Test
  public void testConnectedDevices() throws IOException {
    IpodDeviceBean briansIpod = new IpodDeviceBean(Paths.get("/media", "brian", "BRIAN'S IPOD"));
    FileSystemDeviceBean briansFs = new FileSystemDeviceBean("dv", Paths.get("/mnt/multimedia"), Paths.get("Music"));
    User brian =
        new UserBean("brian", "bmay", "queen", Arrays.asList(new Device[] {
            briansIpod,
            new MtpDeviceBean("mtp", "05c8:ffff"),
            briansFs }));
    MtpDeviceBean freddiesWalkman = new MtpDeviceBean("mtp", "05c8:030d");
    FileSystemDeviceBean freddiesFs = new FileSystemDeviceBean("dvb", Paths.get("/mnt/home"), Paths.get("Music"));
    User freddie =
        new UserBean("freddie", "fmercury", "queen", Arrays.asList(new Device[] {
            new IpodDeviceBean(Paths.get("/media", "freddie", "FRED'S IPOD")),
            freddiesWalkman,
            freddiesFs }));
    List<User> users = Arrays.asList(brian, freddie);
    ConnectedDeviceService connectedDeviceService = new ConnectedDeviceServiceImpl(users, null) {
      @Override
      protected void executeProcess(ProcessCallback callback, String command, String... arguments) {
        List<String> lines;
        if ("lsusb".equals(command) && arguments.length == 0) {
          lines = lsusbOutput;
        }
        else if ("cat".equals(command) && arguments.length == 1 && "/etc/mtab".equals(arguments[0])) {
          lines = mtabOutput;
        }
        else {
          fail("An invalid native command was sent: " + command + " " + Joiner.on(' ').join(arguments));
          return;
        }
        for (String line : lines) {
          callback.lineWritten(line);
        }
      }
    };
    Multimap<User, Device> connectedDevices = connectedDeviceService.listConnectedDevices();
    Map<User, Device[]> expectedDevices = Maps.newHashMap();
    expectedDevices.put(brian, new Device[] { briansIpod, briansFs });
    expectedDevices.put(freddie, new Device[] { freddiesWalkman, freddiesFs });
    assertThat("The wrong users were returned.", connectedDevices.keySet(), containsInAnyOrder(brian, freddie));
    for (Entry<User, Collection<Device>> entry : connectedDevices.asMap().entrySet()) {
      assertThat(
          "User " + entry.getKey().getName() + " has the wrong connected devices.",
          entry.getValue(),
          containsInAnyOrder(expectedDevices.get(entry.getKey())));
    }
  }

}
