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

package uk.co.unclealex.music.command.validation;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Test;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.JMusicFileBean;
import uk.co.unclealex.music.action.JAction;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.action.JActionsImpl;
import uk.co.unclealex.music.action.JFailureAction;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.configuration.json.JUserBean;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.message.JMessageService;
import uk.co.unclealex.music.musicbrainz.JOwnerService;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
public class NoOwnerFlacFilesValidatorTest {

  @Test
  public void testOwnerValidation() throws IOException {
    JOwnerService ownerService = mock(JOwnerService.class);
    JUser brianMay = new JUserBean("brian", "Brian", null, null);
    JUser freddieMercury = new JUserBean("freddie", "Freddie", null, null);
    when(ownerService.getAllInvalidOwners()).thenReturn(Sets.newHashSet(brianMay, freddieMercury));
    JMusicFile mf1 = musicFile("1");
    JMusicFile mf2 = musicFile("2");
    JMusicFile mf3 = musicFile("3");
    JFileLocation fl1 = fileLocation("1");
    JFileLocation fl2 = fileLocation("2");
    JFileLocation fl3 = fileLocation("3");
    when(ownerService.isFileOwnedByAnyone(mf1)).thenReturn(false);
    when(ownerService.isFileOwnedByAnyone(mf2)).thenReturn(true);
    when(ownerService.isFileOwnedByAnyone(mf3)).thenReturn(false);
    JFlacFilesValidator flacFilesValidator = new JNoOwnerFlacFilesValidator(ownerService);
    Map<JFileLocation, JMusicFile> musicFilesByFlacPath = Maps.newHashMap();
    musicFilesByFlacPath.put(fl1, mf1);
    musicFilesByFlacPath.put(fl2, mf2);
    musicFilesByFlacPath.put(fl3, mf3);
    JActions actualActions = flacFilesValidator.validate(musicFilesByFlacPath, new JActionsImpl());
    assertThat("The wrong actions were returned", actualActions, containsInAnyOrder(new JAction[] {
        new JFailureAction(null, JMessageService.NO_OWNER_INFORMATION, brianMay.getMusicBrainzUserName()),
        new JFailureAction(null, JMessageService.NO_OWNER_INFORMATION, freddieMercury.getMusicBrainzUserName()),
        new JFailureAction(fl1, JMessageService.NOT_OWNED),
        new JFailureAction(fl3, JMessageService.NOT_OWNED) }));
  }

  protected JMusicFile musicFile(String releaseId) {
    JMusicFile musicFile = new JMusicFileBean();
    musicFile.setAlbumId(releaseId);
    return musicFile;
  }

  protected JFileLocation fileLocation(String path) {
    return new JFileLocation(Paths.get("/"), Paths.get("flac", path), true);
  }
}
