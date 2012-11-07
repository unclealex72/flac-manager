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

import java.nio.file.Paths;

import org.junit.Test;

import uk.co.unclealex.music.MusicFileBean;
import uk.co.unclealex.music.action.Action;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.ActionsImpl;
import uk.co.unclealex.music.files.FileLocation;

import com.google.common.collect.Iterables;

/**
 * @author alex
 * 
 */
public class FailuresOnlyFlacFilesValidatorTest {

  FileLocation fl = new FileLocation(Paths.get("/"), Paths.get("dummy"));

  @Test
  public void testIncludingFailure() {
    Actions actions =
        new ActionsImpl()
            .coverArt(fl)
            .delete(fl)
            .encode(fl, fl, new MusicFileBean())
            .fail(fl, "messageTemplate", 1, 2)
            .move(fl, fl)
            .protect(fl)
            .unprotect(fl)
            .fail(fl, "otherMessageTemplate", 3, 4);
    Actions expectedActions =
        new ActionsImpl().fail(fl, "messageTemplate", 1, 2).fail(fl, "otherMessageTemplate", 3, 4);
    runTest(expectedActions, actions);
  }

  @Test
  public void testNoFailure() {
    Actions actions =
        new ActionsImpl()
            .coverArt(fl)
            .delete(fl)
            .encode(fl, fl, new MusicFileBean())
            .move(fl, fl)
            .protect(fl)
            .unprotect(fl);
    runTest(actions, actions);
  }

  public void runTest(Actions expectedActions, Actions actions) {
    FlacFilesValidator validator = new FailuresOnlyFlacFilesValidator(new ActionsImpl());
    Actions actualActions = validator.validate(null, actions);
    assertThat(
        "The wrong actions were returned.",
        actualActions.get(),
        contains(Iterables.toArray(expectedActions, Action.class)));
  }
}
