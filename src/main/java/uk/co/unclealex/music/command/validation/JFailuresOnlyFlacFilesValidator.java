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

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.action.JAction;
import uk.co.unclealex.music.action.JActionFunction;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.action.JFailureAction;
import uk.co.unclealex.music.files.JFileLocation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * A {@link JFlacFilesValidator} that returns only the {@link uk.co.unclealex.music.action.JFailureAction}s if
 * at least one exists. Otherwise, the original {@link uk.co.unclealex.music.action.JActions} are returned.
 * This should be the last validator in any list of validators.
 * 
 * @author alex
 * 
 */
public class JFailuresOnlyFlacFilesValidator implements JFlacFilesValidator {

  /**
   * The {@link uk.co.unclealex.music.action.JActions} object to use if failures are found.
   */
  private final JActions actions;

  @Inject
  public JFailuresOnlyFlacFilesValidator(JActions actions) {
    super();
    this.actions = actions;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions validate(Map<JFileLocation, JMusicFile> musicFilesByFlacPath, JActions actions) {
    Function<JAction, Boolean> failureFunction = new JActionFunction<Boolean>(false) {
      @Override
      protected Boolean visitAndReturn(JFailureAction failureAction) {
        return true;
      }
    };
    Predicate<JAction> failurePredicate = Predicates.compose(Predicates.equalTo(true), failureFunction);
    Iterable<JAction> failureActions = Iterables.filter(actions.get(), failurePredicate);
    if (Iterables.isEmpty(failureActions)) {
      return actions;
    }
    else {
      JActions newActions = getActions();
      for (JAction action : failureActions) {
        newActions = newActions.then(action);
      }
      return newActions;
    }
  }

  public JActions getActions() {
    return actions;
  }

}
