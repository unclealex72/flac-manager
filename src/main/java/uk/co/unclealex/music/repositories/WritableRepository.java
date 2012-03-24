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
import java.nio.file.Path;

import uk.co.unclealex.music.common.MusicTrack;
import uk.co.unclealex.music.common.MusicType;

/**
 * @author alex
 *
 */
public interface WritableRepository extends Repository {

	/**
	 * Indicate whether this repository can accept music files of a given type.
	 * @param musicType The {@link MusicType} to accept or deny.
	 * @return True if this repository accepts this music type, false otherwise.
	 */
	public boolean accept(MusicType musicType);
	
	/**
	 * Update an exisiting music track.
	 * @param originalPath The path of the music track that contains the data that will be the source of the update.
	 * @param musicTrack The music track being updated.
	 * @param musicType The type of the music track at the original path.
	 * @return True if the music type is accepted by this repository or false otherwise.
	 * @throws IOException
	 */
	public boolean updateTrack(Path originalPath, MusicTrack musicTrack, MusicType musicType) throws IOException;
	
	/**
	 * Add a new music track.
	 * @param originalPath The path of the music track that contains the data that will be the source of the update.
	 * @param musicTrack The music track being added.
	 * @param musicType The type of the music track at the original path.
	 * @return True if the music type is accepted by this repository or false otherwise.
	 * @throws IOException
	 */
	public boolean addTrack(Path originalPath, MusicTrack musicTrack, MusicType musicType) throws IOException;

	/**
	 * Remove an exisiting music track.
	 * @param musicTrack The music track being removed.
	 * @param musicType The type of the music track at the original path.
	 * @return True if the music type is accepted by this repository or false otherwise.
	 * @throws IOException
	 */
	public boolean removeTrack(MusicTrack musicTrack, MusicType musicType) throws IOException;
}
