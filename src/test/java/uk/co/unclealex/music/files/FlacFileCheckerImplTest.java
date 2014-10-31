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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author alex
 *
 */
public class FlacFileCheckerImplTest {

  @Test
  public void testFlac() throws IOException {
    runTest("untagged.flac", true);
  }

  @Test
  public void testMp3() throws IOException {
    runTest("untagged.mp3", false);
  }

  @Test
  public void testJson() throws IOException {
    runTest("configuration.json", false);
  }
  
  protected void runTest(String resourceName, boolean expectedResult) throws IOException {
    Path path = Files.createTempFile("flac-file-checker-test-", ".apj");
    try {
      try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName)) {
        Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
      }
      boolean result = new FlacFileCheckerImpl().isFlacFile(path);
      Assert.assertEquals("The wrong result was returned whilst checking whether " + resourceName + " is a flac file.", expectedResult, result);
    }
    finally {
      Files.deleteIfExists(path);
    }
  }
}
