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

/**
 * A visitor interface for {@link MusicType}s.
 * @author alex
 *
 */
public interface MusicTypeVisitor<E> {

	/**
	 * Visit a {@link MusicType}.
	 * @param musicType The {@link MusicType} to visit.
	 */
	public E visit(MusicType musicType);
	
	/**
	 * Visit a {@link FlacType}.
	 * @param flacType The {@link FlacType} to visit.
	 */
	public E visit(FlacType flacType);

	/**
	 * Visit an {@link OggType}.
	 * @param oggType The {@link OggType} to visit.
	 */
	public E visit(OggType oggType);

	/**
	 * Visit a {@link Mp3Type}.
	 * @param mp3Type The {@link Mp3Type} to visit.
	 */
	public E visit(Mp3Type mp3Type);
	
	public abstract class Default<E> implements MusicTypeVisitor<E> {
		
		public final E visit(MusicType musicType) {
			throw new IllegalArgumentException(musicType.getClass() + " is not a valid music type.");
		}
	}
}
