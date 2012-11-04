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

import java.util.List;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.files.FileLocation;

import com.google.common.collect.Lists;

/**
 * The default implentation of {@link Actions}.
 * 
 * @author alex
 */
public class ActionsImpl implements Actions {


  /**
   * {@inheritDoc}
   */
  @Override
  public List<Action> get() {
    return Lists.newArrayList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Actions coverArt(FileLocation flacFileLocation) {
    return actions().coverArt(flacFileLocation);
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public Actions delete(FileLocation fileLocation) {
    return actions().delete(fileLocation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Actions encode(FileLocation fileLocation, FileLocation encodedFileLocation, MusicFile flacMusicFile) {
    return actions().encode(fileLocation, encodedFileLocation, flacMusicFile);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Actions fail(FileLocation fileLocation, String messageTemplate, Object... parameters) {
    return actions().fail(fileLocation, messageTemplate, parameters);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Actions move(FileLocation fileLocation, FileLocation targetFileLocation) {
    return actions().move(fileLocation, targetFileLocation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Actions protect(FileLocation fileLocation) {
    return actions().protect(fileLocation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Actions unprotect(FileLocation fileLocation) {
    return actions().unprotect(fileLocation);
  }

  @Override
  public Actions then(Actions actions) {
    return actions;
  }
  
  protected Actions actions() {
    return new ListOfActions();
  }
  
  /**
   * An internal, stateful, implementation of {@link Actions}.
   */
  class ListOfActions implements Actions {

    /**
     * The list of {@link Action}s held by this {@link Actions} object.
     */
    private final List<Action> actions = Lists.newArrayList();

    /**
     * {@inheritDoc}
     */
    @Override
    public Actions coverArt(FileLocation flacFileLocation) {
      getActions().add(new CoverArtAction(flacFileLocation));
      return this;
    }
    
    /**
     * Add a new {@link DeleteAction}.
     * 
     * @param fileLocation
     *          The location of the file to remove.
     * @return An {@link Actions} object with the new {@link Action} added to
     *         its actions.
     */
    @Override
    public Actions delete(FileLocation fileLocation) {
      getActions().add(new DeleteAction(fileLocation));
      return this;
    }

    /**
     * Add a new {@link EncodeAction}.
     * 
     * @param fileLocation
     *          The location of the FLAC file to encode.
     * @param encodedFileLocation
     *          The location of newly encoded MP3 file.
     * @param flacMusicFile
     *          The tagging information of the FLAC file.
     * @return An {@link Actions} object with the new {@link Action} added to
     *         its actions.
     */
    @Override
    public Actions encode(FileLocation fileLocation, FileLocation encodedFileLocation, MusicFile flacMusicFile) {
      getActions().add(new EncodeAction(fileLocation, encodedFileLocation, flacMusicFile));
      return this;
    }

    /**
     * Add a new {@link FailureAction}.
     * 
     * @param fileLocation
     *          The location of the file that has caused a failure.
     * @param messageTemplate
     *          The template of the message to display to the user.
     * @param parameters
     *          A list of parameters used to construct the message.
     * @return An {@link Actions} object with the new {@link Action} added to
     *         its actions.
     */
    @Override
    public Actions fail(FileLocation fileLocation, String messageTemplate, Object... parameters) {
      getActions().add(new FailureAction(fileLocation, messageTemplate, parameters));
      return this;
    }

    /**
     * Add a new {@link MoveAction}.
     * 
     * @param fileLocation
     *          The location of the file to move.
     * @param targetFileLocation
     *          The location to where the file will be moved.
     * @return An {@link Actions} object with the new {@link Action} added to
     *         its actions.
     */
    @Override
    public Actions move(FileLocation fileLocation, FileLocation targetFileLocation) {
      getActions().add(new MoveAction(fileLocation, targetFileLocation));
      return this;
    }

    /**
     * Add a new {@link ProtectAction}.
     * 
     * @param fileLocation
     *          The location of the file to write protect.
     * @return An {@link Actions} object with the new {@link Action} added to
     *         its actions.
     */
    @Override
    public Actions protect(FileLocation fileLocation) {
      getActions().add(new ProtectAction(fileLocation));
      return this;
    }

    /**
     * Add a new {@link UnprotectAction}.
     * 
     * @param fileLocation
     *          The location of the file from which to remove write protection.
     * @return An {@link Actions} object with the new {@link Action} added to
     *         its actions.
     */
    @Override
    public Actions unprotect(FileLocation fileLocation) {
      getActions().add(new UnprotectAction(fileLocation));
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Actions then(Actions actions) {
      getActions().addAll(actions.get());
      return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Action> get() {
      return getActions();
    }
    
    /**
     * Gets the list of {@link Action}s held by this {@link Actions} object.
     * 
     * @return the list of {@link Action}s held by this {@link Actions} object
     */
    public List<Action> getActions() {
      return actions;
    }
  }
}
