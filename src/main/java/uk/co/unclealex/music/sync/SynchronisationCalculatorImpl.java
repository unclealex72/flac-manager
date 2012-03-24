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

package uk.co.unclealex.music.sync;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import uk.co.unclealex.music.common.MusicTrack;
import uk.co.unclealex.music.repositories.Repository;
import uk.co.unclealex.music.sync.actions.SynchronisationAction;
import uk.co.unclealex.music.sync.actions.SynchronisationActionFactory;

/**
 * The default implementation of {@link SynchronisationCalculator}.
 * 
 * @author alex
 * 
 */
public class SynchronisationCalculatorImpl implements SynchronisationCalculator {

	private static final Logger log = LoggerFactory.getLogger(SynchronisationCalculatorImpl.class);

	/**
	 * The {@link SynchronisationActionFactory} used to create
	 * {@link SynchronisationAction}s.
	 */
	private final SynchronisationActionFactory synchronisationActionFactory;

	public SynchronisationCalculatorImpl(SynchronisationActionFactory synchronisationActionFactory) {
		super();
		this.synchronisationActionFactory = synchronisationActionFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedSet<SynchronisationAction> calculateSynchronisationActions(Repository source, Repository target) {
		SortedMap<MusicTrack, Long> sourceUpdateTimesByMusicTrack = source.getUpdateTimesByMusicTrack();
		SortedMap<MusicTrack, Long> targetUpdateTimesByMusicTrack = target.getUpdateTimesByMusicTrack();
		SortedSet<MusicTrack> remainingTargetMusicTracks = Sets.newTreeSet(targetUpdateTimesByMusicTrack.keySet());
		SortedSet<SynchronisationAction> synchronisationActions = Sets.newTreeSet();
		final SynchronisationActionFactory synchronisationActionFactory = getSynchronisationActionFactory();

		for (Entry<MusicTrack, Long> entry : sourceUpdateTimesByMusicTrack.entrySet()) {
			MusicTrack musicTrack = entry.getKey();
			if (targetUpdateTimesByMusicTrack.containsKey(musicTrack)) {
				remainingTargetMusicTracks.remove(musicTrack);
				long sourceUpdateTime = entry.getValue();
				long targetUpdateTime = targetUpdateTimesByMusicTrack.get(musicTrack);
				if (sourceUpdateTime > targetUpdateTime) {
					if (log.isDebugEnabled()) {
						log.debug("Music track " + musicTrack + " will be updated because it's update time " + sourceUpdateTime
								+ " is later than " + targetUpdateTime);
					}
					synchronisationActions.add(synchronisationActionFactory.createTrackUpdatedAction(musicTrack));
				}
				else if (log.isDebugEnabled()) {
					log.debug("Music track " + musicTrack + " will be not updated because it's update time " + sourceUpdateTime
							+ " is not later than " + targetUpdateTime);					
				}
			}
			else {
				if (log.isDebugEnabled()) {
					log.debug("Music track " + musicTrack + " will be added.");
				}
				synchronisationActions.add(synchronisationActionFactory.createTrackAddedAction(musicTrack));
			}
		}
		Function<MusicTrack, SynchronisationAction> removeTrackFunction = new Function<MusicTrack, SynchronisationAction>() {
			@Override
			public SynchronisationAction apply(MusicTrack musicTrack) {
				if (log.isDebugEnabled()) {
					log.debug("Music Track " + musicTrack + " will be removed.");
				}
				return synchronisationActionFactory.createTrackRemovedAction(musicTrack);
			}
		};
		Iterables.addAll(synchronisationActions, Iterables.transform(remainingTargetMusicTracks, removeTrackFunction));
		return synchronisationActions;
	}

	/**
	 * @return the synchronisationActionFactory
	 */
	public SynchronisationActionFactory getSynchronisationActionFactory() {
		return synchronisationActionFactory;
	}

}
