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
import java.util.Arrays;

import org.junit.Test;

import uk.co.unclealex.music.MusicFileBean;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.ActionsImpl;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.message.MessageService;

import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
public class UniqueFlacFilesValidatorTest extends FlacFileValidatorTest {

  FileLocation fl1 = new FileLocation(Paths.get("/"), Paths.get("1"), true);
  FileLocation fl2 = new FileLocation(Paths.get("/"), Paths.get("2"), true);
  FileLocation fl3 = new FileLocation(Paths.get("/"), Paths.get("3"), true);
  FileLocation fl4 = new FileLocation(Paths.get("/"), Paths.get("4"), true);

  @Test
  public void testAllUnqiue() throws IOException {
    Actions actions = new ActionsImpl().encode(fl1, fl2, new MusicFileBean()).move(fl1, fl3).delete(fl4);
    runTest(actions, actions);
  }

  @Test
  public void testNonUnqiue() throws IOException {
    Actions actions =
        new ActionsImpl().encode(fl1, fl3, new MusicFileBean()).move(fl2, fl3).move(fl1, fl4).move(fl2, fl4);
    Actions expectedActions =
        new ActionsImpl()
            .encode(fl1, fl3, new MusicFileBean())
            .move(fl2, fl3)
            .move(fl1, fl4)
            .move(fl2, fl4)
            .fail(fl3, MessageService.NON_UNIQUE, Sets.newTreeSet(Arrays.asList(fl1, fl2)))
            .fail(fl4, MessageService.NON_UNIQUE, Sets.newTreeSet(Arrays.asList(fl1, fl2)));
    runTest(expectedActions, actions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected FlacFilesValidator createFlacFilesValidator() {
    return new UniqueFlacFilesValidator();
  }
}
