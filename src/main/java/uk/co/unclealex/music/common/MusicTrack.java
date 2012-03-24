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

package uk.co.unclealex.music.common;

import java.io.Serializable;
import java.util.Objects;

/**
 * A music track is used to identify a unique music track in a {@link MusicRepository}. The music track itself
 * does not identify how the track is stored. All identifiers are normalised.
 * @author alex
 * @see Normaliser
 */
public class MusicTrack implements Serializable, Comparable<MusicTrack> {

	/**
	 * The normalised name of the artist.
	 */
	private final String artist;
	
	/**
	 * The normalised name of the album.
	 */
	private final String album;
	
	/**
	 * The normalised track number and title.
	 */
	private final String track;

	public MusicTrack(String artist, String album, String track) {
		super();
		this.artist = artist;
		this.album = album;
		this.track = track;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getArtist(), getAlbum(), getTrack());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("{%s, %s, %s}", getArtist(), getAlbum(), getTrack());
	}
	/**
	 * Compare two music tracks. First, the tracks {@link #artist} are compared, then the {@link #album} and then
	 * the {@link #track}
	 * @param o The music track to compare to.
	 */
	@Override
	public int compareTo(MusicTrack o) {
		int cmp = getArtist().compareTo(o.getArtist());
		if (cmp == 0) {
			cmp = getAlbum().compareTo(o.getAlbum());
			if (cmp == 0) {
				cmp = getTrack().compareTo(o.getTrack());
			}
		}
		return cmp;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof MusicTrack) && compareTo((MusicTrack) obj) == 0;
	}
	
	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @return the album
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * @return the track
	 */
	public String getTrack() {
		return track;
	}
}
