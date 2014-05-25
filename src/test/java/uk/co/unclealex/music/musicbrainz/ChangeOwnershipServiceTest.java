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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.JMusicFileBean;
import uk.co.unclealex.music.configuration.JDevice;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.configuration.json.JUserBean;

/**
 * @author alex
 *
 */
public class ChangeOwnershipServiceTest {

  @Test
  public void testChangeOwnership() throws JNoCollectionException {
    JMusicBrainzClient musicBrainzClient = mock(JMusicBrainzClient.class);
    JChangeOwnershipService changeOwnershipService = new JChangeOwnershipServiceImpl(musicBrainzClient);
    JUser freddie = new JUserBean("freddie", "Freddie Mercury", "pass", new ArrayList<JDevice>());
    JUser brian = new JUserBean("brian", "Brian May", "pass", new ArrayList<JDevice>());
    String[] releaseIds = { "0", "1", "2", "3", "4", "5", "0", "1", "2", "3", "4", "5" };
    JMusicFile[] musicFiles = new JMusicFile[releaseIds.length];
    for (int idx = 0; idx < releaseIds.length; idx++) {
      JMusicFile musicFile = new JMusicFileBean();
      musicFile.setAlbumId(releaseIds[idx]);
      musicFiles[idx] = musicFile;
    }
    changeOwnershipService.changeOwnership(musicFiles[0], true, Arrays.asList(freddie, brian));
    changeOwnershipService.changeOwnership(musicFiles[6], true, Arrays.asList(brian));
    changeOwnershipService.changeOwnership(musicFiles[1], true, Arrays.asList(brian));
    changeOwnershipService.changeOwnership(musicFiles[2], false, Arrays.asList(brian, freddie));
    changeOwnershipService.changeOwnership(musicFiles[3], false, Arrays.asList(freddie));
    changeOwnershipService.commitChanges();
    verify(musicBrainzClient).addReleases(eq(brian), argThat(containsInAnyOrder("0", "1")));
    verify(musicBrainzClient).addReleases(eq(freddie), argThat(containsInAnyOrder("0")));
    verify(musicBrainzClient).removeReleases(eq(brian), argThat(containsInAnyOrder("2")));
    verify(musicBrainzClient).removeReleases(eq(freddie), argThat(containsInAnyOrder("2", "3")));
    verifyNoMoreInteractions(musicBrainzClient);
  }

}
