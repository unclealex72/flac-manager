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

import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.music.common.MusicTrack;

/**
 * @author alex
 * 
 */
public class SynchronisationActionFactoryImplTest {

	SynchronisationActionFactory synchronisationActionFactory = new SynchronisationActionFactoryImpl();

	@Test
	public void testAdded() {
		TestingVisitor testingVisitor = new TestingVisitor("Queen", "A Day at the Races", "01 Tie Your Mother Down") {
			@Override
			public void visit(TrackAddedAction trackAddedAction) {
				expect(trackAddedAction);
			}
		};
		runTest(synchronisationActionFactory.createTrackAddedAction(new MusicTrack("Queen", "A Day at the Races",
				"01 Tie Your Mother Down")), testingVisitor);
	}

	@Test
	public void testRemoved() {
		TestingVisitor testingVisitor = new TestingVisitor("Queen", "A Day at the Races", "01 Tie Your Mother Down") {
			@Override
			public void visit(TrackRemovedAction trackRemovedAction) {
				expect(trackRemovedAction);
			}
		};
		runTest(synchronisationActionFactory.createTrackRemovedAction(new MusicTrack("Queen", "A Day at the Races",
				"01 Tie Your Mother Down")), testingVisitor);
	}

	@Test
	public void testUpdated() {
		TestingVisitor testingVisitor = new TestingVisitor("Queen", "A Day at the Races", "01 Tie Your Mother Down") {
			@Override
			public void visit(TrackUpdatedAction trackUpdatedAction) {
				expect(trackUpdatedAction);
			}
		};
		runTest(synchronisationActionFactory.createTrackUpdatedAction(new MusicTrack("Queen", "A Day at the Races",
				"01 Tie Your Mother Down")), testingVisitor);
	}

	@Test
	public void testInvalid() {
		TestingVisitor testingVisitor = new TestingVisitor("Queen", "A Day at the Races", "01 Tie Your Mother Down");
		SynchronisationAction synchronisationAction = new SynchronisationAction() {
			@Override
			public int compareTo(SynchronisationAction o) {
				return 0;
			}
			@Override
			public MusicTrack getMusicTrack() {
				return null;
			}
			@Override
			public void accept(SynchronisationActionVisitor synchronisationActionVisitor) {
				synchronisationActionVisitor.visit(this);
			}
		};
		try {
			runTest(synchronisationAction, testingVisitor);
			Assert.fail("An illegal argument exception was not thrown.");
		}
		catch (IllegalArgumentException e) {
			// This is expected.
		}
	}
	public void runTest(SynchronisationAction actualSynchronisationAction, TestingVisitor testingVisitor) {
		actualSynchronisationAction.accept(testingVisitor);
	}

	class TestingVisitor extends SynchronisationActionVisitor.Default {

		final String artist;
		final String album;
		final String track;

		public TestingVisitor(String artist, String album, String track) {
			super();
			this.artist = artist;
			this.album = album;
			this.track = track;
		}

		@Override
		public void visit(TrackAddedAction trackAddedAction) {
			Assert.fail("Was not expecting a track added action");
		}

		@Override
		public void visit(TrackRemovedAction trackRemovedAction) {
			Assert.fail("Was not expecting a track removed action");
		}

		@Override
		public void visit(TrackUpdatedAction trackUpdatedAction) {
			Assert.fail("Was not expecting a track udated action");
		}

		public void expect(SynchronisationAction synchronisationAction) {
			Assert.assertEquals("The wrong music track was returned.", new MusicTrack(artist, album, track),
					synchronisationAction.getMusicTrack());
		}
	}
}
