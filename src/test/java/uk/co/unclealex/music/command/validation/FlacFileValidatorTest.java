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

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Map;

import javax.inject.Provider;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Action;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.ActionsImpl;
import uk.co.unclealex.music.files.FileLocation;

import com.google.common.collect.Iterables;

/**
 * A base clase for testing {@link FlacFilesValidator}s.
 * @author alex
 *
 */
public abstract class FlacFileValidatorTest {

  Map<FileLocation, MusicFile> musicFilesByFlacPath;
  Provider<Actions> actionsSupplier = new Provider<Actions>() {
    @Override
    public Actions get() {
      return new ActionsImpl();
    }
  };
  
  public void runTest(Actions expectedActions, Actions actions) throws IOException {
    expectedActions = actionsSupplier.get().then(expectedActions);
    FlacFilesValidator validator = createFlacFilesValidator();
    Actions actualActions = validator.validate(musicFilesByFlacPath, actions);
    assertThat(
        "The wrong actions were returned.",
        actualActions.get(),
        contains(Iterables.toArray(expectedActions, Action.class)));
  }

  protected abstract FlacFilesValidator createFlacFilesValidator();

}
