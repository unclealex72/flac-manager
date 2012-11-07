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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.Test;

import uk.co.unclealex.music.CoverArt;
import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.Validator;
import uk.co.unclealex.music.audio.AudioMusicFile;
import uk.co.unclealex.music.audio.AudioMusicFileFactoryImpl;

import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

/**
 * @author alex
 *
 */
public class ArtworkServiceImplTest {

  @Test
  public void testMp3() throws IOException, URISyntaxException {
    runTest("untagged.mp3");
  }

  @Test
  public void testFlac() throws IOException, URISyntaxException {
    runTest("untagged.flac");
  }
  
  public void runTest(String resourceName) throws IOException, URISyntaxException {
    Validator validator = new Validator() {
      @Override
      public <T> T validate(T object, String message) {
        return object;
      }
    };
    Path musicFile = Files.createTempFile("artwork-serive-impl-test-", "-" + resourceName);
    try {
      InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName);
      Files.copy(in, musicFile, StandardCopyOption.REPLACE_EXISTING);
      in.close();
      final URI coverArtUri = getClass().getClassLoader().getResource("cover.jpg").toURI();
      new ArtworkServiceImpl(new AudioMusicFileFactoryImpl(validator)).addArwork(musicFile, coverArtUri);
      MusicFile audioMusicFile = new AudioMusicFile(musicFile);
      CoverArt actualCoverArt = audioMusicFile.getCoverArt();
      assertEquals("The wrong mime type was returned.", "image/jpeg", actualCoverArt.getMimeType());
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      InputSupplier<InputStream> supplier = new InputSupplier<InputStream>() {
        @Override
        public InputStream getInput() throws IOException {
          return coverArtUri.toURL().openStream();
        }
      };
      ByteStreams.copy(supplier, out);
      assertArrayEquals("The wrong image data was returned.", out.toByteArray(), actualCoverArt.getImageData());
    }
    finally {
      Files.deleteIfExists(musicFile);
    }
  }
}
