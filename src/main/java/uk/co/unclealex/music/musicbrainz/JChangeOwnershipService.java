/**
 * Copyright 2012 Alex Jones
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

package uk.co.unclealex.music.musicbrainz;

import java.util.List;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.configuration.JUser;

/**
 * An interface for classes that can tracks changes in ownership and then
 * finally commit these changes to the {@link JOwnerService}.
 * 
 * @author alex
 * 
 */
public interface JChangeOwnershipService {

  /**
   * Track a change of ownership
   * 
   * @param musicFile
   *          The {@link uk.co.unclealex.music.JMusicFile} whose ownership is being changed.
   * @param addOwner
   *          True if the owners are to be added, false otherwise.
   * @param newOwners
   *          The new owners to add or remove.
   */
  public void changeOwnership(JMusicFile musicFile, boolean addOwner, List<JUser> newOwners);

  /**
   * Commit all changes to ownership to MusicBrainz.
   */
  public void commitChanges();
}
