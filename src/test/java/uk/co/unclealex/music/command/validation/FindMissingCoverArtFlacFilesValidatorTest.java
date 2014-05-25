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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.JMusicFileBean;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.command.checkin.covers.ArtworkSearchingService;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.message.JMessageService;

/**
 * @author alex
 * 
 */
public class FindMissingCoverArtFlacFilesValidatorTest extends FlacFileValidatorTest {

  JFileLocation fileLocation;
  URI uri;
  ArtworkSearchingService artworkSearchingService = Mockito.mock(ArtworkSearchingService.class);
  JMusicFile musicFile;

  @Before
  public void setup() throws URISyntaxException {
    fileLocation = new JFileLocation(Paths.get("/"), Paths.get("queen", "greatest hits", "01 bohemian rhapsody.flac"), true);
    uri = new URI("http://somewhere.com/greatesthits.jpg");
    musicFile = new JMusicFileBean();
    musicFile.setAlbumId("12345");
    musicFilesByFlacPath = Collections.singletonMap(fileLocation, musicFile);
  }
  
  @Test
  public void testNoArtworkRequired() throws IOException {
    JActions actions = actionsSupplier.get().delete(fileLocation);
    runTest(actions, actions);
  }

  @Test
  public void testFoundArtwork() throws IOException {
    Mockito.when(artworkSearchingService.findArtwork(musicFile)).thenReturn(uri);
    JActions actions = actionsSupplier.get().coverArt(fileLocation);
    JActions expectedActions = actionsSupplier.get().addArtwork(fileLocation, uri).coverArt(fileLocation);
    runTest(expectedActions, actions);
  }
  
  @Test
  public void testMissingArtwork() throws IOException {
    Mockito.when(artworkSearchingService.findArtwork(musicFile)).thenReturn(null);
    JActions actions = actionsSupplier.get().coverArt(fileLocation);
    JActions expectedActions = actionsSupplier.get().coverArt(fileLocation).fail(fileLocation, JMessageService.MISSING_ARTWORK);
    runTest(expectedActions, actions);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected JFlacFilesValidator createFlacFilesValidator() {
    return new JFindMissingCoverArtFlacFilesValidator(actionsSupplier, artworkSearchingService);
  }
}
