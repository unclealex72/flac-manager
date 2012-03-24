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

import com.google.common.base.Function;

/**
 * The default implementation of {@link SynchronisationActionFactory}.
 * @author alex
 *
 */
public class SynchronisationActionFactoryImpl implements SynchronisationActionFactory {

	/**
	 * A {@link Function} to compare {@link SynchronisationAction}s so that, given two equal {@link MusicTrack}s,
	 * {@link TrackAddedAction} &lt; {@link TrackRemovedAction} &lt; {@link TrackUpdatedAction}.
	 * @author alex
	 *
	 */
	class SynchronisationActionOrderingFunction implements Function<SynchronisationAction, Integer> {
		@Override
		public Integer apply(SynchronisationAction synchronisationAction) {
			class Visitor extends SynchronisationActionVisitor.Default {
				int value;
				@Override
				public void visit(TrackAddedAction trackAddedAction) {
					value = 0;
				}
				@Override
				public void visit(TrackRemovedAction trackRemovedAction) {
					value = 1;
				}
				@Override
				public void visit(TrackUpdatedAction trackUpdatedAction) {
					value = 2;
				}			
			}
			Visitor visitor = new Visitor();
			synchronisationAction.accept(visitor);
			return visitor.value;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TrackAddedAction createTrackAddedAction(MusicTrack musicTrack) {
		class TrackAddedActionImpl extends AbstractSynchronisationAction implements TrackAddedAction {

			public TrackAddedActionImpl(MusicTrack musicTrack) {
				super(musicTrack, "Adding");
			}
			
			@Override
			public void accept(SynchronisationActionVisitor synchronisationActionVisitor) {
				synchronisationActionVisitor.visit((TrackAddedAction) this);
			}
		};
		return new TrackAddedActionImpl(musicTrack);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TrackUpdatedAction createTrackUpdatedAction(MusicTrack musicTrack) {
		class TrackUpdatedActionImpl extends AbstractSynchronisationAction implements TrackUpdatedAction {

			public TrackUpdatedActionImpl(MusicTrack musicTrack) {
				super(musicTrack, "Updating");
			}
			
			@Override
			public void accept(SynchronisationActionVisitor synchronisationActionVisitor) {
				synchronisationActionVisitor.visit((TrackUpdatedAction) this);
			}
		};
		return new TrackUpdatedActionImpl(musicTrack);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TrackRemovedAction createTrackRemovedAction(MusicTrack musicTrack) {
		class TrackRemovedActionImpl extends AbstractSynchronisationAction implements TrackRemovedAction {

			public TrackRemovedActionImpl(MusicTrack musicTrack) {
				super(musicTrack, "Removing");
			}
			
			@Override
			public void accept(SynchronisationActionVisitor synchronisationActionVisitor) {
				synchronisationActionVisitor.visit((TrackRemovedAction) this);
			}
		};
		return new TrackRemovedActionImpl(musicTrack);
	}

	/**
	 * The base implementation of {@link SynchronisationAction}.
	 * @author alex
	 *
	 */
	abstract class AbstractSynchronisationAction implements SynchronisationAction {
		
		/**
		 * The {@link MusicTrack} to be synchronised.
		 */
		private final MusicTrack musicTrack;
		
		/**
		 * A descriptive word to describe this action.
		 */
		private final String descriptiveType;
		
		public AbstractSynchronisationAction(MusicTrack musicTrack, String descriptiveType) {
			super();
			this.musicTrack = musicTrack;
			this.descriptiveType = descriptiveType;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(SynchronisationAction o) {
			int cmp = getMusicTrack().compareTo(o.getMusicTrack());
			if (cmp == 0) {
				Function<SynchronisationAction, Integer> f = new SynchronisationActionOrderingFunction();
				cmp = f.apply(this) - f.apply(o);
			}
			return cmp;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return String.format("<%s %s>", getDescriptiveType(), getMusicTrack());
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof SynchronisationAction) && compareTo((SynchronisationAction) obj) == 0;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return getMusicTrack().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MusicTrack getMusicTrack() {
			return musicTrack;
		}
		
		/**
		 * @return the descriptiveType
		 */
		public String getDescriptiveType() {
			return descriptiveType;
		}
	}
}
