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

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.action.JAction;
import uk.co.unclealex.music.action.JActionFunction;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.action.JChangeOwnerAction;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.musicbrainz.JChangeOwnershipService;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * A {@link JFlacFilesValidator} that commits any ownership changes.
 * 
 * @author alex
 * 
 */
public class JCommitOwnershipFlacFilesValidator implements JFlacFilesValidator {

  /**
   * The {@link uk.co.unclealex.music.musicbrainz.JOwnerService} used to find out who owns and alter any releases.
   */
  private final JChangeOwnershipService changeOwnershipService;

  /**
   * Instantiates a new commit ownership flac files validator.
   * 
   * @param changeOwnershipService
   *          the change ownership service
   */
  @Inject
  public JCommitOwnershipFlacFilesValidator(JChangeOwnershipService changeOwnershipService) {
    super();
    this.changeOwnershipService = changeOwnershipService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions validate(Map<JFileLocation, JMusicFile> musicFilesByFlacPath, JActions actions) throws IOException {
    Function<JAction, Boolean> changeOwnershipFunction = new JActionFunction<Boolean>(false) {
      protected Boolean visitAndReturn(JChangeOwnerAction changeOwnerAction) {
        return true;
      }
    };
    Predicate<JAction> isOwnershipAction = Predicates.compose(Predicates.equalTo(true), changeOwnershipFunction);
    if (Iterables.find(actions, isOwnershipAction, null) != null) {
      actions = actions.updateOwnership();
    }
    return actions;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.musicbrainz.JOwnerService} used to find out who owns and alter any
   * releases.
   * 
   * @return the {@link uk.co.unclealex.music.musicbrainz.JOwnerService} used to find out who owns and alter any
   *         releases
   */
  public JChangeOwnershipService getChangeOwnershipService() {
    return changeOwnershipService;
  }

}
