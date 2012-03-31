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

import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.music.common.MusicTrack;
import uk.co.unclealex.music.common.MusicType;
import uk.co.unclealex.music.common.MusicTypeFactory;
import uk.co.unclealex.music.common.MusicTypeFactoryImpl;

/**
 * @author alex
 * 
 */
public class FilenameServiceImplTest {

	@Test
	public void testMp3ToMusicTrack() {
		testToMusicTrack(
				FileSystems.getDefault().getPath("mnt", "multimedia", "mp3", "Queen", "A Night at the Opera",
						"01 - Death on Two Legs.mp3"), "Queen", "A Night at the Opera", "01 - Death on Two Legs");
	}

	@Test
	public void testOggToMusicTrack() {
		testToMusicTrack(
				FileSystems.getDefault().getPath("mnt", "multimedia", "ogg", "Slayer", "Reign in Blood",
						"02 - Piece by Piece.ogg"), "Slayer", "Reign in Blood", "02 - Piece by Piece");
	}

	@Test
	public void testFlacToMusicTrack() {
		testToMusicTrack(
				FileSystems.getDefault().getPath("mnt", "multimedia", "flac", "Metallica", "Master of Puppets",
						"05 - Disposable Heroes.flac"), "Metallica", "Master of Puppets", "05 - Disposable Heroes");
	}

	@Test
	public void testFlacToPath() {
		new TestToPath("Queen", "A Night at the Opera", "01 - Death on Two Legs", false, "Queen", "A Night at the Opera",
				"01 - Death on Two Legs.flac") {

			@Override
			MusicType createMusicType(MusicTypeFactory musicTypeFactory) {
				return musicTypeFactory.createFlacType();
			}
		};
	}

	@Test
	public void testMp3ToPath() {
		new TestToPath("The Stranglers", "The Singles (The UA Years)", "04 - Something Better Change", true, "S",
				"The Stranglers", "The Singles (The UA Years)", "04 - Something Better Change.mp3") {

			@Override
			MusicType createMusicType(MusicTypeFactory musicTypeFactory) {
				return musicTypeFactory.createMp3Type();
			}
		};
	}

	@Test
	public void testOggToPath() {
		new TestToPath("Slayer", "Reign in Blood", "03 - Necrophobic", true, "S",
				"Slayer", "Reign in Blood", "03 - Necrophobic.ogg") {

			@Override
			MusicType createMusicType(MusicTypeFactory musicTypeFactory) {
				return musicTypeFactory.createOggType();
			}
		};
	}

	abstract class TestToPath {
		public TestToPath(String artist, String album, String track, boolean precedeWithFirstLetter,
				String... expectedComponents) {
			super();
			Path basePath = FileSystems.getDefault().getPath("repository");
			MusicTypeFactory musicTypeFactory = new MusicTypeFactoryImpl();
			MusicType musicType = createMusicType(musicTypeFactory);
			Path actualPath = createFilenameService().toPath(basePath, new MusicTrack(artist, album, track), musicType,
					precedeWithFirstLetter);
			Path expectedPath = basePath;
			for (String expectedComponent : expectedComponents) {
				expectedPath = expectedPath.resolve(expectedComponent);
			}
			Assert.assertEquals("The wrong path was returned.", expectedPath, actualPath);
		}

		abstract MusicType createMusicType(MusicTypeFactory musicTypeFactory);
	}

	public void testToMusicTrack(Path path, String expectedArtist, String expectedAlbum, String expectedTrack) {
		MusicTrack actualMusicTrack = createFilenameService().toMusicTrack(path);
		MusicTrack expectedMusicTrack = new MusicTrack(expectedArtist, expectedAlbum, expectedTrack);
		Assert.assertEquals("The wrong music track was returned.", expectedMusicTrack, actualMusicTrack);
	}

	protected FilenameService createFilenameService() {
		return new FilenameServiceImpl(new FileUtilsImpl());
	}
}
