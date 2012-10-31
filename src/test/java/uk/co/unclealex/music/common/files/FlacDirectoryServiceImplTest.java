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

package uk.co.unclealex.music.common.files;

import static org.hamcrest.Matchers.contains;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.common.exception.InvalidDirectoriesException;

import com.google.common.collect.Lists;

/**
 * @author alex
 * 
 */
public class FlacDirectoryServiceImplTest {

  private static final Logger log = LoggerFactory.getLogger(FlacDirectoryServiceImplTest.class);

  Path testDirectory;
  FlacDirectoryServiceImpl flacDirectoryServiceImpl;

  @Before
  public void createRepository() throws IOException {
    testDirectory = Files.createTempDirectory("flac-directory-service-impl-test-");
    flacDirectoryServiceImpl = new FlacDirectoryServiceImpl(FileSystems.getDefault());
    log.info("Using directory " + testDirectory);
    for (Path path : new Path[] {
        Paths.get("dir.flac", "myfile.flac"),
        Paths.get("dir.flac", "myfile.xml"),
        Paths.get("my.flac"),
        Paths.get("my.xml"),
        Paths.get("dir", "your.flac"),
        Paths.get("dir", "your.mp3") }) {
      Path fullPath = testDirectory.resolve(path);
      Files.createDirectories(fullPath.getParent());
      Files.createFile(fullPath);
    }
  }

  @Test
  public void testListFlacFilesSuccess() throws InvalidDirectoriesException, IOException {
    SortedSet<Path> actualFlacFiles =
        flacDirectoryServiceImpl.listFlacFiles(
            testDirectory,
            Lists.newArrayList(testDirectory.resolve("dir.flac"), testDirectory.resolve("dir")));
    Assert.assertThat(
        "The wrong flac files were found.",
        actualFlacFiles,
        contains(
            testDirectory.resolve(Paths.get("dir.flac", "myfile.flac")),
            testDirectory.resolve(Paths.get("dir", "your.flac"))));
  }

  @Test
  public void testListFlacFilesFail() throws IOException {
    try {
      flacDirectoryServiceImpl.listFlacFiles(
          testDirectory,
          Lists.newArrayList(
              testDirectory.resolve("dir.flac"),
              testDirectory.getParent(),
              testDirectory.resolve("my.xml")));
      Assert.fail("Invalid directories did not fail.");
    }
    catch (InvalidDirectoriesException e) {
      Assert.assertThat(
          "The wrong files were marked as invalid.",
          e.getInvalidDirectories(),
          contains(
              testDirectory.getParent(),
              testDirectory.resolve("my.xml")));
    }
  }

  @Test
  public void testFindFlacFiles() throws IOException {
    SortedSet<Path> actualFlacFiles = flacDirectoryServiceImpl.findAllFlacFiles(testDirectory);
    Assert.assertThat(
        "The wrong flac files were found.",
        actualFlacFiles,
        contains(
            testDirectory.resolve(Paths.get("dir.flac", "myfile.flac")),
            testDirectory.resolve(Paths.get("dir", "your.flac")),
            testDirectory.resolve("my.flac")));
  }

  @After
  public void removeTestDirectory() throws IOException {
    if (testDirectory != null) {
      removeRecurisvely(testDirectory.toFile());
    }
  }

  protected void removeRecurisvely(File f) throws IOException {
    f.setWritable(true);
    if (f.isDirectory()) {
      for (File child : f.listFiles()) {
        removeRecurisvely(child);
      }
    }
    f.delete();
  }

}
