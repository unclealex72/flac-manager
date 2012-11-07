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
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.unclealex.music.action.Action;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.ActionsImpl;
import uk.co.unclealex.music.files.FileLocation;

import com.google.common.collect.Iterables;

/**
 * @author alex
 * 
 */
public class NoOverwritingFlacFilesValidatorTest {

  FileLocation existingFileLocation;
  FileLocation nonExistingFileLocation;

  @Before
  public void setup() throws IOException {
    existingFileLocation =
        new FileLocation(Paths.get("/"), Paths.get("/").relativize(
            Files.createTempFile("no-overwriting-flac-files-validator-", ".flac")));
    nonExistingFileLocation =
        new FileLocation(Paths.get("/"), Paths.get("/").relativize(
            Files.createTempFile("no-overwriting-flac-files-validator-", ".flac")));
    Files.delete(nonExistingFileLocation.resolve());
  }

  @Test
  public void testNonExisting() {
    Actions actions =
        new ActionsImpl().move(existingFileLocation, nonExistingFileLocation);
    runTest(actions, actions);
  }

  @Test
  public void testExisting() {
    Actions actions =
        new ActionsImpl().move(nonExistingFileLocation, existingFileLocation);
    Actions expectedActions =
        new ActionsImpl()
            .move(nonExistingFileLocation, existingFileLocation)
            .fail(nonExistingFileLocation, "overwrite", existingFileLocation);
    runTest(expectedActions, actions);
  }

  @After
  public void delete() throws IOException {
    for (FileLocation fileLocation : new FileLocation[] { existingFileLocation, nonExistingFileLocation }) {
      Files.deleteIfExists(fileLocation.resolve());
    }
  }

  public void runTest(Actions expectedActions, Actions actions) {
    FlacFilesValidator validator = new NoOverwritingFlacFilesValidator();
    Actions actualActions = validator.validate(null, actions);
    assertThat(
        "The wrong actions were returned.",
        actualActions.get(),
        contains(Iterables.toArray(expectedActions, Action.class)));
  }
}
