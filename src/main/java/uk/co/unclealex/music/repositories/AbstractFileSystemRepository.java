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
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.common.ExtensionFactory;
import uk.co.unclealex.music.common.MusicTrack;
import uk.co.unclealex.music.common.MusicType;
import uk.co.unclealex.music.common.Normaliser;
import uk.co.unclealex.music.files.FileUtils;
import uk.co.unclealex.music.files.FilenameService;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

/**
 * A base class for file system repositories. It is assumed that all filenames
 * are normalised.
 * 
 * @author alex
 * @see Normaliser
 */
public abstract class AbstractFileSystemRepository implements ReadableRepository, WritableRepository {

	private static final Logger log = LoggerFactory.getLogger(AbstractFileSystemRepository.class);

	/**
	 * The base path for this repository.
	 */
	private final Path basePath;

	/**
	 * The {@link MusicType} of files stored in this repository.
	 */
	private final MusicType readableMusicType;

	/**
	 * The {@link FileUtils} object for manipulating files.
	 */
	private final FileUtils fileUtils;

	/**
	 * The {@link FilenameService} used to convert between paths and
	 * {@link MusicTrack}s.
	 */
	private final FilenameService filenameService;

	private final ExtensionFactory extensionFactory;

	/**
	 * True if the first directory should be the first letter of the artist, false
	 * otherwise.
	 */
	private final boolean precedePathsWithFirstLetterOfArtist;

	private final BiMap<MusicTrack, Path> pathsByMusicTrack = HashBiMap.create();

	protected AbstractFileSystemRepository(FilenameService filenameService, ExtensionFactory extensionFactory,
			FileUtils fileUtils, Path basePath, boolean precedePathsWithFirstLetterOfArtist, MusicType readableMusicType) {
		super();
		this.extensionFactory = extensionFactory;
		this.filenameService = filenameService;
		this.fileUtils = fileUtils;
		this.basePath = basePath;
		this.precedePathsWithFirstLetterOfArtist = precedePathsWithFirstLetterOfArtist;
		this.readableMusicType = readableMusicType;
	}

	/**
	 * Initialise this {@link Repository} by looking for all music files.
	 * 
	 * @throws IOException
	 */
	public void initialise() throws IOException {
		String extension = getExtensionFactory().getExtensionForMusicType(getReadableMusicType());
		final Predicate<Path> isMusicTrackPredicate = getFileUtils().createFileHasExtensionPredicate(extension);
		FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				if (isMusicTrackPredicate.apply(path)) {
					addInitialPath(path);
				}
				else {
					onNonMusicFileRead(path, attrs);
				}
				return FileVisitResult.CONTINUE;
			}
		};
		Files.walkFileTree(getBasePath(), visitor);
	}

	/**
	 * @param path
	 * @param attrs
	 */
	protected void onNonMusicFileRead(Path path, BasicFileAttributes attrs) {
		// Default to do nothing.
	}

	/**
	 * @param path
	 */
	protected void addInitialPath(Path path) {
		MusicTrack musicTrack = getFilenameService().toMusicTrack(path);
		getPathsByMusicTrack().put(musicTrack, path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedMap<MusicTrack, Long> getUpdateTimesByMusicTrack() {
		Function<Path, Long> updateTimeFunction = getFileUtils().createUpdateTimeFunction();
		SortedMap<MusicTrack, Long> updateTimesByMusicTrack = Maps.newTreeMap();
		updateTimesByMusicTrack.putAll(Maps.transformValues(getPathsByMusicTrack(), updateTimeFunction));
		return updateTimesByMusicTrack;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path readMusicTrack(MusicTrack musicTrack) {
		return getPathsByMusicTrack().get(musicTrack);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addTrack(Path originalPath, MusicTrack musicTrack, MusicType musicType) throws IOException {
		if (accept(musicType)) {
			Path newPath = getFilenameService().toPath(getBasePath(), musicTrack, musicType,
					isPrecedePathsWithFirstLetterOfArtist());
			Files.createDirectories(newPath.getParent());
			importTrack(originalPath, newPath, musicTrack, musicType);
			getPathsByMusicTrack().put(musicTrack, newPath);
			getUpdateTimesByMusicTrack().put(musicTrack, getFileUtils().createUpdateTimeFunction().apply(newPath));
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Import a music track.
	 * 
	 * @param originalPath
	 *          The original path is where the data to import is.
	 * @param newPath
	 *          The path where the new track will be stored.
	 * @param musicTrack
	 *          The {@link MusicTrack} being stored.
	 * @param musicType
	 *          The {@link MusicType} being stored.
	 * @return
	 */
	protected abstract void importTrack(Path originalPath, Path newPath, MusicTrack musicTrack, MusicType musicType)
			throws IOException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateTrack(Path originalPath, MusicTrack musicTrack, MusicType musicType) throws IOException {
		// For file systems, adding is the same as updating.
		return addTrack(originalPath, musicTrack, musicType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeTrack(MusicTrack musicTrack, MusicType musicType) throws IOException {
		if (accept(musicType)) {
			Path path = getPathsByMusicTrack().get(musicTrack);
			log.info("Removing path " + path);
			if (Files.deleteIfExists(path)) {
				getFileUtils().cleanIfEmpty(getBasePath(), path.getParent());
			}
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @return the basePath
	 */
	public Path getBasePath() {
		return basePath;
	}

	public BiMap<MusicTrack, Path> getPathsByMusicTrack() {
		return pathsByMusicTrack;
	}

	/**
	 * @return the readableMusicType
	 */
	public MusicType getReadableMusicType() {
		return readableMusicType;
	}

	/**
	 * @return the fileUtils
	 */
	public FileUtils getFileUtils() {
		return fileUtils;
	}

	/**
	 * @return the filenameService
	 */
	public FilenameService getFilenameService() {
		return filenameService;
	}

	/**
	 * @return the precedePathsWithFirstLetterOfArtist
	 */
	public boolean isPrecedePathsWithFirstLetterOfArtist() {
		return precedePathsWithFirstLetterOfArtist;
	}

	/**
	 * @return the extensionFactory
	 */
	public ExtensionFactory getExtensionFactory() {
		return extensionFactory;
	}
}
