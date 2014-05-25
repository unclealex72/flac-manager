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
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.action.JActionsImpl;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.message.JMessageService;

/**
 * @author alex
 * 
 */
public class NoOverwritingFlacFilesValidatorTest extends FlacFileValidatorTest {

  JFileLocation existingFileLocation;
  JFileLocation nonExistingFileLocation;

  @Before
  public void setup() throws IOException {
    existingFileLocation =
        new JFileLocation(Paths.get("/"), Paths.get("/").relativize(
            Files.createTempFile("no-overwriting-flac-files-validator-", ".flac")), true);
    nonExistingFileLocation =
        new JFileLocation(Paths.get("/"), Paths.get("/").relativize(
            Files.createTempFile("no-overwriting-flac-files-validator-", ".flac")), true);
    Files.delete(nonExistingFileLocation.resolve());
  }

  @Test
  public void testNonExisting() throws IOException {
    JActions actions =
        new JActionsImpl().move(existingFileLocation, nonExistingFileLocation);
    runTest(actions, actions);
  }

  @Test
  public void testExisting() throws IOException {
    JActions actions =
        new JActionsImpl().move(nonExistingFileLocation, existingFileLocation);
    JActions expectedActions =
        new JActionsImpl()
            .move(nonExistingFileLocation, existingFileLocation)
            .fail(nonExistingFileLocation, JMessageService.OVERWRITE, existingFileLocation);
    runTest(expectedActions, actions);
  }

  @After
  public void delete() throws IOException {
    for (JFileLocation fileLocation : new JFileLocation[] { existingFileLocation, nonExistingFileLocation }) {
      Files.deleteIfExists(fileLocation.resolve());
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected JFlacFilesValidator createFlacFilesValidator() {
    return new JNoOverwritingFlacFilesValidator();
  }
}
