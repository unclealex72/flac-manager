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

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.configuration.JUser;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author alex
 *
 */
public class JChangeOwnershipServiceImpl implements JChangeOwnershipService {

  private static final Logger log = LoggerFactory.getLogger(JChangeOwnershipServiceImpl.class);
  
  /**
   * A map containing a collection of all the MusicBrainz release IDs to be added to a user's collection.
   */
  private final Multimap<JUser, String> additionsByOwner = HashMultimap.create();
  
  /**
   * A map containing a collection of all the MusicBrainz release IDs to be removed from a user's collection.
   */
  private final Multimap<JUser, String> removalsByOwner = HashMultimap.create();
  
  /**
   * The {@link JMusicBrainzClient} used to actually alter users' collections.
   */
  private final JMusicBrainzClient musicBrainzClient;
  
  @Inject
  public JChangeOwnershipServiceImpl(JMusicBrainzClient musicBrainzClient) {
    super();
    this.musicBrainzClient = musicBrainzClient;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void commitChanges() {
    ReleaseCallback removeReleaseCallback = new ReleaseCallback() {
      @Override
      public void execute(JMusicBrainzClient musicBrainzClient, JUser user, Iterable<String> releaseIds) throws JNoCollectionException {
        musicBrainzClient.removeReleases(user, releaseIds);
      }
    };
    ReleaseCallback addReleaseCallback = new ReleaseCallback() {
      @Override
      public void execute(JMusicBrainzClient musicBrainzClient, JUser user, Iterable<String> releaseIds) throws JNoCollectionException {
        musicBrainzClient.addReleases(user, releaseIds);
      }
    };
    changeReleases(getRemovalsByOwner(), removeReleaseCallback);
    changeReleases(getAdditionsByOwner(), addReleaseCallback);
  }

  protected void changeReleases(Multimap<JUser, String> changeset, ReleaseCallback releaseCallback) {
    JMusicBrainzClient musicBrainzClient = getMusicBrainzClient();
    for (Entry<JUser, Collection<String>> entry : changeset.asMap().entrySet()) {
      JUser user = entry.getKey();
      Collection<String> releaseIds = entry.getValue();
      try {
        releaseCallback.execute(musicBrainzClient, user, releaseIds);
      }
      catch (JNoCollectionException e) {
        log.error("Cannot change the releases collection for user " + user.getName(), e);
      }
    }
  }
  
  /**
   * A callback interface that can be used to change a user's collection of releases.
   */
  interface ReleaseCallback {
    
    /**
     * Alter a user's releases.
     * @param musicBrainzClient The {@link JMusicBrainzClient} used to actually do the work.
     * @param user The user whose releases are to be changed.
     * @param releaseIds The IDs of the releases to change.
     * @throws JNoCollectionException
     */
    public void execute(JMusicBrainzClient musicBrainzClient, JUser user, Iterable<String> releaseIds) throws JNoCollectionException;
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public void changeOwnership(JMusicFile musicFile, boolean addOwner, List<JUser> newOwners) {
    Multimap<JUser, String> mapToChange = addOwner ? getAdditionsByOwner() : getRemovalsByOwner();
    for (JUser owner : newOwners) {
      mapToChange.put(owner, musicFile.getAlbumId());
    }
  }

  public Multimap<JUser, String> getAdditionsByOwner() {
    return additionsByOwner;
  }

  public Multimap<JUser, String> getRemovalsByOwner() {
    return removalsByOwner;
  }

  public JMusicBrainzClient getMusicBrainzClient() {
    return musicBrainzClient;
  }

}
