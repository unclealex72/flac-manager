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
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.SortedSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

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

	@After
	public void removeTestDirectory() {
		if (testDirectory != null) {
			org.apache.commons.io.FileUtils.deleteQuietly(testDirectory.toFile());
		}
	}

	@Test
	public void testCreateUpdateTimeFunction() throws IOException {
		Path newPath = testDirectory.resolve("one");
		Files.createFile(newPath);
		BasicFileAttributes expectedAttributes = Files.readAttributes(newPath, BasicFileAttributes.class,
				LinkOption.NOFOLLOW_LINKS);
		long expectedLastModifiedTime = expectedAttributes.lastModifiedTime().toMillis();
		long actualLastModifiedTime = new FileUtilsImpl().createUpdateTimeFunction().apply(newPath);
		Assert.assertEquals("The wrong modify time was returned.", expectedLastModifiedTime, actualLastModifiedTime);
	}

	@Test
	public void testFileHasExtensionPredicateCorrectExtension() {
		testFileHasExtensionPredicate("mp3", true, "mnt", "multimedia", "mp3", "file.mp3");
	}

	@Test
	public void testFileHasExtensionPredicateIncorrectExtension() {
		testFileHasExtensionPredicate("mp3", false, "mnt", "multimedia", "mp3", "file.flac");
	}

	@Test
	public void testFileHasExtensionPredicateMultipleDots() {
		testFileHasExtensionPredicate("mp3", true, "mnt", "multimedia", "mp3", "file.something.mp3");
	}

	@Test
	public void testFileHasExtensionPredicateNoExtension() {
		testFileHasExtensionPredicate("mp3", false, "mnt", "multimedia", "mp3", "filemp3");
	}

	protected void testFileHasExtensionPredicate(String extension, boolean expectedResult, String component,
			String... components) {
		Predicate<Path> predicate = new FileUtilsImpl().createFileHasExtensionPredicate(extension);
		Path path = FileSystems.getDefault().getPath(component, components);
		boolean actualResult = predicate.apply(path);
		Assert.assertEquals("The path " + path + " did not test correctly for extension " + extension, expectedResult,
				actualResult);
	}

	@Test
	public void testFilenameWithSuffixWithoutSuffix() {
		testFilenameWithoutSuffix("file", "mnt", "multimedia", "flac", "file.flac");
	}

	@Test
	public void testFilenameWithNoSuffixWithoutSuffix() {
		testFilenameWithoutSuffix("file", "mnt", "multimedia", "flac", "file");
	}

	@Test
	public void testFilenameWithDotButNoSuffixWithoutSuffix() {
		testFilenameWithoutSuffix("file", "mnt", "multimedia", "flac", "file.");
	}

	@Test
	public void testFilenameWithMultipleDotsWithoutSuffix() {
		testFilenameWithoutSuffix("file.something", "mnt", "multimedia", "flac", "file.something.mp3");
	}

	protected void testFilenameWithoutSuffix(String expectedResult, String component, String... components) {
		Path path = FileSystems.getDefault().getPath(component, components);
		String actualResult = new FileUtilsImpl().filenameWithoutSuffix(path);
		Assert.assertEquals("Path " + path + " had the wrong filename without suffix", expectedResult, actualResult);
	}

	/**
	 * Test method for
	 * {@link uk.co.unclealex.music.files.FileUtilsImpl#deleteDirectory(java.nio.file.Path)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDeleteDirectory() throws IOException {
		Path nonEmptyDirectory = testDirectory.resolve("non-empty-directory");
		Files.createDirectory(nonEmptyDirectory);
		Files.createFile(nonEmptyDirectory.resolve("file"));
		for (int idx = 1; idx <= 2; idx++) {
			new FileUtilsImpl().deleteDirectory(testDirectory);
			Assert.assertFalse("The test directory existed after pass " + idx, Files.exists(testDirectory));
		}
	}

	/**
	 * Test method for
	 * {@link uk.co.unclealex.music.files.FileUtilsImpl#cleanDirectory(java.nio.file.Path)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCleanDirectory() throws IOException {
		Path nonEmptyDirectory = testDirectory.resolve("non-empty-directory");
		Files.createDirectory(nonEmptyDirectory);
		Files.createFile(nonEmptyDirectory.resolve("file"));
		for (int idx = 1; idx <= 2; idx++) {
			new FileUtilsImpl().cleanDirectory(testDirectory);
			Assert.assertTrue("The test directory did not exist after pass " + idx + ".", Files.exists(testDirectory));
			Assert.assertArrayEquals("The test directory contained the wrong files.", new File[0], testDirectory.toFile()
					.listFiles());
		}
	}

	@Test
	public void testNonEmptyCleanIfEmpty() throws IOException {
		new TestCleanIfEmpty().
			createPath("Queen", "A Night at the Opera", "01 - Death on Two Legs.flac").
			createPath("Queen", "A Night at the Opera", "02 - Lazing on a Sunday Afternoon.flac").
			createPath("Queen", "A Night at the Opera", "owner.alex").
			expectPath("Queen").
			expectPath("Queen", "A Night at the Opera").
			expectPath("Queen", "A Night at the Opera", "01 - Death on Two Legs.flac").
			expectPath("Queen", "A Night at the Opera", "owner.alex").
			clean("Queen", "A Night at the Opera", "02 - Lazing on a Sunday Afternoon.flac");
	}
	
	@Test
	public void testCleanAlbumCleanIfEmpty() throws IOException {
		new TestCleanIfEmpty().
			createPath("Queen", "A Night at the Opera", "01 - Death on Two Legs.flac").
			createPath("Queen", "A Night at the Opera", "owner.alex").
			createPath("Queen", "A Day at the Races", "01 - Tie Your Mother Down.flac").
			expectPath("Queen").
			expectPath("Queen", "A Day at the Races").
			expectPath("Queen", "A Day at the Races", "01 - Tie Your Mother Down.flac").
			clean("Queen", "A Night at the Opera", "01 - Death on Two Legs.flac");
	}

	@Test
	public void testCleanArtistCleanIfEmpty() throws IOException {
		new TestCleanIfEmpty().
			createPath("Queen", "A Night at the Opera", "01 - Death on Two Legs.flac").
			createPath("Queen", "owner.alex").
			createPath("Slayer", "Christ Illusion", "01 - Fleshstorm.flac").
			expectPath("Slayer").
			expectPath("Slayer", "Christ Illusion").
			expectPath("Slayer", "Christ Illusion", "01 - Fleshstorm.flac").
			clean("Queen", "A Night at the Opera", "01 - Death on Two Legs.flac");
	}

	class TestCleanIfEmpty {
		SortedSet<Path> pathsToCreate = Sets.newTreeSet();
		Path pathToClean;
		SortedSet<Path> expectedPathsAfterCleaning = Sets.newTreeSet();

		public TestCleanIfEmpty createPath(String first, String... others) {
			return addPath(pathsToCreate, first, others);
		}

		public TestCleanIfEmpty expectPath(String first, String... others) {
			return addPath(expectedPathsAfterCleaning, first, others);
		}

		private TestCleanIfEmpty addPath(SortedSet<Path> paths, String first, String[] others) {
			paths.add(makePath(first, others));
			return this;
		}

		protected Path makePath(String first, String[] others) {
			return testDirectory.resolve(testDirectory.getFileSystem().getPath(first, others));
		}

		public void clean(String first, String... others) throws IOException {
			for (Path path : pathsToCreate) {
				log.info("Creating path " + path);
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			}
			Path pathToClean = makePath(first, others);
			Path directoryToClean = pathToClean.getParent();
			Files.createDirectories(directoryToClean);
			Files.deleteIfExists(pathToClean);

			Predicate<Path> preservePathPredicate = new Predicate<Path>() {
				@Override
				public boolean apply(Path path) {
					return path.getFileName().toString().endsWith(".flac");
				}
			};
			new FileUtilsImpl().cleanIfEmpty(testDirectory, directoryToClean, preservePathPredicate);
			
			final SortedSet<Path> actualPaths = Sets.newTreeSet();
			FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					if (!dir.equals(testDirectory)) {
						actualPaths.add(dir);
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					actualPaths.add(file);
					return FileVisitResult.CONTINUE;
				}
			};
			Files.walkFileTree(testDirectory, visitor);
			Assert.assertArrayEquals("The wrong paths were found after cleaning.",
					Iterables.toArray(expectedPathsAfterCleaning, Path.class), Iterables.toArray(actualPaths, Path.class));
		}
	}
}
