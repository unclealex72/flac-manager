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

package uk.co.unclealex.music.command.checkin.covers;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.MusicFileBean;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
/**
 * @author alex
 *
 */
public class ArtworkSearchingServiceCachingTest {

  class TestArtworkSearchingService extends AbstractArtworkSearchingService {
    
    private final Map<String, URI> coverArtUrisByAlbumIdentifier;
    
    private final List<String> calls = Lists.newArrayList();
    
    public TestArtworkSearchingService(Map<String, URI> coverArtUrisByAlbumIdentifier) {
      super();
      this.coverArtUrisByAlbumIdentifier = coverArtUrisByAlbumIdentifier;
    }

    @Override
    protected URI loadArtwork(String albumIdentifier) throws IOException {
      calls.add(albumIdentifier);
      return coverArtUrisByAlbumIdentifier.get(albumIdentifier);
    }
    
    @Override
    protected String locateAlbumIdentifier(MusicFile musicFile) {
      return musicFile.getAsin();
    }
  }

  TestArtworkSearchingService testArtworkSearchingService;
  String firstKnownIdentifier = "123";
  String secondKnownIdentifier = "456";
  String unknownIdentifier = "789";
  URI firstCoverArtUri;
  URI secondCoverArtUri;
  MusicFile firstKnownMusicFile;
  MusicFile secondKnownMusicFile;
  MusicFile unknownMusicFile;
  
  @Before
  public void setup() throws URISyntaxException, IOException {
    firstCoverArtUri = new URI("http://somewhere");
    secondCoverArtUri = new URI("http://somewhere/else");
    firstKnownMusicFile = musicFile(firstKnownIdentifier);
    secondKnownMusicFile = musicFile(secondKnownIdentifier);
    unknownMusicFile = musicFile(unknownIdentifier);
    final Map<String, URI> coverArtUrisByAlbumIdentifier = Maps.newHashMap();
    coverArtUrisByAlbumIdentifier.put(firstKnownIdentifier, firstCoverArtUri);
    coverArtUrisByAlbumIdentifier.put(secondKnownIdentifier, secondCoverArtUri);
    testArtworkSearchingService = new TestArtworkSearchingService(coverArtUrisByAlbumIdentifier);
  }

  protected MusicFile musicFile(String identifier) {
    MusicFile musicFile = new MusicFileBean();
    musicFile.setAsin(identifier);
    return musicFile;
  }
  
  @Test
  public void testNoAnswerIsCached() throws IOException {
    URI firstNullUri = testArtworkSearchingService.findArtwork(unknownMusicFile);
    URI secondNullUri = testArtworkSearchingService.findArtwork(unknownMusicFile);
    assertNull("The first uri call should have been null", firstNullUri);
    assertNull("The first uri call should have been null", secondNullUri);
    assertThat("The wrong calls were made.", testArtworkSearchingService.calls, contains("789"));
  }

  @Test
  public void testSameAnswerIsCached() throws IOException {
    URI firstUri = testArtworkSearchingService.findArtwork(firstKnownMusicFile);
    URI secondUri = testArtworkSearchingService.findArtwork(firstKnownMusicFile);
    assertEquals("The first uri call was incorrect", firstCoverArtUri, firstUri);
    assertEquals("The second uri call was incorrect", firstCoverArtUri, secondUri);
    assertThat("The wrong calls were made.", testArtworkSearchingService.calls, contains("123"));
  }

  @Test
  public void testDifferentAnswersAreNotCached() throws IOException {
    URI firstUri = testArtworkSearchingService.findArtwork(firstKnownMusicFile);
    URI secondUri = testArtworkSearchingService.findArtwork(secondKnownMusicFile);
    assertEquals("The first uri call was incorrect", firstCoverArtUri, firstUri);
    assertEquals("The second uri call was incorrect", secondCoverArtUri, secondUri);
    assertThat("The wrong calls were made.", testArtworkSearchingService.calls, contains("123", "456"));
  }
}
