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

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.command.Execution;
import uk.co.unclealex.music.command.OwnCommand;
import uk.co.unclealex.music.command.OwnCommandLine;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.message.MessageService;
import uk.co.unclealex.music.musicbrainz.OwnerService;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The {@link Execution} for the {@link OwnCommand}.
 * 
 * @author alex
 * 
 */
public class OwnExecution implements Execution<OwnCommandLine> {

  /**
   * The {@link OwnerService} used to determine current ownership.
   */
  private final OwnerService ownerService;

  /**
   * The list of all known users.
   */
  private final List<User> users;

  /**
   * True if any releases are to be given new owners, false otherwise.
   */
  private boolean addOwners;

  /**
   * The list of owners specified in the command line.
   */
  private final List<User> owners = Lists.newArrayList();

  @Inject
  public OwnExecution(OwnerService ownerService, List<User> users) {
    super();
    this.ownerService = ownerService;
    this.users = users;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Actions initialise(Actions actions, OwnCommandLine commandLine) {
    setAddOwners(!commandLine.isDisown());
    Iterable<String> ownerNames = Splitter.on(',').trimResults().omitEmptyStrings().split(commandLine.getOwners());
    Function<User, String> nameFunction = new Function<User, String>() {
      public String apply(User user) {
        return user.getName();
      }
    };
    Map<String, User> usersByUsername = Maps.uniqueIndex(getUsers(), nameFunction);
    List<String> unknownOwners = Lists.newArrayList();
    List<User> owners = Lists.newArrayList();
    for (String ownerName : ownerNames) {
      User owner = usersByUsername.get(ownerName);
      if (owner == null) {
        unknownOwners.add(ownerName);
      }
      else {
        owners.add(owner);
      }
    }
    if (!unknownOwners.isEmpty()) {
      for (String unknownOwner : unknownOwners) {
        actions = actions.fail(null, MessageService.UNKNOWN_USER, unknownOwner);
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
  public Actions execute(Actions actions, FileLocation flacFileLocation, MusicFile musicFile) throws IOException {
    boolean addOwners = isAddOwners();
    Set<User> currentOwners = getOwnerService().getOwnersForMusicFile(musicFile);
    List<User> ownersToChange;
    List<User> owners = getOwners();
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

  public OwnerService getOwnerService() {
    return ownerService;
  }

  public List<User> getUsers() {
    return users;
  }

  public List<User> getOwners() {
    return owners;
  }

}
