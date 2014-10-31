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

package uk.co.unclealex.music.message;

import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.configuration.json.IpodDeviceBean;
import uk.co.unclealex.music.configuration.json.UserBean;
import uk.co.unclealex.music.files.FileLocation;

import com.google.common.collect.Lists;

/**
 * @author alex
 * 
 */
public class MessageServiceImplTest {

  FileLocation fl1 = new FileLocation(Paths.get("/mnt", "flac"), Paths.get("myflacfile.flac"), true);
  FileLocation fl2 = new FileLocation(Paths.get("/mnt", "flac"), Paths.get("myotherflacfile.flac"), true);
  FileLocation fl3 = new FileLocation(Paths.get("/mnt", "flac"), Paths.get("yetanotherflacfile.flac"), true);
  FileLocation fl4 = new FileLocation(Paths.get("/mnt", "mp3"), Paths.get("myflacfile.mp3"), true);

  User brianMay = new UserBean("brian", "Brian May", "pwd", new ArrayList<Device>());
  Device device = new IpodDeviceBean("118118");
  User freddieMercury = new UserBean("freddie", "Freddie Mercury", "pwd", new ArrayList<Device>());

  @Test
  public void testArtwork() throws URISyntaxException {
    runTest(
        "Using artwork for /mnt/flac/myflacfile.flac from http://unclealex.co.uk/",
        MessageService.ARTWORK,
        fl1,
        new URI("http://unclealex.co.uk/"));
  }

  @Test
  public void testEncode() {
    runTest("Encoding /mnt/flac/myflacfile.flac to /mnt/mp3/myflacfile.mp3", MessageService.ENCODE, fl1, fl4);
  }

  @Test
  public void testDelete() {
    runTest("Deleting /mnt/flac/myflacfile.flac", MessageService.DELETE, fl1);
  }

  @Test
  public void testMove() {
    runTest("Moving /mnt/flac/myflacfile.flac to /mnt/flac/myotherflacfile.flac", MessageService.MOVE, fl1, fl2);
  }

  @Test
  public void testNotFlac() {
    runTest("/mnt/mp3/myflacfile.mp3 is not a FLAC file", MessageService.NOT_FLAC, fl4);
  }

  @Test
  public void testMissingArtwork() {
    runTest("Cannot find any artwork for /mnt/flac/yetanotherflacfile.flac", MessageService.MISSING_ARTWORK, fl3);
  }

  @Test
  public void testOverwrite() {
    runTest(
        "Processing /mnt/flac/myflacfile.flac will cause /mnt/flac/myotherflacfile.flac to be overwritten",
        MessageService.OVERWRITE,
        fl1,
        fl2);
  }

  @Test
  public void testNonUnique() {
    runTest(
        "/mnt/mp3/myflacfile.mp3 will be generated more than once from /mnt/flac/myflacfile.flac, /mnt/flac/myotherflacfile.flac and /mnt/flac/yetanotherflacfile.flac",
        MessageService.NON_UNIQUE,
        fl4,
        Arrays.asList(fl1, fl2, fl3));
  }

  @Test
  public void testNotOwned() {
    runTest("/mnt/flac/yetanotherflacfile.flac has no owners", MessageService.NOT_OWNED, fl3);
  }

  @Test
  public void testNoOwner() {
    runTest("Cannot find the owned releases for Brian May", MessageService.NO_OWNER_INFORMATION, "Brian May");
  }

  @Test
  public void testLink() {
    runTest("Linking /mnt/flac/yetanotherflacfile.flac to /mnt/flac/myflacfile.flac", MessageService.LINK, fl1, fl3);
  }

  @Test
  public void testUnlink() {
    runTest("Removing link /mnt/flac/yetanotherflacfile.flac", MessageService.UNLINK, fl1, fl3);
  }

  @Test
  public void testUnknownUser() {
    runTest("brian is not a valid user name", MessageService.UNKNOWN_USER, "brian");
  }

  @Test
  public void testAddOwner() {
    runTest(
        "Adding owners brian to /mnt/flac/myflacfile.flac",
        MessageService.ADD_OWNER,
        fl1,
        Lists.newArrayList(brianMay));
  }

  @Test
  public void testRemoveOwner() {
    runTest(
        "Removing owners brian and freddie from /mnt/flac/myflacfile.flac",
        MessageService.REMOVE_OWNER,
        fl1,
        Lists.newArrayList(brianMay, freddieMercury));
  }

  @Test
  public void testCommitOwnership() {
    runTest("Committing ownership changes to MusicBrainz", MessageService.COMMIT_OWNERSHIP);
  }

  @Test
  public void testSyncKeep() {
    runTest(
        "brian's iPOD: Keeping file a/b/c.mp3",
        MessageService.SYNC_KEEP,
        Paths.get("a", "b", "c.mp3"),
        brianMay.getName(),
        device.getName());
  }

  @Test
  public void testSyncAdd() {
    runTest(
        "brian's iPOD: Adding file a/b/c.mp3",
        MessageService.SYNC_ADD,
        Paths.get("a", "b", "c.mp3"),
        brianMay.getName(),
        device.getName());
  }

  @Test
  public void testSyncRemove() {
    runTest(
        "brian's iPOD: Removing file a/b/c.mp3",
        MessageService.SYNC_REMOVE,
        Paths.get("a", "b", "c.mp3"),
        brianMay.getName(),
        device.getName());
  }

  @Test
  public void testFoundFile() {
    runTest("Found file /mnt/flac/myflacfile.flac", MessageService.FOUND_FILE, fl1);
  }

  @Test
  public void testFoundDevice() {
    runTest("Found alex's iPOD", MessageService.FOUND_DEVICE, "alex", "iPOD");
  }

  @Test
  public void testSynchronising() {
    runTest("Synchronising alex's iPOD", MessageService.SYNCHRONISING, "alex", "iPOD");
  }

  @Test
  public void testFinishedSynchronising() {
    runTest("Finished synchronising alex's iPOD", MessageService.DEVICE_SYNCHRONISED, "alex", "iPOD");
  }

  @Test
  public void testFoundTrack() {
    runTest(
        "Found artist Queen, album A Kind of Magic (1 of 2), track 6 of 9: Who Wants to Live Forever? at /mnt/flac/myflacfile.flac",
        MessageService.FOUND_TRACK,
        fl1,
        "Queen",
        "A Kind of Magic",
        1,
        2,
        6,
        9,
        "Who Wants to Live Forever?");

  }

  @Test
  public void testFree() {
    runTest("My own message", "My own message");
  }

  protected void runTest(final String expectedMessage, final String template, final Object... arguments) {
    final StringWriter buffer = new StringWriter();
    final PrintWriter stdout = new PrintWriter(buffer, true);
    final MessageService messageService = new MessageServiceImpl(stdout);
    messageService.printMessage(template, arguments);
    assertEquals("The wrong message was printed.", expectedMessage + "\n", buffer.toString());
  }

}
