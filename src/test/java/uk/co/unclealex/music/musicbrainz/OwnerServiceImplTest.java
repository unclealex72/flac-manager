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

package uk.co.unclealex.music.musicbrainz;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.MusicFileBean;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.configuration.json.UserBean;

import com.google.common.collect.Lists;

/**
 * @author alex
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class OwnerServiceImplTest {

  @Mock
  MusicBrainzClient musicBrainzClient;
  OwnerServiceImpl ownerService;
  User brianMay = new UserBean("Brian", null, null);
  User freddieMercury = new UserBean("Freddie", null, null);
  User rogerTaylor = new UserBean("Roger", null, null);
  List<User> users = Lists.newArrayList(brianMay, freddieMercury, rogerTaylor);

  @Before
  public void before() throws NoCollectionException {
    when(musicBrainzClient.getRelasesForOwner(brianMay)).thenReturn(Lists.newArrayList("1", "2", "3"));
    when(musicBrainzClient.getRelasesForOwner(freddieMercury)).thenReturn(Lists.newArrayList("2", "3", "4"));
    when(musicBrainzClient.getRelasesForOwner(rogerTaylor)).thenThrow(new NoCollectionException("xyz"));
    ownerService = new OwnerServiceImpl(musicBrainzClient, users);
    ownerService.setup();
  }

  @Test
  public void testInvalidOwners() {
    assertThat("The wrong owners were found to be invalid.", ownerService.getAllInvalidOwners(), contains(rogerTaylor));
  }

  @Test
  public void testOwnersForMusicFile() {
    testOwnersForMusicFile("1", brianMay);
    testOwnersForMusicFile("2", brianMay, freddieMercury);
    testOwnersForMusicFile("3", brianMay, freddieMercury);
    testOwnersForMusicFile("4", freddieMercury);
    testOwnersForMusicFile("5");
  }

  protected void testOwnersForMusicFile(String releaseId, User... expectedOwners) {
    MusicFile musicFile = new MusicFileBean();
    musicFile.setAlbumId(releaseId);
    assertThat(
        "The wrong owners were found for release " + releaseId,
        ownerService.getOwnersForMusicFile(musicFile),
        containsInAnyOrder(expectedOwners));
  }

  public void testIsMusicFileOwnedByAnyone() {
    testIsMusicFileOwnedByAnyone("1", true);
    testIsMusicFileOwnedByAnyone("2", true);
    testIsMusicFileOwnedByAnyone("3", true);
    testIsMusicFileOwnedByAnyone("4", true);
    testIsMusicFileOwnedByAnyone("5", false);
  }

  protected void testIsMusicFileOwnedByAnyone(String releaseId, boolean expectedResult) {
    MusicFile musicFile = new MusicFileBean();
    musicFile.setAlbumId(releaseId);
    assertEquals(
        "Checking whether release " + releaseId + " has any owners returned the wrong result.",
        expectedResult,
        ownerService.isFileOwnedByAnyone(musicFile));
  }
}
