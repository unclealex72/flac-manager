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

package uk.co.unclealex.music.action;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.JMusicFileBean;
import uk.co.unclealex.music.files.JFileLocation;

/**
 * @author alex
 * 
 */
public class ActionsImplTest {

  @Test
  public void testBuildActions() {
    JActions actions = new JActionsImpl();
    JMusicFile flacMusicFile = new JMusicFileBean();
    flacMusicFile.setAlbum("Dummy");
    List<JAction> actualActions =
        actions
            .delete(fileLocation("delete"))
            .encode(fileLocation("flacEncode"), fileLocation("mp3encode"), flacMusicFile)
            .fail(fileLocation("fail"), "D'Oh!", 1, 2)
            .move(fileLocation("from"), fileLocation("to"))
            .get();
    assertThat("The wrong actions were generated", actualActions, contains(new JAction[] {
        new JDeleteAction(fileLocation("delete")),
        new JEncodeAction(fileLocation("flacEncode"), fileLocation("mp3encode"), flacMusicFile),
        new JFailureAction(fileLocation("fail"), "D'Oh!", 1, 2),
        new JMoveAction(fileLocation("from"), fileLocation("to")) }));
  }

  protected JFileLocation fileLocation(String path) {
    return new JFileLocation(Paths.get("/dummy"), Paths.get(path), false);
  }
}
