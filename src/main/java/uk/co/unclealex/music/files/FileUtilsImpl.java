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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

/**
 * The default implementation of {@link FileUtils}.
 * 
 * @author alex
 * 
 */
public class FileUtilsImpl implements FileUtils {

	private static final Logger log = LoggerFactory.getLogger(FileUtilsImpl.class);

	@Override
	public Function<Path, Long> createUpdateTimeFunction() {
		return new Function<Path, Long>() {
			@Override
			public Long apply(Path path) {
				try {
					BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class,
							LinkOption.NOFOLLOW_LINKS);
					return attributes.lastModifiedTime().toMillis();
				}
				catch (IOException e) {
					log.error("Could not read file attributes for path " + path, e);
					return 0l;
				}
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createFileSuffix(String extension) {
		return "." + extension;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Predicate<Path> createFileHasExtensionPredicate(String extension) {
		final String suffix = createFileSuffix(extension);
		return new Predicate<Path>() {
			@Override
			public boolean apply(Path path) {
				return path.getFileName().toString().endsWith(suffix);
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String filenameWithoutSuffix(Path path) {
		String filename = path.getFileName().toString();
		int dotPos = filename.lastIndexOf('.');
		return dotPos < 0?filename:filename.substring(0, dotPos);
	}
	
	@Override
	public void deleteDirectory(Path directory) throws IOException {
		if (!Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
			return;
		}
		if (!Files.isSymbolicLink(directory)) {
			cleanDirectory(directory);
		}

		Files.delete(directory);
	}

	@Override
	public void cleanDirectory(Path directory) throws IOException {
		if (!Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}
		if (!Files.isDirectory(directory)) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
			for (Path path : directoryStream) {
				forceDelete(path);
			}
		}
	}

	@Override
	public void forceDelete(Path path) throws IOException {
		if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			deleteDirectory(path);
		}
		else if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			Files.deleteIfExists(path);
		}
	}
	
	@Override
	public void cleanIfEmpty(Path topLevelPath, Path directory, Predicate<Path> preservePathPredicate) throws IOException {
		if (topLevelPath.equals(directory) || !Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)) {
			return;
		}
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
			boolean empty = true;
			SortedSet<Path> nonPreservedPathsToRemove = Sets.newTreeSet();
			for (Path path : directoryStream) {
				if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS) || preservePathPredicate.apply(path)) {
					empty = false;
				}
				else {
					nonPreservedPathsToRemove.add(path);
				}
			}
			if (empty) {
				for (Path nonPreservedPathToRemove : nonPreservedPathsToRemove) {
					log.info("Removing non preserved file " + nonPreservedPathToRemove);
					Files.delete(nonPreservedPathToRemove);
				}
				log.info("Removing empty directory " + directory);
				Files.delete(directory);
				cleanIfEmpty(topLevelPath, directory.getParent(), preservePathPredicate);
			}
		}
	}
}
