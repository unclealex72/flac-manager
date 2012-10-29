/**
 * Copyright 2011 Alex Jones
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

/**
 * @author alex
 * 
 */
public class FileUtilsImplTest {

  private static final Logger log = LoggerFactory.getLogger(FileUtilsImplTest.class);

  Path testDirectory;

  @Before
  public void createTestDirectory() throws IOException {
    removeTestDirectory();
    testDirectory = Files.createTempDirectory("file-utils-impl-test-");
    log.info("Using directory " + testDirectory);
  }

  @Test
  public void testReadOnly() throws IOException {
    Path newFile = testDirectory.resolve(Paths.get("a", "good", "time.txt"));
    Files.createDirectories(newFile.getParent());
    Files.createFile(newFile);
    new FileUtilsImpl().alterWriteable(testDirectory, newFile, false);
    for (Path path : new Path[] {
        testDirectory,
        testDirectory.resolve(Paths.get("a")),
        testDirectory.resolve(Paths.get("a", "good")),
        testDirectory.resolve(Paths.get("a", "good", "time.txt")) }) {
      Assert.assertFalse("Path " + path + " is not writeable", path.toFile().canWrite());
    }
  }

  @Test
  public void testReadWrite() throws IOException {
    Path newFile = testDirectory.resolve(Paths.get("a", "good", "time.txt"));
    Files.createDirectories(newFile.getParent());
    Files.createFile(newFile);
    new FileUtilsImpl().alterWriteable(testDirectory, newFile, false);
    new FileUtilsImpl().alterWriteable(testDirectory, newFile, true);
    for (Path path : new Path[] {
        testDirectory,
        testDirectory.resolve(Paths.get("a")),
        testDirectory.resolve(Paths.get("a", "good")),
        testDirectory.resolve(Paths.get("a", "good", "time.txt")) }) {
      Assert.assertTrue("Path " + path + " is writeable", path.toFile().canWrite());
    }
  }

  @Test
  public void testMoveWithSiblings() throws IOException {
    Path source = testDirectory.resolve("source");
    Path target = testDirectory.resolve("target");
    Files.createDirectories(target);
    Path fileToMove = source.resolve(Paths.get("dir", "moveme.txt"));
    Path fileToKeep = source.resolve(Paths.get("dir", "keepme.txt"));
    for (Path p : new Path[] { fileToMove, fileToKeep }) {
      Files.createDirectories(p.getParent());
      Files.createFile(p);
    }
    new FileUtilsImpl().move(source, source.relativize(fileToMove), target);
    Assert.assertTrue(
        "File target/dir/moveme.txt does not exist.",
        Files.exists(target.resolve(Paths.get("dir", "moveme.txt"))));
    Assert.assertFalse(
        "File target/dir/moveme.txt is a directory.",
        Files.isDirectory(target.resolve(Paths.get("dir", "moveme.txt"))));
    Assert.assertTrue("File source/dir/keepme.txt does not exist.", Files.exists(fileToKeep));
    Assert.assertFalse("File source/dir/moveme.txt exists.", Files.exists(fileToMove));
  }

  @Test
  public void testMoveWithoutSiblings() throws IOException {
    Path source = testDirectory.resolve("source");
    Path target = testDirectory.resolve("target");
    Files.createDirectories(target);
    Path fileToMove = source.resolve(Paths.get("dir", "moveme.txt"));
    Files.createDirectories(fileToMove.getParent());
    Files.createFile(fileToMove);
    new FileUtilsImpl().move(source, source.relativize(fileToMove), target);
    Assert.assertTrue(
        "File target/dir/moveme.txt does not exist.",
        Files.exists(target.resolve(Paths.get("dir", "moveme.txt"))));
    Assert.assertFalse(
        "File target/dir/moveme.txt is a directory.",
        Files.isDirectory(target.resolve(Paths.get("dir", "moveme.txt"))));
    Assert.assertFalse("File source/dir exists.", Files.exists(fileToMove.getParent()));
    Assert.assertTrue("File source does not exist.", Files.exists(source));
  }

  @Test
  public void testFindFlacFiles() throws IOException {
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
    SortedSet<Path> actualFlacFiles = new FileUtilsImpl().findAllFlacFiles(testDirectory);
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
