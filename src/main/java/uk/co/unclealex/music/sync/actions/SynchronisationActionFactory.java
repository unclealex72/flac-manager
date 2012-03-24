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

package uk.co.unclealex.music.sync.actions;

import uk.co.unclealex.music.common.MusicTrack;

/**
 * An interface that creates {@link SynchronisationAction}s.
 * @author alex
 *
 */
public interface SynchronisationActionFactory {

	/**
	 * Create a new {@link TrackAddedAction}.
	 * @param musicTrack The subject of the {@link TrackAddedAction}.
	 * @return A new {@link TrackAddedAction} with the given {@link MusicTrack} as a subject.
	 */
	public SynchronisationAction createTrackAddedAction(MusicTrack musicTrack);

	/**
	 * Create a new {@link TrackUpdatedAction}.
	 * @param musicTrack The subject of the {@link TrackUpdatedAction}.
	 * @return A new {@link TrackUpdatedAction} with the given {@link MusicTrack} as a subject.
	 */
	public SynchronisationAction createTrackUpdatedAction(MusicTrack musicTrack);

	/**
	 * Create a new {@link TrackRemovedAction}.
	 * @param musicTrack The subject of the {@link TrackRemovedAction}.
	 * @return A new {@link TrackRemovedAction} with the given {@link MusicTrack} as a subject.
	 */
	public SynchronisationAction createTrackRemovedAction(MusicTrack musicTrack);
}
