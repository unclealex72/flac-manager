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

package uk.co.unclealex.music.action;

import java.io.IOException;
import java.util.List;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.files.FileLocation;

/**
 * An action used to indicate that ownership of a release needs to be changed.
 * 
 * @author alex
 * 
 */
public class ChangeOwnerAction extends AbstractAction implements Action {

  /**
   * True if owners are to be added, false otherwise.
   */
  private final boolean addOwner;
  
  /**
   * The owners who are to be added or removed.
   */
  private final List<User> newOwners;

  /**
   * The {@link MusicFile} whose ownership is being changed.
   */
  private final MusicFile musicFile;
  
  /**
   * Instantiates a new change owner action.
   *
   * @param fileLocation the file location
   * @param musicFile the music file
   * @param addOwner the add owner
   * @param newOwners the new owners
   */
  public ChangeOwnerAction(FileLocation fileLocation, MusicFile musicFile, boolean addOwner, List<User> newOwners) {
    super(fileLocation);
    this.addOwner = addOwner;
    this.newOwners = newOwners;
    this.musicFile = musicFile;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(ActionVisitor actionVisitor) throws IOException {
    actionVisitor.visit(this);
  }

  /**
   * Checks if is true if owners are to be added, false otherwise.
   *
   * @return the true if owners are to be added, false otherwise
   */
  public boolean isAddOwner() {
    return addOwner;
  }

  /**
   * Gets the owners who are to be added or removed.
   *
   * @return the owners who are to be added or removed
   */
  public List<User> getNewOwners() {
    return newOwners;
  }

  /**
   * Gets the {@link MusicFile} whose ownership is being changed.
   *
   * @return the {@link MusicFile} whose ownership is being changed
   */
  public MusicFile getMusicFile() {
    return musicFile;
  }
}
