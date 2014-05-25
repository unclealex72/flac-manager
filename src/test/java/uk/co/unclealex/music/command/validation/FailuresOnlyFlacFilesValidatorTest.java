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
import java.nio.file.Paths;

import org.junit.Test;

import uk.co.unclealex.music.JMusicFileBean;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.action.JActionsImpl;
import uk.co.unclealex.music.files.JFileLocation;

/**
 * @author alex
 * 
 */
public class FailuresOnlyFlacFilesValidatorTest extends FlacFileValidatorTest {

  JFileLocation fl = new JFileLocation(Paths.get("/"), Paths.get("dummy"), true);

  @Test
  public void testIncludingFailure() throws IOException {
    JActions actions =
        new JActionsImpl()
            .coverArt(fl)
            .delete(fl)
            .encode(fl, fl, new JMusicFileBean())
            .fail(fl, "messageTemplate", 1, 2)
            .move(fl, fl)
            .fail(fl, "otherMessageTemplate", 3, 4);
    JActions expectedActions =
        new JActionsImpl().fail(fl, "messageTemplate", 1, 2).fail(fl, "otherMessageTemplate", 3, 4);
    runTest(expectedActions, actions);
  }

  @Test
  public void testNoFailure() throws IOException {
    JActions actions =
        new JActionsImpl()
            .coverArt(fl)
            .delete(fl)
            .encode(fl, fl, new JMusicFileBean())
            .move(fl, fl);
    runTest(actions, actions);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected JFlacFilesValidator createFlacFilesValidator() {
    return new JFailuresOnlyFlacFilesValidator(actionsSupplier.get());
  }
}
