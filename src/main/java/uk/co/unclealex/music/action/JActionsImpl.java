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

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.files.JFileLocation;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * The default implentation of {@link JActions}.
 * 
 * @author alex
 */
public class JActionsImpl implements JActions {

  /**
   * {@inheritDoc}
   */
  @Override
  public List<JAction> get() {
    return Lists.newArrayList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions addArtwork(JFileLocation fileLocation, URI coverArtUri) {
    return actions().addArtwork(fileLocation, coverArtUri);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public JActions coverArt(JFileLocation flacFileLocation) {
    return actions().coverArt(flacFileLocation);
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public JActions delete(JFileLocation fileLocation) {
    return actions().delete(fileLocation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions encode(JFileLocation fileLocation, JFileLocation encodedFileLocation, JMusicFile flacMusicFile) {
    return actions().encode(fileLocation, encodedFileLocation, flacMusicFile);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions fail(JFileLocation fileLocation, String messageTemplate, Object... parameters) {
    return actions().fail(fileLocation, messageTemplate, parameters);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions move(JFileLocation fileLocation, JFileLocation targetFileLocation) {
    return actions().move(fileLocation, targetFileLocation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions link(JFileLocation encodedFileLocation, Iterable<JUser> users) {
    return actions().link(encodedFileLocation, users);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public JActions unlink(JFileLocation encodedFileLocation, Iterable<JUser> users) {
    return actions().unlink(encodedFileLocation, users);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public JActions changeOwnership(JFileLocation flacFileLocation, JMusicFile musicFile, boolean addOwners, List<JUser> ownersToChange) {
    return actions().changeOwnership(flacFileLocation, musicFile, addOwners, ownersToChange);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public JActions updateOwnership() {
    return actions().updateOwnership();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public JActions then(JAction action) {
    return new ListOfActions().then(action);
  }
  
  @Override
  public JActions then(JActions actions) {
    return new ListOfActions().then(actions);
  }
  
  protected JActions actions() {
    return new ListOfActions();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<JAction> iterator() {
    return Iterators.emptyIterator();
  }
  
  /**
   * An internal, stateful, implementation of {@link JActions}.
   */
  class ListOfActions implements JActions {

    /**
     * The list of {@link JAction}s held by this {@link JActions} object.
     */
    private final List<JAction> actions = Lists.newArrayList();

    /**
     * {@inheritDoc}
     */
    @Override
    public JActions addArtwork(JFileLocation fileLocation, URI coverArtUri) {
      getActions().add(new JAddArtworkAction(fileLocation, coverArtUri));
      return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public JActions coverArt(JFileLocation flacFileLocation) {
      getActions().add(new JCoverArtAction(flacFileLocation));
      return this;
    }
    
    /**
     * Add a new {@link JDeleteAction}.
     * 
     * @param fileLocation
     *          The location of the file to remove.
     * @return An {@link JActions} object with the new {@link JAction} added to
     *         its actions.
     */
    @Override
    public JActions delete(JFileLocation fileLocation) {
      getActions().add(new JDeleteAction(fileLocation));
      return this;
    }

    /**
     * Add a new {@link JEncodeAction}.
     * 
     * @param fileLocation
     *          The location of the FLAC file to encode.
     * @param encodedFileLocation
     *          The location of newly encoded MP3 file.
     * @param flacMusicFile
     *          The tagging information of the FLAC file.
     * @return An {@link JActions} object with the new {@link JAction} added to
     *         its actions.
     */
    @Override
    public JActions encode(JFileLocation fileLocation, JFileLocation encodedFileLocation, JMusicFile flacMusicFile) {
      getActions().add(new JEncodeAction(fileLocation, encodedFileLocation, flacMusicFile));
      return this;
    }

    /**
     * Add a new {@link JFailureAction}.
     * 
     * @param fileLocation
     *          The location of the file that has caused a failure.
     * @param messageTemplate
     *          The template of the message to display to the user.
     * @param parameters
     *          A list of parameters used to construct the message.
     * @return An {@link JActions} object with the new {@link JAction} added to
     *         its actions.
     */
    @Override
    public JActions fail(JFileLocation fileLocation, String messageTemplate, Object... parameters) {
      getActions().add(new JFailureAction(fileLocation, messageTemplate, parameters));
      return this;
    }

    /**
     * Add a new {@link JMoveAction}.
     * 
     * @param fileLocation
     *          The location of the file to move.
     * @param targetFileLocation
     *          The location to where the file will be moved.
     * @return An {@link JActions} object with the new {@link JAction} added to
     *         its actions.
     */
    @Override
    public JActions move(JFileLocation fileLocation, JFileLocation targetFileLocation) {
      getActions().add(new JMoveAction(fileLocation, targetFileLocation));
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JActions link(JFileLocation encodedFileLocation, Iterable<JUser> owners) {
      getActions().add(new JLinkAction(encodedFileLocation, owners));
      return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public JActions unlink(JFileLocation encodedFileLocation, Iterable<JUser> owners) {
      getActions().add(new JUnlinkAction(encodedFileLocation, owners));
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JActions changeOwnership(JFileLocation flacFileLocation, JMusicFile musicFile, boolean addOwners, List<JUser> ownersToChange) {
      getActions().add(new JChangeOwnerAction(flacFileLocation, musicFile, addOwners, ownersToChange));
      return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public JActions updateOwnership() {
      getActions().add(new JUpdateOwnershipAction());
      return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public JActions then(JAction action) {
      getActions().add(action);
      return this;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public JActions then(JActions actions) {
      getActions().addAll(actions.get());
      return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<JAction> iterator() {
      return getActions().iterator();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<JAction> get() {
      return getActions();
    }
    
    /**
     * Gets the list of {@link JAction}s held by this {@link JActions} object.
     * 
     * @return the list of {@link JAction}s held by this {@link JActions} object
     */
    public List<JAction> getActions() {
      return actions;
    }
  }
}
