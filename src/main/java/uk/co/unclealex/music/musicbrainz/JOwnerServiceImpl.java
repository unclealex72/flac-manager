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
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.configuration.JUser;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The default implementation of {@link JOwnerService}.
 * @author alex
 *
 */
@Singleton
public class JOwnerServiceImpl implements JOwnerService {

  /**
   * The {@link JMusicBrainzClient} used to talk to MusicBrainz.
   */
  private final JMusicBrainzClient musicBrainzClient;
  
  /**
   * The list of known users.
   */
  private final List<JUser> users;
  
  /**
   * A map of users and all all the releases they own.
   */
  private final Map<JUser, Collection<String>> owners = Maps.newHashMap();

  /**
   * A set of all releases owned by at least one person.
   */
  private final Set<String> allOwnedReleases = Sets.newTreeSet();

  /**
   * A set of all users whose collection could not be found.
   */
  private final Set<JUser> allInvalidOwners = Sets.newHashSet();
  
  /**
   * Instantiates a new owner service impl.
   *
   * @param musicBrainzClient the music brainz client
   * @param users the users
   */
  @Inject
  public JOwnerServiceImpl(JMusicBrainzClient musicBrainzClient, List<JUser> users) {
    super();
    this.musicBrainzClient = musicBrainzClient;
    this.users = users;
  }

  /**
   * Populate all known owned release information.
   */
  @PostConstruct
  public void setup() {
    Map<JUser, Collection<String>> owners = getOwners();
    JMusicBrainzClient musicBrainzClient = getMusicBrainzClient();
    Set<String> allOwnedReleases = getAllOwnedReleases();
    Set<JUser> allInvalidOwners = getAllInvalidOwners();
    for (JUser user : getUsers()) {
      try {
        List<String> relasesForOwner = musicBrainzClient.getRelasesForOwner(user);
        owners.put(user, relasesForOwner);
        allOwnedReleases.addAll(relasesForOwner);
      }
      catch (JNoCollectionException e) {
        allInvalidOwners.add(user);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<JUser> getOwnersForMusicFile(JMusicFile musicFile) {
    final String releaseId = releaseIdOf(musicFile);
    Predicate<Collection<String>> ownsPredicate = new Predicate<Collection<String>>() {
      public boolean apply(Collection<String> releases) {
        return releases.contains(releaseId);
      }
    };
    return Maps.filterValues(getOwners(), ownsPredicate).keySet();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFileOwnedByAnyone(JMusicFile musicFile) {
    return getAllOwnedReleases().contains(releaseIdOf(musicFile));
  }

  /**
   * Release id of.
   *
   * @param musicFile the music file
   * @return the string
   */
  protected String releaseIdOf(JMusicFile musicFile) {
    return musicFile.getAlbumId();
  }
  
  /**
   * Gets the {@link JMusicBrainzClient} used to talk to MusicBrainz.
   *
   * @return the {@link JMusicBrainzClient} used to talk to MusicBrainz
   */
  public JMusicBrainzClient getMusicBrainzClient() {
    return musicBrainzClient;
  }

  /**
   * Gets the list of known users.
   *
   * @return the list of known users
   */
  public List<JUser> getUsers() {
    return users;
  }

  /**
   * Gets the a map of users and all all the releases they own.
   *
   * @return the a map of users and all all the releases they own
   */
  public Map<JUser, Collection<String>> getOwners() {
    return owners;
  }

  /**
   * Gets the a set of all releases owned by at least one person.
   *
   * @return the a set of all releases owned by at least one person
   */
  public Set<String> getAllOwnedReleases() {
    return allOwnedReleases;
  }

  /**
   * Gets the a set of all users whose collection could not be found.
   *
   * @return the a set of all users whose collection could not be found
   */
  public Set<JUser> getAllInvalidOwners() {
    return allInvalidOwners;
  }

}
