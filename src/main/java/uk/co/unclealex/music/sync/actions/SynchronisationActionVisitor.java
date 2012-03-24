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

/**
 * A visitor for {@link SynchronisationAction}s.
 * 
 * @author alex
 * 
 */
public interface SynchronisationActionVisitor {

	/**
	 * Visit a {@link SynchronisationAction}.
	 * @param synchronisationAction The {@link SynchronisationAction} to visit.
	 */
	public void visit(SynchronisationAction synchronisationAction);

	/**
	 * Visit a {@link TrackAddedAction}.
	 * @param trackAddedAction The {@link TrackAddedAction} to visit.
	 */
	public void visit(TrackAddedAction trackAddedAction);

	/**
	 * Visit a {@link TrackUpdatedAction}.
	 * @param trackUpdatedAction The {@link TrackUpdatedAction} to visit.
	 */
	public void visit(TrackUpdatedAction trackUpdatedAction);

	/**
	 * Visit a {@link TrackRemovedAction}.
	 * @param trackRemovedAction The {@link TrackRemovedAction} to visit.
	 */
	public void visit(TrackRemovedAction trackRemovedAction);

	public abstract class Default implements SynchronisationActionVisitor {

		/**
		 * Throw an exception.
		 * @throws IllegalArgumentException This is always thrown.
		 */
		public final void visit(SynchronisationAction synchronisationAction) throws IllegalArgumentException {
			throw new IllegalArgumentException(synchronisationAction.getClass()
					+ " is an unknown type of synchronisation action.");
		}
	}
}
