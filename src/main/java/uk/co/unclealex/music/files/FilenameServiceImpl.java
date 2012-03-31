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

import java.nio.file.Path;

import uk.co.unclealex.music.common.MusicTrack;
import uk.co.unclealex.music.common.MusicType;

/**
 * The default implementation of {@link FilenameService}.
 * @author alex
 *
 */
public class FilenameServiceImpl implements FilenameService {

	private final FileUtils fileUtils;
	
	public FilenameServiceImpl(FileUtils fileUtils) {
		super();
		this.fileUtils = fileUtils;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MusicTrack toMusicTrack(Path path) {
		String track = getFileUtils().filenameWithoutSuffix(path);
		int pos = path.getNameCount() - 2;
		String album = path.getName(pos).toString();
		String artist = path.getName(pos - 1).toString();
		MusicTrack musicTrack = new MusicTrack(artist, album, track);
		return musicTrack;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path toPath(Path basePath, MusicTrack musicTrack, MusicType musicType, boolean precedeWithFirstLetter) {
		Path result = basePath;
		String artist = musicTrack.getArtist();
		if (precedeWithFirstLetter) {
			String artistWithoutDefiniteArticle = removeDefiniteArticle(artist);
			String firstLetter = artistWithoutDefiniteArticle.substring(0, 1);
			result = result.resolve(firstLetter);
		}
		String suffix = "." + musicType.getExtension();
		return result.resolve(artist).resolve(musicTrack.getAlbum()).resolve(musicTrack.getTrack() + suffix);
	}

	protected String removeDefiniteArticle(String artist) {
		return artist.toLowerCase().startsWith("the ")?artist.substring(4):artist;
	}
	
	/**
	 * @return the fileUtils
	 */
	public FileUtils getFileUtils() {
		return fileUtils;
	}
}
