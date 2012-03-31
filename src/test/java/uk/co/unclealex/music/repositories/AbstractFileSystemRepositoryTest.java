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

package uk.co.unclealex.music.repositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.SortedSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.common.MusicTrack;
import uk.co.unclealex.music.common.MusicType;
import uk.co.unclealex.music.common.MusicTypeFactory;
import uk.co.unclealex.music.common.MusicTypeFactoryImpl;
import uk.co.unclealex.music.files.FileUtils;
import uk.co.unclealex.music.files.FileUtilsImpl;
import uk.co.unclealex.music.files.FilenameService;
import uk.co.unclealex.music.files.FilenameServiceImpl;

import com.google.common.collect.Sets;

/**
 * @author alex
 *
 */
public class AbstractFileSystemRepositoryTest {

	private static final Logger log = LoggerFactory.getLogger(AbstractFileSystemRepositoryTest.class);
	
	Path testDirectory;
	FileUtils fileUtils = new FileUtilsImpl();
	FilenameService filenameService = new FilenameServiceImpl(fileUtils);
	MusicTypeFactory musicTypeFactory = new MusicTypeFactoryImpl();
	
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
	public void testInitialise() {
		final MusicType flacMusicType = musicTypeFactory.createFlacType();
		final SortedSet<Path> actualNonMusicFiles = Sets.newTreeSet();
		TestFileSystemRepository repository = new TestFileSystemRepository(false, flacMusicType) {
			
			@Override
			public boolean accept(MusicType musicType) {
				return flacMusicType.equals(musicType);
			}
			
			@Override
			protected void importTrack(Path originalPath, Path newPath, MusicTrack musicTrack, MusicType musicType)
					throws IOException {
				Files.copy(originalPath, newPath);
			}

			@Override
			protected void onNonMusicFileRead(Path path, BasicFileAttributes attrs) {
				actualNonMusicFiles.add(path);
			}
		};
		// TODO Write me.
	}

	protected Path createPath(String first, String... others) {
		return testDirectory.resolve(testDirectory.getFileSystem().getPath(first, others));
	}
	
	protected Path touch(Path path) throws IOException {
		if (!Files.exists(path.getParent())) {
			Files.createDirectories(path.getParent());
		}
		Files.createFile(path);
		return path;
	}
	
	abstract class TestFileSystemRepository extends AbstractFileSystemRepository {

		public TestFileSystemRepository(boolean precedePathsWithFirstLetterOfArtist, MusicType readableMusicType) {
			super(filenameService, fileUtils, testDirectory, precedePathsWithFirstLetterOfArtist, readableMusicType);
		}
		
		
	}
}
