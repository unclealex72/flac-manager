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

package uk.co.unclealex.music.sync.drive;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.unclealex.music.sync.scsi.ScsiId;
import uk.co.unclealex.music.sync.scsi.ScsiIdFactory;

import com.google.common.collect.Lists;

/**
 * @author alex
 * 
 */
public class LsscsiScsiServiceTest {

  Path tmpDir;

  @Before
  public void setup() throws IOException {
    tmpDir = Files.createTempDirectory("lsscsiscsitest-");
  }

  @Test
  public void testLsscsi() throws IOException {
    for (final String dir : new String[] { "sda", "sda1", "sda2", "sdb", "sr0" }) {
      Files.createDirectories(tmpDir.resolve(dir));
    }
    final ScsiIdFactory scsiIdFactory = mock(ScsiIdFactory.class);
    final ScsiId scsi0 = new ScsiId(0, 0, 0, 0);
    final ScsiId scsi1 = new ScsiId(1, 0, 0, 0);
    final ScsiId scsi2 = new ScsiId(2, 0, 0, 0);
    when(scsiIdFactory.create("[0:0:0:0]")).thenReturn(scsi0);
    when(scsiIdFactory.create("[1:0:0:0]")).thenReturn(scsi1);
    when(scsiIdFactory.create("[2:0:0:0]")).thenReturn(scsi2);
    final LsscsiScsiService scsiService = new LsscsiScsiService(null, scsiIdFactory) {
      @Override
      public List<String> generateLines() {
        return Lists.newArrayList(
            "[0:0:0:0]    disk    ATA      INTEL SSDSC2MH12 PPG4  " + tmpDir + "/sda",
            "[1:0:0:0]    disk    ATA      INTEL SSDSC2MH12 PPG4  " + tmpDir + "/sdb",
            "[2:0:0:0]    cd/dvd  Slimtype DS8A5SH          XP91  " + tmpDir + "/sr0");
      };
    };
    scsiService.initialise();
    final Map<ScsiId, Path> map = scsiService.getMap();
    assertThat("The wrong number of scsi devices were returned.", map.entrySet(), hasSize(3));
    assertThat(
        "The wrong scsi devices were returned.",
        map,
        allOf(
            hasEntry(scsi0, tmpDir.resolve("sda1")),
            hasEntry(scsi1, tmpDir.resolve("sdb")),
            hasEntry(scsi2, tmpDir.resolve("sr0"))));
  }

  @After
  public void delete() throws IOException {
    FileUtils.deleteDirectory(tmpDir.toFile());
  }
}
