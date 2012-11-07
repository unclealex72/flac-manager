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

import java.util.Map;

import javax.inject.Inject;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Action;
import uk.co.unclealex.music.action.ActionFunction;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.FailureAction;
import uk.co.unclealex.music.files.FileLocation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * A {@link FlacFilesValidator} that returns only the {@link FailureAction}s if at least one exists. Otherwise, the original
 * {@link Actions} are returned.
 * @author alex
 *
 */
public class FailuresOnlyFlacFilesValidator implements FlacFilesValidator {

  /**
   * The {@link Actions} object to use if failures are found.
   */
  private final Actions actions;
  
  @Inject
  public FailuresOnlyFlacFilesValidator(Actions actions) {
    super();
    this.actions = actions;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Actions validate(Map<FileLocation, MusicFile> musicFilesByFlacPath, Actions actions) {
    Function<Action, Boolean> failureFunction = new ActionFunction<Boolean>(false) {
      @Override
      protected Boolean visitAndReturn(FailureAction failureAction) {
        return true;
      }
    };
    Predicate<Action> failurePredicate = Predicates.compose(Predicates.equalTo(true), failureFunction);
    Iterable<Action> failureActions = Iterables.filter(actions.get(), failurePredicate);
    if (Iterables.isEmpty(failureActions)) {
      return actions;
    }
    else {
      Actions newActions = getActions();
      for (Action action : failureActions) {
        newActions = newActions.then(action);
      }
      return newActions;
    }
  }


  public Actions getActions() {
    return actions;
  }

}
