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

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.configuration.User;

/**
 * An interface for classes that can tracks changes in ownership and then
 * finally commit these changes to the {@link OwnerService}.
 * 
 * @author alex
 * 
 */
public interface ChangeOwnershipService {

  /**
   * Track a change of ownership
   * 
   * @param musicFile
   *          The {@link MusicFile} whose ownership is being changed.
   * @param addOwner
   *          True if the owners are to be added, false otherwise.
   * @param newOwners
   *          The new owners to add or remove.
   */
  public void changeOwnership(MusicFile musicFile, boolean addOwner, List<User> newOwners);

  /**
   * Commit all changes to ownership to MusicBrainz.
   */
  public void commitChanges();
}
