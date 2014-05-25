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

package uk.co.unclealex.music.command.own;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.command.JExecution;
import uk.co.unclealex.music.command.JOwnCommandLine;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.message.JMessageService;
import uk.co.unclealex.music.musicbrainz.JOwnerService;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The {@link uk.co.unclealex.music.command.JExecution} for the {@link uk.co.unclealex.music.command.JOwnCommand}.
 * 
 * @author alex
 * 
 */
public class JOwnExecution implements JExecution<JOwnCommandLine> {

  /**
   * The {@link uk.co.unclealex.music.musicbrainz.JOwnerService} used to determine current ownership.
   */
  private final JOwnerService ownerService;

  /**
   * The list of all known users.
   */
  private final List<JUser> users;

  /**
   * True if any releases are to be given new owners, false otherwise.
   */
  private boolean addOwners;

  /**
   * The list of owners specified in the command line.
   */
  private final List<JUser> owners = Lists.newArrayList();

  @Inject
  public JOwnExecution(JOwnerService ownerService, List<JUser> users) {
    super();
    this.ownerService = ownerService;
    this.users = users;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions initialise(JActions actions, JOwnCommandLine commandLine) {
    setAddOwners(!commandLine.isDisown());
    Iterable<String> ownerNames = Splitter.on(',').trimResults().omitEmptyStrings().split(commandLine.getOwners());
    Function<JUser, String> nameFunction = new Function<JUser, String>() {
      public String apply(JUser user) {
        return user.getName();
      }
    };
    Map<String, JUser> usersByUsername = Maps.uniqueIndex(getUsers(), nameFunction);
    List<String> unknownOwners = Lists.newArrayList();
    List<JUser> owners = Lists.newArrayList();
    for (String ownerName : ownerNames) {
      JUser owner = usersByUsername.get(ownerName);
      if (owner == null) {
        unknownOwners.add(ownerName);
      }
      else {
        owners.add(owner);
      }
    }
    if (!unknownOwners.isEmpty()) {
      for (String unknownOwner : unknownOwners) {
        actions = actions.fail(null, JMessageService.UNKNOWN_USER, unknownOwner);
      }
    }
    else {
      getOwners().addAll(owners);
    }
    return actions;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions execute(JActions actions, JFileLocation flacFileLocation, JMusicFile musicFile) throws IOException {
    boolean addOwners = isAddOwners();
    Set<JUser> currentOwners = getOwnerService().getOwnersForMusicFile(musicFile);
    List<JUser> ownersToChange;
    List<JUser> owners = getOwners();
    if (addOwners) {
      ownersToChange = Lists.newArrayList(owners);
      ownersToChange.removeAll(currentOwners);
    }
    else {
      ownersToChange = Lists.newArrayList(currentOwners);
      ownersToChange.retainAll(owners);
    }
    if (!ownersToChange.isEmpty()) {
      actions = actions.changeOwnership(flacFileLocation, musicFile, addOwners, ownersToChange);
    }
    return actions;
  }

  public boolean isAddOwners() {
    return addOwners;
  }

  public void setAddOwners(boolean addOwners) {
    this.addOwners = addOwners;
  }

  public JOwnerService getOwnerService() {
    return ownerService;
  }

  public List<JUser> getUsers() {
    return users;
  }

  public List<JUser> getOwners() {
    return owners;
  }

}
