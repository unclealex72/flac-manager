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

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.message.JMessageService;
import uk.co.unclealex.music.musicbrainz.JOwnerService;

/**
 * A {@link JFlacFilesValidator} that makes sure that each FLAC file has at least one owner.
 * @author alex
 *
 */
public class JNoOwnerFlacFilesValidator implements JFlacFilesValidator {

  /**
   * The {@link uk.co.unclealex.music.musicbrainz.JOwnerService} used to find out who owns any releases.
   */
  private final JOwnerService ownerService;
  
  @Inject
  public JNoOwnerFlacFilesValidator(JOwnerService ownerService) {
    super();
    this.ownerService = ownerService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions validate(Map<JFileLocation, JMusicFile> musicFilesByFlacPath, JActions actions) throws IOException {
    JOwnerService ownerService = getOwnerService();
    for (JUser user : ownerService.getAllInvalidOwners()) {
      actions = actions.fail(null, JMessageService.NO_OWNER_INFORMATION, user.getMusicBrainzUserName());
    }
    for (Entry<JFileLocation, JMusicFile> entry : musicFilesByFlacPath.entrySet()) {
      JFileLocation fileLocation = entry.getKey();
      JMusicFile musicFile = entry.getValue();
      if (!ownerService.isFileOwnedByAnyone(musicFile)) {
        actions = actions.fail(fileLocation, JMessageService.NOT_OWNED);
      }
    }
    return actions;
  }

  public JOwnerService getOwnerService() {
    return ownerService;
  }
}
