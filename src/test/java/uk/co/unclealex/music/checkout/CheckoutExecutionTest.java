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

package uk.co.unclealex.music.checkout;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.co.unclealex.music.common.command.Execution;
import uk.co.unclealex.music.common.configuration.Configuration;
import uk.co.unclealex.music.common.configuration.json.ConfigurationBean;
import uk.co.unclealex.music.common.configuration.json.PathsBean;
import uk.co.unclealex.music.common.exception.InvalidDirectoriesException;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.google.inject.Guice;

/**
 * @author alex
 * 
 */
public class CheckoutExecutionTest {

  Path tempDir;
  Path flacPath;
  Path stagingPath;
  Execution execution;

  @Before
  public void setUp() throws IOException {
    tempDir = Files.createTempDirectory("checkout-execution-test-");
    flacPath = path(tempDir, "flac");
    stagingPath = path(tempDir, "staging");
    Files.createDirectories(stagingPath);
    Path[] flacFiles =
        new Path[] {
            path(flacPath, "m", "muse", "absolution", "01 intro.flac"),
            path(flacPath, "m", "muse", "absolution", "02 apocalypse please.flac"),
            path(flacPath, "q", "queen", "queen", "01 keep yourself alive.flac"),
            path(flacPath, "q", "queen", "queen", "02 doing all right.flac"),
            path(flacPath, "q", "queen", "queen ii", "01 procession.flac"),
            path(flacPath, "q", "queen", "queen ii", "02 father to son.flac") };
    for (Path flacFile : flacFiles) {
      Files.createDirectories(flacFile.getParent());
      Files.createFile(flacFile);
    }
    FileVisitor<Path> readOnlyVisitor = new SimpleFileVisitor<Path>() {

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        readOnly(file);
        return super.visitFile(file, attrs);
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        readOnly(dir);
        return super.postVisitDirectory(dir, exc);
      }

      protected void readOnly(Path file) throws IOException {
        Set<PosixFilePermission> posixFilePermissions = Files.getPosixFilePermissions(file);
        posixFilePermissions.removeAll(Arrays.asList(
            PosixFilePermission.OWNER_WRITE,
            PosixFilePermission.GROUP_WRITE,
            PosixFilePermission.OTHERS_WRITE));
        Files.setPosixFilePermissions(file, posixFilePermissions);
      }
    };
    Files.walkFileTree(flacPath, readOnlyVisitor);
    PathsBean directories = new PathsBean(flacPath, null, null, stagingPath);
    final ConfigurationBean configurationBean = new ConfigurationBean(directories, null, null);
    CheckoutModule checkoutModule = new CheckoutModule() {
      @Override
      protected void bindConfiguration() {
        bind(Configuration.class).toInstance(configurationBean);
      }
    };
    execution = Guice.createInjector(checkoutModule).getInstance(Execution.class);
  }

  @Test
  public void testCheckoutQueenII() throws IOException, InvalidDirectoriesException {
    Path[] expectedRemainingFlacFiles =
        new Path[] {
            path(flacPath),
            path(flacPath, "m"),
            path(flacPath, "m", "muse"),
            path(flacPath, "m", "muse", "absolution"),
            path(flacPath, "m", "muse", "absolution", "01 intro.flac"),
            path(flacPath, "m", "muse", "absolution", "02 apocalypse please.flac"),
            path(flacPath, "q"),
            path(flacPath, "q", "queen"),
            path(flacPath, "q", "queen", "queen"),
            path(flacPath, "q", "queen", "queen", "01 keep yourself alive.flac"),
            path(flacPath, "q", "queen", "queen", "02 doing all right.flac") };
    Path[] expectedCheckedOutFiles =
        new Path[] {
            path(stagingPath),
            path(stagingPath, "q"),
            path(stagingPath, "q", "queen"),
            path(stagingPath, "q", "queen", "queen ii"),
            path(stagingPath, "q", "queen", "queen ii", "01 procession.flac"),
            path(stagingPath, "q", "queen", "queen ii", "02 father to son.flac") };
    testCheckout(expectedRemainingFlacFiles, expectedCheckedOutFiles, path(flacPath, "q", "queen", "queen ii"));
  }

  @Test
  public void testCheckoutQueenAndQueenII() throws IOException, InvalidDirectoriesException {
    Path[] expectedRemainingFlacFiles =
        new Path[] {
            path(flacPath),
            path(flacPath, "m"),
            path(flacPath, "m", "muse"),
            path(flacPath, "m", "muse", "absolution"),
            path(flacPath, "m", "muse", "absolution", "01 intro.flac"),
            path(flacPath, "m", "muse", "absolution", "02 apocalypse please.flac") };
    Path[] expectedCheckedOutFiles =
        new Path[] {
            path(stagingPath),
            path(stagingPath, "q"),
            path(stagingPath, "q", "queen"),
            path(stagingPath, "q", "queen", "queen"),
            path(stagingPath, "q", "queen", "queen", "01 keep yourself alive.flac"),
            path(stagingPath, "q", "queen", "queen", "02 doing all right.flac"),
            path(stagingPath, "q", "queen", "queen ii"),
            path(stagingPath, "q", "queen", "queen ii", "01 procession.flac"),
            path(stagingPath, "q", "queen", "queen ii", "02 father to son.flac") };
    testCheckout(
        expectedRemainingFlacFiles,
        expectedCheckedOutFiles,
        path(flacPath, "q", "queen", "queen ii"),
        path(flacPath, "q", "queen", "queen"));
  }

  @Test
  public void testCheckoutMuse() throws IOException, InvalidDirectoriesException {
    Path[] expectedRemainingFlacFiles =
        new Path[] {
            path(flacPath),
            path(flacPath, "q"),
            path(flacPath, "q", "queen"),
            path(flacPath, "q", "queen", "queen"),
            path(flacPath, "q", "queen", "queen", "01 keep yourself alive.flac"),
            path(flacPath, "q", "queen", "queen", "02 doing all right.flac"),
            path(flacPath, "q", "queen", "queen ii"),
            path(flacPath, "q", "queen", "queen ii", "01 procession.flac"),
            path(flacPath, "q", "queen", "queen ii", "02 father to son.flac") };
    Path[] expectedCheckedOutFiles =
        new Path[] {
            path(stagingPath),
            path(stagingPath, "m"),
            path(stagingPath, "m", "muse"),
            path(stagingPath, "m", "muse", "absolution"),
            path(stagingPath, "m", "muse", "absolution", "01 intro.flac"),
            path(stagingPath, "m", "muse", "absolution", "02 apocalypse please.flac") };
    testCheckout(expectedRemainingFlacFiles, expectedCheckedOutFiles, path(flacPath, "m", "muse"));
  }

  /**
   * @param expectedRemainingFlacFiles
   * @param expectedCheckedOutFiles
   * @param path
   * @throws InvalidDirectoriesException
   * @throws IOException
   */
  protected void testCheckout(Path[] expectedRemainingFlacFiles, Path[] expectedCheckedOutFiles, Path... paths)
      throws IOException,
      InvalidDirectoriesException {
    execution.execute(Arrays.asList(paths));
    SortedSet<Path> actualRemainingFlacFiles = listFiles(flacPath);
    assertThat(
        "The wrong flac files were left in the flac directory.",
        actualRemainingFlacFiles,
        containsInAnyOrder(expectedRemainingFlacFiles));
    SortedSet<Path> actualCheckedOutFiles = listFiles(stagingPath);
    assertThat(
        "The wrong flac files were moved into the staging directory.",
        actualCheckedOutFiles,
        containsInAnyOrder(expectedCheckedOutFiles));
    Predicate<Path> canWritePredicate = new Predicate<Path>() {
      public boolean apply(Path path) {
        return Files.isWritable(path);
      }
    };
    checkPath(flacPath, Predicates.not(canWritePredicate), "read only");
    checkPath(stagingPath, canWritePredicate, "writable");
  }

  protected SortedSet<Path> listFiles(Path path) throws IOException {
    final SortedSet<Path> paths = Sets.newTreeSet();
    FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        paths.add(dir);
        return super.preVisitDirectory(dir, attrs);
      }

      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        paths.add(file);
        return super.visitFile(file, attrs);
      }
    };
    Files.walkFileTree(path, visitor);
    return paths;
  }

  protected void checkPath(Path path, final Predicate<Path> predicate, final String predicateName) throws IOException {
    FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        checkPath(dir);
        return super.preVisitDirectory(dir, attrs);
      }

      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        checkPath(file);
        return super.visitFile(file, attrs);
      }

      protected void checkPath(Path file) {
        Assert.assertTrue("File " + file + " failed the " + predicateName + " test ", predicate.apply(file));
      }
    };
    Files.walkFileTree(path, visitor);
  }

  protected Path path(Path parent, String... parts) {
    Path path = parent;
    for (String part : parts) {
      path = path.resolve(part);
    }
    return path;
  }

  @After
  public void tearDown() throws IOException {
    if (tempDir != null && Files.exists(tempDir)) {
      FileVisitor<Path> writableVisitor = new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          delete(file);
          return super.visitFile(file, attrs);
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          delete(dir);
          return super.postVisitDirectory(dir, exc);
        }

        protected void delete(Path file) throws IOException {
          Set<PosixFilePermission> posixFilePermissions = Files.getPosixFilePermissions(file);
          posixFilePermissions.add(PosixFilePermission.OWNER_WRITE);
          Files.setPosixFilePermissions(file, posixFilePermissions);
        }
      };
      Files.walkFileTree(flacPath, writableVisitor);
      FileUtils.deleteDirectory(tempDir.toFile());
    }
  }

}
