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

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.MusicFileBean;
import uk.co.unclealex.music.action.Action;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.ActionsImpl;
import uk.co.unclealex.music.action.FailureAction;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.configuration.json.UserBean;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.message.MessageService;
import uk.co.unclealex.music.musicbrainz.OwnerService;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
public class NoOwnerFlacFilesValidatorTest {

  @Test
  public void testOwnerValidation() throws IOException {
    OwnerService ownerService = mock(OwnerService.class);
    User brianMay = new UserBean("Brian", null, null);
    User freddieMercury = new UserBean("Freddie", null, null);
    when(ownerService.getAllInvalidOwners()).thenReturn(Sets.newHashSet(brianMay, freddieMercury));
    MusicFile mf1 = musicFile("1");
    MusicFile mf2 = musicFile("2");
    MusicFile mf3 = musicFile("3");
    FileLocation fl1 = fileLocation("1");
    FileLocation fl2 = fileLocation("2");
    FileLocation fl3 = fileLocation("3");
    when(ownerService.isFileOwnedByAnyone(mf1)).thenReturn(false);
    when(ownerService.isFileOwnedByAnyone(mf2)).thenReturn(true);
    when(ownerService.isFileOwnedByAnyone(mf3)).thenReturn(false);
    FlacFilesValidator flacFilesValidator = new NoOwnerFlacFilesValidator(ownerService);
    Map<FileLocation, MusicFile> musicFilesByFlacPath = Maps.newHashMap();
    musicFilesByFlacPath.put(fl1, mf1);
    musicFilesByFlacPath.put(fl2, mf2);
    musicFilesByFlacPath.put(fl3, mf3);
    Actions actualActions = flacFilesValidator.validate(musicFilesByFlacPath, new ActionsImpl());
    assertThat("The wrong actions were returned", actualActions, containsInAnyOrder(new Action[] {
        new FailureAction(null, MessageService.NO_OWNER_INFORMATION, brianMay.getUserName()),
        new FailureAction(null, MessageService.NO_OWNER_INFORMATION, freddieMercury.getUserName()),
        new FailureAction(fl1, MessageService.NOT_OWNED),
        new FailureAction(fl3, MessageService.NOT_OWNED) }));
  }

  protected MusicFile musicFile(String releaseId) {
    MusicFile musicFile = new MusicFileBean();
    musicFile.setAlbumId(releaseId);
    return musicFile;
  }

  protected FileLocation fileLocation(String path) {
    return new FileLocation(Paths.get("/"), Paths.get("flac", path));
  }
}
