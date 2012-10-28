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

package uk.co.unclealex.music.audio;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.unclealex.music.common.MusicFile;
import uk.co.unclealex.music.common.Validator;

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
    MusicFile musicFile = load("untagged.flac");
    Assert.fail("Booo!");
  }

  public MusicFile load(String resourceName) throws IOException {
    URL resource = getClass().getClassLoader().getResource(resourceName);
    try (InputStream in = resource.openStream()) {
      Files.copy(in, tempMusicFile, StandardCopyOption.REPLACE_EXISTING);
    }
    return new AudioMusicFileFactoryImpl(new Validator()).load(tempMusicFile);
  }
}
