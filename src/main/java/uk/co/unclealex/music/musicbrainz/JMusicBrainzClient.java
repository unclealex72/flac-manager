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

import uk.co.unclealex.music.configuration.JUser;

/**
 * <p>
 * An interface for classes that handle high level interactions with the
 * MusicBrainz web services.
 * </p>
 * <p>
 * When considering a user's collection, if a user has only one collection then
 * that shall be used. Otherwise, the user must have a collection called <i>All
 * my music</i> defined.
 * </p>
 * 
 * @author alex
 * 
 */
public interface JMusicBrainzClient {

  /**
   * Get all the releases owned by a user. If the user only has one collection
   * then this shall be used. Otherwise, only a collection called <i>All my
   * music</i> will be searched.
   * 
   * @param user
   *          The user who is doing the searching.
   * @return A list of all the MusicBrainz releases owned by the user.
   * @throws thrown if a unique collection cannot be found.
   */
  public List<String> getRelasesForOwner(JUser user) throws JNoCollectionException;

  /**
   * Add releases to an owner's collection.
   * @param user The user whose collection needs changing.
   * @param newReleaseIds The new releases to add to the user's collection.
   * @throws thrown if a unique collection cannot be found.
   */
  public void addReleases(JUser user, Iterable<? extends String> newReleaseIds) throws JNoCollectionException;

  /**
   * Remove releases from an owner's collection.
   * @param user The user whose collection needs changing.
   * @param oldReleaseIds The old releases to remove from the user's collection.
   * @throws thrown if a unique collection cannot be found.
   */
  public void removeReleases(JUser user, Iterable<? extends String> oldReleaseIds) throws JNoCollectionException;
}
