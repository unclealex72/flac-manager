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

import java.nio.file.Path;

import uk.co.unclealex.music.common.MusicTrack;
import uk.co.unclealex.music.common.MusicType;

/**
 * A {@link Repository} whose {@link MusicTrack}s can be read using {@link Path} objects.
 * @author alex
 *
 */
public interface ReadableRepository extends Repository {

	/**
	 * Get the {@link Path} that uniquely identifies a {@link MusicTrack} in this repository.
	 * @param musicTrack The music track to read.
	 * @return The {@link Path} for the given music track, or null if no such track exists.
	 */
	public Path readMusicTrack(MusicTrack musicTrack);
	
	/**
	 * Return the {@link MusicType} of files that can be read from this repository.
	 * @return The {@link MusicType} of files that can be read from this repository.
	 */
	public MusicType getReadableMusicType();
}
