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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.music.common.MusicTrack;
import uk.co.unclealex.music.repositories.Repository;
import uk.co.unclealex.music.sync.actions.SynchronisationAction;
import uk.co.unclealex.music.sync.actions.SynchronisationActionFactory;
import uk.co.unclealex.music.sync.actions.SynchronisationActionFactoryImpl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * @author alex
 * 
 */
public class SynchronisationCalculatorImplTest {

	private static final DateFormat DF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	SynchronisationActionFactory synchronisationActionFactory = new SynchronisationActionFactoryImpl();
	SynchronisationCalculator synchronisationCalculator = new SynchronisationCalculatorImpl(synchronisationActionFactory);

	@Test
	public void testNoUpdates() throws ParseException {
		Repository source = new TestRepository().
				with("Queen", "A Night at the Opera", "01 Death on Two Legs", "14/03/2012 18:00:00").
				with("Queen", "A Night at the Opera", "02 Lazing on a Sunday Afternoon", "14/03/2012 18:02:00");
		Repository target = new TestRepository().
				with("Queen", "A Night at the Opera", "01 Death on Two Legs", "15/03/2012 18:00:00").
				with("Queen", "A Night at the Opera", "02 Lazing on a Sunday Afternoon", "15/03/2012 18:02:00");
		test(source, target, new SynchronisationActions());
	}
	
	@Test
	public void testUpdateFromClean() throws ParseException {
		Repository source = new TestRepository().
				with("Queen", "A Night at the Opera", "01 Death on Two Legs", "14/03/2012 18:00:00").
				with("Queen", "A Night at the Opera", "02 Lazing on a Sunday Afternoon", "14/03/2012 18:02:00");
		Repository target = new TestRepository();
		SynchronisationActions expectedSynchronisationActions = new SynchronisationActions().
				withAdded("Queen", "A Night at the Opera", "01 Death on Two Legs").
				withAdded("Queen", "A Night at the Opera", "02 Lazing on a Sunday Afternoon");
		test(source, target, expectedSynchronisationActions);
	}

	@Test
	public void testUpdate() throws ParseException {
		Repository source = new TestRepository().
				with("Queen", "A Night at the Opera", "01 Death on Two Legs", "17/03/2012 18:00:00").
				with("Queen", "A Night at the Opera", "02 Lazing on a Sunday Afternoon", "14/03/2012 18:02:00");
		Repository target = new TestRepository().
				with("Queen", "A Night at the Opera", "01 Death on Two Legs", "15/03/2012 18:00:00").
				with("Queen", "A Night at the Opera", "02 Lazing on a Sunday Afternoon", "15/03/2012 18:02:00");
		SynchronisationActions expectedSynchronisationActions = new SynchronisationActions().
				withUpdated("Queen", "A Night at the Opera", "01 Death on Two Legs");
		test(source, target, expectedSynchronisationActions);
	}

	@Test
	public void testRemove() throws ParseException {
		Repository source = new TestRepository().
				with("Queen", "A Night at the Opera", "02 Lazing on a Sunday Afternoon", "14/03/2012 18:02:00");
		Repository target = new TestRepository().
				with("Queen", "A Night at the Opera", "01 Death on Two Legs", "15/03/2012 18:00:00").
				with("Queen", "A Night at the Opera", "02 Lazing on a Sunday Afternoon", "15/03/2012 18:02:00");
		SynchronisationActions expectedSynchronisationActions = new SynchronisationActions().
				withRemoved("Queen", "A Night at the Opera", "01 Death on Two Legs");
		test(source, target, expectedSynchronisationActions);
	}

	@Test
	public void testAllTogether() throws ParseException {
		Repository source = new TestRepository().
				with("Queen", "A Night at the Opera", "01 Death on Two Legs", "17/03/2012 18:00:00").
				with("Queen", "A Night at the Opera", "02 Lazing on a Sunday Afternoon", "14/03/2012 18:02:00").
				with("Slayer", "Reign in Blood", "01 Angel of Death", "17/03/2012 18:00:00").
				with("Slayer", "Reign in Blood", "02 Piece by Piece", "17/03/2012 18:00:00").
				with("Metallica", "Ride the Lightning", "01 Fight Fire with Fire", "17/03/2012 18:00:00").
				with("Metallica", "Ride the Lightning", "02 Ride the Lightning", "17/03/2012 18:00:00");
		Repository target = new TestRepository().
				with("Queen", "A Night at the Opera", "01 Death on Two Legs", "17/03/2012 18:00:00").
				with("Slayer", "Reign in Blood", "01 Angel of Death", "14/03/2012 18:00:00").
				with("Slayer", "Reign in Blood", "02 Piece by Piece", "14/03/2012 18:00:00").
				with("Metallica", "Ride the Lightning", "01 Fight Fire with Fire", "17/03/2012 18:00:00").
				with("Metallica", "Ride the Lightning", "03 Ride the Lightning", "17/03/2012 18:00:00");
		SynchronisationActions expectedSynchronisationActions = new SynchronisationActions().
				withAdded("Queen", "A Night at the Opera", "02 Lazing on a Sunday Afternoon").
				withUpdated("Slayer", "Reign in Blood", "01 Angel of Death").
				withUpdated("Slayer", "Reign in Blood", "02 Piece by Piece").
				withAdded("Metallica", "Ride the Lightning", "02 Ride the Lightning").
				withRemoved("Metallica", "Ride the Lightning", "03 Ride the Lightning");
		test(source, target, expectedSynchronisationActions);
	}

	public void test(Repository source, Repository target, SortedSet<SynchronisationAction> expectedSynchronisationActions) {
		SortedSet<SynchronisationAction> actualSynchronisationActions = synchronisationCalculator
				.calculateSynchronisationActions(source, target);
		Assert.assertArrayEquals("The wrong synchronisation actions were returned.",
				Iterables.toArray(expectedSynchronisationActions, SynchronisationAction.class),
				Iterables.toArray(actualSynchronisationActions, SynchronisationAction.class));
	}

	class TestRepository implements Repository {

		final SortedMap<MusicTrack, Long> updateTimesByMusicTrack = Maps.newTreeMap();

		public TestRepository with(String artist, String album, String track, String updateTime) throws ParseException {
			MusicTrack musicTrack = new MusicTrack(artist, album, track);
			long lastModified = DF.parse(updateTime).getTime();
			updateTimesByMusicTrack.put(musicTrack, lastModified);
			return this;
		}

		public SortedMap<MusicTrack, Long> getUpdateTimesByMusicTrack() {
			return updateTimesByMusicTrack;
		}
	}

	class SynchronisationActions extends TreeSet<SynchronisationAction> {

		public SynchronisationActions withAdded(String artist, String album, String track) {
			add(synchronisationActionFactory.createTrackAddedAction(new MusicTrack(artist, album, track)));
			return this;
		}

		public SynchronisationActions withUpdated(String artist, String album, String track) {
			add(synchronisationActionFactory.createTrackUpdatedAction(new MusicTrack(artist, album, track)));
			return this;
		}

		public SynchronisationActions withRemoved(String artist, String album, String track) {
			add(synchronisationActionFactory.createTrackRemovedAction(new MusicTrack(artist, album, track)));
			return this;
		}

	}
}
