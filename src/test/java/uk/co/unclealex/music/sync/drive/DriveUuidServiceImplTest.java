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

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.BiMap;

/**
 * @author alex
 * 
 */
public class DriveUuidServiceImplTest {

  Path tmpDir;

  @Before
  public void setup() throws IOException {
    tmpDir = Files.createTempDirectory("drive-uuid-test-");
  }

  @Test
  public void testUuids() throws IOException {
    final Path uuidBasePath = Files.createDirectory(tmpDir.resolve("uuids"));
    final Path deviceBasePath = Files.createDirectory(tmpDir.resolve("devices"));
    Matcher<Map<? extends String, ? extends Path>> matcher = null;
    for (final String uuid : new String[] { "one", "two", "three" }) {
      final Path devicePath = Files.createDirectories(deviceBasePath.resolve(uuid + uuid));
      Files.createSymbolicLink(uuidBasePath.resolve(uuid), devicePath);
      final Matcher<Map<? extends String, ? extends Path>> thisMatcher = hasEntry(uuid, devicePath);
      matcher = (matcher == null) ? thisMatcher : both(thisMatcher).and(matcher);
    }
    final DriveUuidServiceImpl driveUuidService = new DriveUuidServiceImpl() {
      @Override
      protected Path getBasePath() {
        return uuidBasePath;
      }
    };
    driveUuidService.initialise();
    final BiMap<String, Path> drivesByUuid = driveUuidService.listDrivesByUuid();
    assertThat("The wrong uuids were identified.", drivesByUuid, matcher);
    assertThat("The wrong number of uuids were identified.", drivesByUuid.entrySet(), hasSize(3));
  }

  @After
  public void clean() throws IOException {
    FileUtils.deleteDirectory(tmpDir.toFile());
  }
}
