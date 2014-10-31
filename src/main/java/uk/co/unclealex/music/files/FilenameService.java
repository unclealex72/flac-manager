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

import uk.co.unclealex.music.MusicFile;

/**
 * 
 * A service for translating between file names and {@link MusicTrack}s.
 * 
 * @author alex
 * 
 */
public interface FilenameService {

  /**
   * Convert a {@link MusicFile} into a relative path. The path will in the
   * following format:
   * 
   * <code>firstLetterOfSortedAlbumArtist/sortedAlbumArtist/album (diskNumber)/trackNumber title.ext
   * </code>
   * 
   * Track and disk numbers will always have two digits. Disk numbers are only
   * included if they are greater than 1.
   * @param musicFile
   *          The {@link MusicFile} used a
   * @param extension
   *          The file extension to append to the end of the path.
   * 
   * @return A path representing the supplied {@link MusicFile}.
   */
  public Path toPath(MusicFile musicFile, Extension extension);
}
