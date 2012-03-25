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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Utilities for manipulating files and directories in ways not directly
 * supported by the JDK.
 * 
 * @author alex
 * 
 */
public interface FileUtils {

	/**
	 * Create a {@link Function} that returns the update time of a {@link Path}.
	 * The update time of a symbolic link is the time the symbolic link itself was
	 * updated.
	 * 
	 * @return A {@link Function} that returns the update time of a {@link Path}.
	 */
	public Function<Path, Long> createUpdateTimeFunction();

	/**
	 * Create a {@link Predicate} that returns true if and only if a {@link Path}
	 * has the supplied extension.
	 * 
	 * @param extension
	 *          The extension to check for.
	 * @return A {@link Predicate} that returns true if and only if a {@link Path}
	 *         has the supplied extension.
	 */
	public Predicate<Path> createFileHasExtensionPredicate(String extension);

	/**
	 * Turn an extension into a filesuffix.
	 * 
	 * @param extension
	 *          The extension in question.
	 * @return The extension as a suffix. That is, the extension preceded by a
	 *         dot.
	 */
	public String createFileSuffix(String extension);

	/**
	 * Remove a directory if it is empty (i.e. it has no paths that are to be
	 * preserved). If this then means the parent directory is then empty, clean
	 * that, too, until a top level directory is reached.
	 * 
	 * @param topLevelPath
	 *          The top level path that should not be deleted.
	 * @param directory
	 *          The directory to delete if it is empty.
	 * @param preservePathPredicate
	 *          A predicate that returns true if a path should not be deleted by
	 *          this method or false otherwise.
	 * @throws IOException
	 */
	public void cleanIfEmpty(Path topLevelPath, Path directory, Predicate<Path> preservePathPredicate) throws IOException;

	/**
	 * Deletes a file. If file is a directory, delete it and all sub-directories.
	 * <p>
	 * The difference between File.delete() and this method are:
	 * <ul>
	 * <li>A directory to be deleted does not have to be empty.</li>
	 * <li>You get exceptions when a file or directory cannot be deleted.
	 * (java.io.File methods returns a boolean)</li>
	 * </ul>
	 * 
	 * @param path
	 *          file or directory to delete, must not be <code>null</code>
	 * @throws NullPointerException
	 *           if the directory is <code>null</code>
	 * @throws FileNotFoundException
	 *           if the file was not found
	 * @throws IOException
	 *           in case deletion is unsuccessful
	 */
	public void forceDelete(Path path) throws IOException;

	/**
	 * Cleans a directory without deleting it.
	 * 
	 * @param directory
	 *          directory to clean
	 * @throws IOException
	 *           in case cleaning is unsuccessful
	 */
	public void cleanDirectory(Path directory) throws IOException;

	/**
	 * Deletes a directory recursively.
	 * 
	 * @param directory
	 *          directory to delete
	 * @throws IOException
	 *           in case deletion is unsuccessful
	 */
	public void deleteDirectory(Path directory) throws IOException;

	/**
	 * Return a file's base name. That, is the filename of the file without the
	 * file's suffix.
	 * 
	 * @param path
	 *          The path whose base name will be returned.
	 * @return The file's base name.
	 */
	public String filenameWithoutSuffix(Path path);

}
