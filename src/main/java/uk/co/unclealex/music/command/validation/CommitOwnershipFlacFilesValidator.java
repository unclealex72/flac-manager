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

import javax.inject.Inject;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Action;
import uk.co.unclealex.music.action.ActionFunction;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.ChangeOwnerAction;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.musicbrainz.ChangeOwnershipService;
import uk.co.unclealex.music.musicbrainz.OwnerService;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * A {@link FlacFilesValidator} that commits any ownership changes.
 * 
 * @author alex
 * 
 */
public class CommitOwnershipFlacFilesValidator implements FlacFilesValidator {

  /**
   * The {@link OwnerService} used to find out who owns and alter any releases.
   */
  private final ChangeOwnershipService changeOwnershipService;

  /**
   * Instantiates a new commit ownership flac files validator.
   * 
   * @param changeOwnershipService
   *          the change ownership service
   */
  @Inject
  public CommitOwnershipFlacFilesValidator(ChangeOwnershipService changeOwnershipService) {
    super();
    this.changeOwnershipService = changeOwnershipService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Actions validate(Map<FileLocation, MusicFile> musicFilesByFlacPath, Actions actions) throws IOException {
    Function<Action, Boolean> changeOwnershipFunction = new ActionFunction<Boolean>(false) {
      protected Boolean visitAndReturn(ChangeOwnerAction changeOwnerAction) {
        return true;
      }
    };
    Predicate<Action> isOwnershipAction = Predicates.compose(Predicates.equalTo(true), changeOwnershipFunction);
    if (Iterables.find(actions, isOwnershipAction, null) != null) {
      actions = actions.updateOwnership();
    }
    return actions;
  }

  /**
   * Gets the {@link OwnerService} used to find out who owns and alter any
   * releases.
   * 
   * @return the {@link OwnerService} used to find out who owns and alter any
   *         releases
   */
  public ChangeOwnershipService getChangeOwnershipService() {
    return changeOwnershipService;
  }

}
