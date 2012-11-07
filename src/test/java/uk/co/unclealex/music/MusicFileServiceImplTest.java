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

package uk.co.unclealex.music;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.validation.ConstraintViolationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.co.unclealex.music.audio.AudioMusicFile;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author alex
 * 
 */
public class MusicFileServiceImplTest {

  Path mp3File;

  @Before
  public void setup() throws IOException {
    mp3File = Files.createTempFile("music-file-service-", ".mp3");
  }

  @Test
  public void testCopyMp3() throws URISyntaxException, ConstraintViolationException, IOException {
    try (InputStream in = getClass().getClassLoader().getResourceAsStream("untagged.mp3")) {
      Files.copy(in, mp3File, StandardCopyOption.REPLACE_EXISTING);
    }
    MusicFile expected =
        new ObjectMapper()
            .reader(MusicFileBean.class)
            .readValue(getClass().getClassLoader().getResource("tagged.json"));
    MusicFile mp3File = new AudioMusicFile(this.mp3File);
    MusicFileService musicFileService = new MusicFileServiceImpl();
    MusicFileBean actual = new MusicFileBean();
    musicFileService.transfer(expected, mp3File);
    musicFileService.transfer(mp3File, actual);
    Assert.assertEquals("The wrong tags were returned.", expected.toString(), actual.toString());
  }

  @After
  public void delete() throws IOException {
    Files.deleteIfExists(mp3File);
  }
}
