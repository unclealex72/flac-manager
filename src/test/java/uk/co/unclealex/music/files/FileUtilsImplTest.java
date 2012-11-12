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

package uk.co.unclealex.music.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.files.FileUtilsImpl;

/**
 * @author alex
 * 
 */
public class FileUtilsImplTest {

  private static final Logger log = LoggerFactory.getLogger(FileUtilsImplTest.class);

  Path testDirectory;

  @Before
  public void createTestDirectory() throws IOException {
    testDirectory = Files.createTempDirectory("file-utils-impl-test-");
    log.info("Using directory " + testDirectory);
  }

  @Test
  public void testReadOnly() throws IOException {
    FileLocation newFile = new FileLocation(testDirectory, Paths.get("a", "good", "time.txt"));
    Files.createDirectories(newFile.resolve().getParent());
    Files.createFile(newFile.resolve());
    new FileUtilsImpl().alterWriteable(newFile, false);
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
    FileLocation newFile = new FileLocation(testDirectory, Paths.get("a", "good", "time.txt"));
    Files.createDirectories(newFile.resolve().getParent());
    Files.createFile(newFile.resolve());
    new FileUtilsImpl().alterWriteable(newFile, false);
    new FileUtilsImpl().alterWriteable(newFile, true);
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
    FileLocation fileToMove = new FileLocation(source, Paths.get("dir", "moveme.txt"));
    FileLocation fileToKeep = new FileLocation(source, Paths.get("dir", "keepme.txt"));
    for (FileLocation fl : new FileLocation[] { fileToMove, fileToKeep }) {
      Files.createDirectories(fl.resolve().getParent());
      Files.createFile(fl.resolve());
    }
    new FileUtilsImpl().move(fileToMove, new FileLocation(target, Paths.get("otherdir", "movedme.txt")));
    Assert.assertTrue(
        "File target/otherdir/movedme.txt does not exist.",
        Files.exists(target.resolve(Paths.get("otherdir", "movedme.txt"))));
    Assert.assertFalse(
        "File target/otherdir/movedme.txt is a directory.",
        Files.isDirectory(target.resolve(Paths.get("otherdir", "movedme.txt"))));
    Assert.assertTrue("File source/dir/keepme.txt does not exist.", Files.exists(fileToKeep.resolve()));
    Assert.assertFalse("File source/dir/moveme.txt exists.", Files.exists(fileToMove.resolve()));
  }

  public void testLink() throws IOException {
    FileLocation targetLocation = new FileLocation(testDirectory, "here.txt");
    Files.createFile(targetLocation.resolve());
    FileLocation linkLocation = new FileLocation(testDirectory, "link.d", "link.txt");
    new FileUtilsImpl().link(targetLocation, linkLocation);
    Assert.assertTrue("The newly created link was not a symbolic link.", Files.isSymbolicLink(linkLocation.resolve()));
    Assert.assertEquals(
        "The newly created link does not point to the correct file.",
        targetLocation.resolve(),
        Files.readSymbolicLink(linkLocation.resolve()));
  }

  @Test
  public void testMoveWithoutSiblings() throws IOException {
    Path source = testDirectory.resolve("source");
    Path target = testDirectory.resolve("target");
    Files.createDirectories(target);
    FileLocation fileToMove = new FileLocation(source, Paths.get("dir", "moveme.txt"));
    Files.createDirectories(fileToMove.resolve().getParent());
    Files.createFile(fileToMove.resolve());
    new FileUtilsImpl().move(fileToMove, new FileLocation(target, Paths.get("otherdir", "movedme.txt")));
    Assert.assertTrue(
        "File target/otherdir/movemed.txt does not exist.",
        Files.exists(target.resolve(Paths.get("otherdir", "movedme.txt"))));
    Assert.assertFalse(
        "File target/otherdir/movedme.txt is a directory.",
        Files.isDirectory(target.resolve(Paths.get("otherdir", "movedme.txt"))));
    Assert.assertFalse("File source/dir exists.", Files.exists(fileToMove.resolve().getParent()));
    Assert.assertTrue("File source does not exist.", Files.exists(source));
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
