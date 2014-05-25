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

package uk.co.unclealex.music.files;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import uk.co.unclealex.music.configuration.JDirectories;
import uk.co.unclealex.music.configuration.json.JPathsBean;

/**
 * @author alex
 * 
 */
public class FileLocationFactoryImplTest {

  JDirectories directories = new JPathsBean(
      Paths.get("/flacPath"),
      Paths.get("/devicesPath"),
      Paths.get("/encodedPath"),
      Paths.get("/stagingPath"));

  JFileLocationFactory fileLocationFactory = new JFileLocationFactoryImpl(directories);

  @Test
  public void testFlac() {
    test(
        true,
        Paths.get("/flacPath"),
        Paths.get("my", "flac", "file.flac"),
        fileLocationFactory.createFlacFileLocation(Paths.get("my", "flac", "file.flac")));
  }

  @Test
  public void testEncoded() {
    test(
        true,
        Paths.get("/encodedPath"),
        Paths.get("my", "encoded", "file.mp3"),
        fileLocationFactory.createEncodedFileLocation(Paths.get("my", "encoded", "file.mp3")));
  }

  @Test
  public void testStaging() {
    test(
        false,
        Paths.get("/stagingPath"),
        Paths.get("my", "flac", "file.flac"),
        fileLocationFactory.createStagingFileLocation(Paths.get("my", "flac", "file.flac")));
  }

  protected void test(boolean expectedReadOnly, Path expectedBasePath, Path expectedRelativePath, JFileLocation actualFileLocation) {
    assertEquals(
        "The wrong file location was created.",
        new JFileLocation(expectedBasePath, expectedRelativePath, expectedReadOnly),
        actualFileLocation);
  }
}
