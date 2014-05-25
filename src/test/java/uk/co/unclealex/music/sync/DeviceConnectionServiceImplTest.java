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

import uk.co.unclealex.music.configuration.JDevice;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.configuration.json.JCowonX7DeviceBean;
import uk.co.unclealex.music.configuration.json.JIpodDeviceBean;
import uk.co.unclealex.music.configuration.json.JUserBean;
import uk.co.unclealex.music.sync.drive.JDriveUuidService;
import uk.co.unclealex.music.sync.drive.JMountedDriveService;
import uk.co.unclealex.music.sync.drive.JScsiService;
import uk.co.unclealex.music.sync.scsi.JScsiId;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Multimap;

/**
 * @author alex
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceConnectionServiceImplTest {

  JDeviceConnectionServiceImpl deviceConnectionService;
  @Mock
  JScsiService scsiService;
  @Mock
  JMountedDriveService mountedDriveService;
  @Mock
  JDriveUuidService driveUuidService;
  @Mock
  JMounterService mounterService;

  JScsiId briansCowonHdScsiId = new JScsiId(6, 0, 0, 0);
  JScsiId briansCowonFlashScsiId = new JScsiId(6, 0, 0, 1);
  JDevice briansCowon = new JCowonX7DeviceBean("118118");
  Path briansCowonHdDevicePath = Paths.get("/dev", "sdb1");
  Path briansCowonFlashDevicePath = Paths.get("/dev", "sdc");
  JDevice briansIpod = new JIpodDeviceBean("229229");
  Path briansIpodDevicePath = Paths.get("/dev/sda1");
  Path briansIpodMountPoint = Paths.get("/media", "brian", "IPOD");
  JUser brian = new JUserBean("brian", "musicBrainzUserName", "musicBrainzPassword", Arrays.asList(
      briansIpod,
      briansCowon));
  JScsiId freddiesCowonHdScsiId = new JScsiId(5, 0, 0, 0);
  JScsiId freddiesCowonFlashScsiId = new JScsiId(5, 0, 0, 1);
  Path freddiesCowonHdDevicePath = Paths.get("/dev", "sdd1");
  Path freddiesCowonFlashDevicePath = Paths.get("/dev", "sde");
  Path freddiesCowonHdMountPath = Paths.get("/media", "freddie", "X7 HDD");
  Path freddiesCowonFlashMountPath = Paths.get("/media", "freddie", "X7 FLASH");
  String freddiesIpodUuid = "666333";
  JDevice freddiesCowon = new JCowonX7DeviceBean("007007");
  JDevice freddiesIpod = new JIpodDeviceBean(freddiesIpodUuid);
  JUser freddie = new JUserBean("freddie", "musicBrainzUserName", "musicBrainzPassword", Arrays.asList(
      freddiesIpod,
      freddiesCowon));
  List<JUser> users = Arrays.asList(brian, freddie);

  @Before
  public void setup() {
    when(mountedDriveService.listDevicesByMountPoint()).thenReturn(
        new ImmutableBiMap.Builder<Path, Path>()
            .put(freddiesCowonFlashMountPath, freddiesCowonFlashDevicePath)
            .put(freddiesCowonHdMountPath, freddiesCowonHdDevicePath)
            .put(briansIpodMountPoint, briansIpodDevicePath)
            .build());
    when(scsiService.listDevicePathsByScsiIds()).thenReturn(
        new ImmutableBiMap.Builder<JScsiId, Path>()
            .put(freddiesCowonHdScsiId, freddiesCowonHdDevicePath)
            .put(freddiesCowonFlashScsiId, freddiesCowonFlashDevicePath)
            .put(briansCowonHdScsiId, briansCowonHdDevicePath)
            .put(briansCowonFlashScsiId, briansCowonFlashDevicePath)
            .put(new JScsiId(1, 0, 0, 0), briansIpodDevicePath)
            .build());
    when(driveUuidService.listDrivesByUuid()).thenReturn(
        new ImmutableBiMap.Builder<String, Path>()
            .put(freddiesCowon.getUuid(), freddiesCowonHdDevicePath)
            .put(briansCowon.getUuid(), briansCowonHdDevicePath)
            .put(briansIpod.getUuid(), briansCowonFlashDevicePath)
            .build());
    deviceConnectionService =
        new JDeviceConnectionServiceImpl(scsiService, mounterService, mountedDriveService, driveUuidService, users);

  }

  /**
   * Test method for
   * {@link JDeviceConnectionServiceImpl#listConnectedDevices()}
   * .
   * 
   * @throws IOException
   */
  @Test
  public void testListConnectedDevices() throws IOException {
    final Multimap<JUser, JDevice> connectedDevices = deviceConnectionService.listConnectedDevices();
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
   * {@link JDeviceConnectionServiceImpl#findScsiIdForUuid(java.lang.String)}
   * .
   */
  @Test
  public void testFindScsiIdForUuid() {
    final JScsiId scsiId = deviceConnectionService.findScsiIdForUuid(freddiesCowon.getUuid());
    assertEquals("The wrong SCSI ID was returned.", freddiesCowonHdScsiId, scsiId);
  }

}
