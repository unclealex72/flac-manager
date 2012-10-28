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

package uk.co.unclealex.music.common.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import org.hamcrest.Matchers;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.co.unclealex.music.common.CoverArt;
import uk.co.unclealex.music.common.MusicFile;
import uk.co.unclealex.music.common.Validator;
import uk.co.unclealex.music.common.audio.AudioMusicFileFactoryImpl;
import uk.co.unclealex.music.violations.Violation;

import com.google.common.io.ByteStreams;

/**
 * @author alex
 * 
 */
public class AudioMusicFileFactoryImplTest {

  Path tempMusicFile;

  @Before
  public void setup() throws IOException {
    tempMusicFile = Files.createTempFile("audio-music-file-", ".flac");
  }

  @After
  public void tearDown() throws IOException {
    if (tempMusicFile != null) {
      Files.deleteIfExists(tempMusicFile);
    }
  }

  @Test
  public void testUntagged() throws IOException {
    try {
      load("untagged.flac");
      Assert.fail("An invalid music file was not found to be invalid.");
    }
    catch (ConstraintViolationException e) {
      Violation[] expectedViolations = new Violation[] {
          Violation.expect(NotEmpty.class,"albumArtistSort"),
          Violation.expect(NotNull.class,"totalDiscs"),
          Violation.expect(NotEmpty.class,"albumId"),
          Violation.expect(NotNull.class,"trackNumber"),
          Violation.expect(NotEmpty.class,"title"),
          Violation.expect(NotEmpty.class,"artistId"),
          Violation.expect(NotEmpty.class,"albumArtist"),
          Violation.expect(NotEmpty.class,"albumArtistId"),
          Violation.expect(NotEmpty.class,"artist"),
          Violation.expect(NotNull.class,"totalTracks"),
          Violation.expect(NotEmpty.class,"trackId"),
          Violation.expect(NotEmpty.class,"album"),
          Violation.expect(NotEmpty.class,"artistSort"),
          Violation.expect(NotNull.class,"discNumber")          
      };
      
      Assert.assertThat(
          "The wrong violations were reported.",
          Violation.untypedViolations(e.getConstraintViolations()),
          Matchers.containsInAnyOrder(expectedViolations));
    }
  }

  @Test
  public void testTagged() throws IOException {
    MusicFile musicFile = load("tagged.flac");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (InputStream in = getClass().getClassLoader().getResourceAsStream("cover.jpg")) {
      ByteStreams.copy(in, out);
    }
    CoverArt expectedCoverArt = new CoverArt(out.toByteArray(), "image/jpeg");
    Assert.assertEquals("The music file has the wrong album.", "Metal: A Headbanger's Companion", musicFile.getAlbum());
    Assert.assertEquals("The music file has the wrong album artist.", "Various Artists", musicFile.getAlbumArtist());
    Assert.assertEquals(
        "The music file has the wrong album artist ID.",
        "89ad4ac3-39f7-470e-963a-56509c546377",
        musicFile.getAlbumArtistId());
    Assert.assertEquals(
        "The music file has the wrong album artist sort.",
        "Various Artists Sort",
        musicFile.getAlbumArtistSort());
    Assert.assertEquals(
        "The music file has the wrong album ID.",
        "6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e",
        musicFile.getAlbumId());
    Assert.assertEquals("The music file has the wrong artist.", "Napalm Death", musicFile.getArtist());
    Assert.assertEquals(
        "The music file has the wrong artist ID.",
        "ce7bba8b-026b-4aa6-bddb-f98ed6d595e4",
        musicFile.getArtistId());
    Assert.assertEquals("The music file has the wrong artist sort.", "Napalm Death Sort", musicFile.getArtistSort());
    Assert.assertEquals("The music file has the wrong Amazon ID.", "B000Q66HUA", musicFile.getAsin());
    Assert.assertEquals("The music file has the wrong title.", "Suffer The Children", musicFile.getTitle());
    Assert.assertEquals(
        "The music file has the wrong track ID.",
        "5b0ef8e9-9b55-4a3e-aca6-d816d6bbc00f",
        musicFile.getTrackId());
    Assert.assertEquals("The music file has the wrong cover art.", expectedCoverArt, musicFile.getCoverArt());
    Assert.assertEquals("The music file has the wrong disc number.", 1, musicFile.getDiscNumber().intValue());
    Assert.assertEquals("The music file has the wrong number of discs.", 6, musicFile.getTotalDiscs().intValue());
    Assert.assertEquals("The music file has the wrong number of tracks.", 17, musicFile.getTotalTracks().intValue());
    Assert.assertEquals("The music file has the wrong track number.", 3, musicFile.getTrackNumber().intValue());

  }

  public MusicFile load(String resourceName) throws IOException {
    URL resource = getClass().getClassLoader().getResource(resourceName);
    try (InputStream in = resource.openStream()) {
      Files.copy(in, tempMusicFile, StandardCopyOption.REPLACE_EXISTING);
    }
    return new AudioMusicFileFactoryImpl(new Validator()).load(tempMusicFile);
  }
}
