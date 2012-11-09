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

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.message.MessageService;
import uk.co.unclealex.music.musicbrainz.OwnerService;

/**
 * A {@link FlacFilesValidator} that makes sure that each FLAC file has at least one owner.
 * @author alex
 *
 */
public class NoOwnerFlacFilesValidator implements FlacFilesValidator {

  /**
   * The {@link OwnerService} used to find out who owns any releases.
   */
  private final OwnerService ownerService;
  
  @Inject
  public NoOwnerFlacFilesValidator(OwnerService ownerService) {
    super();
    this.ownerService = ownerService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Actions validate(Map<FileLocation, MusicFile> musicFilesByFlacPath, Actions actions) throws IOException {
    OwnerService ownerService = getOwnerService();
    for (User user : ownerService.getAllInvalidOwners()) {
      actions = actions.fail(null, MessageService.NO_OWNER_INFORMATION, user.getUserName());
    }
    for (Entry<FileLocation, MusicFile> entry : musicFilesByFlacPath.entrySet()) {
      FileLocation fileLocation = entry.getKey();
      MusicFile musicFile = entry.getValue();
      if (!ownerService.isFileOwnedByAnyone(musicFile)) {
        actions = actions.fail(fileLocation, MessageService.NOT_OWNED);
      }
    }
    return actions;
  }

  public OwnerService getOwnerService() {
    return ownerService;
  }
}
