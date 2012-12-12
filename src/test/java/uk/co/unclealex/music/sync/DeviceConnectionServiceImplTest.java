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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsMultimapContaining.hasMultiEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.configuration.json.CowonX7DeviceBean;
import uk.co.unclealex.music.configuration.json.IpodDeviceBean;
import uk.co.unclealex.music.configuration.json.UserBean;
import uk.co.unclealex.music.sync.drive.DriveUuidService;
import uk.co.unclealex.music.sync.drive.MountedDriveService;
import uk.co.unclealex.music.sync.drive.ScsiService;
import uk.co.unclealex.music.sync.scsi.ScsiId;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Multimap;

/**
 * @author alex
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceConnectionServiceImplTest {

  DeviceConnectionServiceImpl deviceConnectionService;
  @Mock
  ScsiService scsiService;
  @Mock
  MountedDriveService mountedDriveService;
  @Mock
  DriveUuidService driveUuidService;
  @Mock
  MounterService mounterService;

  ScsiId briansCowonHdScsiId = new ScsiId(6, 0, 0, 0);
  ScsiId briansCowonFlashScsiId = new ScsiId(6, 0, 0, 1);
  Device briansCowon = new CowonX7DeviceBean("118118");
  Path briansCowonHdDevicePath = Paths.get("/dev", "sdb1");
  Path briansCowonFlashDevicePath = Paths.get("/dev", "sdc");
  Device briansIpod = new IpodDeviceBean("229229");
  Path briansIpodDevicePath = Paths.get("/dev/sda1");
  Path briansIpodMountPoint = Paths.get("/media", "brian", "IPOD");
  User brian = new UserBean("brian", "musicBrainzUserName", "musicBrainzPassword", Arrays.asList(
      briansIpod,
      briansCowon));
  ScsiId freddiesCowonHdScsiId = new ScsiId(5, 0, 0, 0);
  ScsiId freddiesCowonFlashScsiId = new ScsiId(5, 0, 0, 1);
  Path freddiesCowonHdDevicePath = Paths.get("/dev", "sdd1");
  Path freddiesCowonFlashDevicePath = Paths.get("/dev", "sde");
  Path freddiesCowonHdMountPath = Paths.get("/media", "freddie", "X7 HDD");
  Path freddiesCowonFlashMountPath = Paths.get("/media", "freddie", "X7 FLASH");
  String freddiesIpodUuid = "666333";
  Device freddiesCowon = new CowonX7DeviceBean("007007");
  Device freddiesIpod = new IpodDeviceBean(freddiesIpodUuid);
  User freddie = new UserBean("freddie", "musicBrainzUserName", "musicBrainzPassword", Arrays.asList(
      freddiesIpod,
      freddiesCowon));
  List<User> users = Arrays.asList(brian, freddie);

  @Before
  public void setup() {
    when(mountedDriveService.listDevicesByMountPoint()).thenReturn(
        new ImmutableBiMap.Builder<Path, Path>()
            .put(freddiesCowonFlashMountPath, freddiesCowonFlashDevicePath)
            .put(freddiesCowonHdMountPath, freddiesCowonHdDevicePath)
            .put(briansIpodMountPoint, briansIpodDevicePath)
            .build());
    when(scsiService.listDevicePathsByScsiIds()).thenReturn(
        new ImmutableBiMap.Builder<ScsiId, Path>()
            .put(freddiesCowonHdScsiId, freddiesCowonHdDevicePath)
            .put(freddiesCowonFlashScsiId, freddiesCowonFlashDevicePath)
            .put(briansCowonHdScsiId, briansCowonHdDevicePath)
            .put(briansCowonFlashScsiId, briansCowonFlashDevicePath)
            .put(new ScsiId(1, 0, 0, 0), briansIpodDevicePath)
            .build());
    when(driveUuidService.listDrivesByUuid()).thenReturn(
        new ImmutableBiMap.Builder<String, Path>()
            .put(freddiesCowon.getUuid(), freddiesCowonHdDevicePath)
            .put(briansCowon.getUuid(), briansCowonHdDevicePath)
            .put(briansIpod.getUuid(), briansCowonFlashDevicePath)
            .build());
    deviceConnectionService =
        new DeviceConnectionServiceImpl(scsiService, mounterService, mountedDriveService, driveUuidService, users);

  }

  /**
   * Test method for
   * {@link uk.co.unclealex.music.sync.DeviceConnectionServiceImpl#listConnectedDevices()}
   * .
   * 
   * @throws IOException
   */
  @Test
  public void testListConnectedDevices() throws IOException {
    final Multimap<User, Device> connectedDevices = deviceConnectionService.listConnectedDevices();
    assertThat("The wrong number of devices are connected.", connectedDevices.entries(), hasSize(3));
    assertThat(
        "The wrong devices are connected.",
        connectedDevices,
        allOf(
            hasMultiEntry(brian, briansCowon),
            hasMultiEntry(brian, briansIpod),
            hasMultiEntry(freddie, freddiesCowon)));
  }

  @Test
  public void testFindMountPointForMountedScsiId() {
    final Path mountPointForScsiId = deviceConnectionService.findMountPointForScsiId(freddiesCowonHdScsiId);
    assertEquals(
        "The wrong mount point was returned for where Freddie's cowon is mounted.",
        freddiesCowonHdMountPath,
        mountPointForScsiId);
  }

  @Test
  public void testFindMountPointForUnmountedScsiId() {
    final Path mountPointForScsiId = deviceConnectionService.findMountPointForScsiId(briansCowonFlashScsiId);
    assertNull("The wrong mount point was returned for where Freddie's cowon is mounted.", mountPointForScsiId);
  }

  @Test
  public void testMountUnmountedUuid() throws IOException {
    when(mounterService.mount(any(Path.class), any(String.class))).thenReturn(Paths.get("path"));
    final Path actualMountPoint = deviceConnectionService.mount(briansCowon.getUuid());
    verify(mounterService).mount(briansCowonHdDevicePath, briansCowon.getUuid());
    assertEquals("The device was mounted to the wrong path.", Paths.get("path"), actualMountPoint);
  }

  @Test
  public void testMountMountedUuid() throws IOException {
    final Path actualMountPoint = deviceConnectionService.mount(freddiesCowon.getUuid());
    verifyZeroInteractions(mounterService);
    assertEquals("The device was mounted to the wrong path.", freddiesCowonHdMountPath, actualMountPoint);
  }

  /**
   * Test method for
   * {@link uk.co.unclealex.music.sync.DeviceConnectionServiceImpl#findScsiIdForUuid(java.lang.String)}
   * .
   */
  @Test
  public void testFindScsiIdForUuid() {
    final ScsiId scsiId = deviceConnectionService.findScsiIdForUuid(freddiesCowon.getUuid());
    assertEquals("The wrong SCSI ID was returned.", freddiesCowonHdScsiId, scsiId);
  }

}
