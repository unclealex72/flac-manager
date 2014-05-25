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
import java.util.List;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.files.JFileLocation;

import com.google.common.base.Supplier;

/**
 * An interface for building lists of {@link JAction}s.
 * 
 * @author alex
 * 
 */
public interface JActions extends Supplier<List<JAction>>, Iterable<JAction> {

  /**
   * Add a new {@link JAddArtworkAction}.
   * 
   * @param fileLocation
   *          The {@link uk.co.unclealex.music.files.JFileLocation} that requires cover art to be downloaded.
   * @param coverArtUri
   *          The location where cover art can be downloaded from.
   * @return An {@link JActions} object with the new {@link JAction} added to its
   *         actions.
   */
  public JActions addArtwork(JFileLocation fileLocation, URI coverArtUri);

  /**
   * Add a new {@link JCoverArtAction}.
   * 
   * @param flacFileLocation
   *          The location of the FLAC file that needs cover art downloaded.
   * @return An {@link JActions} object with the new {@link JAction} added to its
   *         actions.
   */
  public JActions coverArt(JFileLocation flacFileLocation);

  /**
   * Add a new {@link JDeleteAction}.
   * 
   * @param fileLocation
   *          The location of the file to remove.
   * @return An {@link JActions} object with the new {@link JAction} added to its
   *         actions.
   */
  public JActions delete(JFileLocation fileLocation);

  /**
   * Add a new {@link JEncodeAction}.
   * 
   * @param fileLocation
   *          The location of the FLAC file to encode.
   * @param encodedFileLocation
   *          The location of newly encoded MP3 file.
   * @param flacMusicFile
   *          The tagging information of the FLAC file.
   * @return An {@link JActions} object with the new {@link JAction} added to its
   *         actions.
   */
  public JActions encode(JFileLocation fileLocation, JFileLocation encodedFileLocation, JMusicFile flacMusicFile);

  /**
   * Add a new {@link JFailureAction}.
   * 
   * @param fileLocation
   *          The location of the file that has caused a failure.
   * @param messageTemplate
   *          The template of the message to display to the user.
   * @param parameters
   *          A list of parameters used to construct the message.
   * @return An {@link JActions} object with the new {@link JAction} added to its
   *         actions.
   */
  public JActions fail(JFileLocation fileLocation, String messageTemplate, Object... parameters);

  /**
   * Add a new {@link JMoveAction}.
   * 
   * @param fileLocation
   *          The location of the file to move.
   * @param targetFileLocation
   *          The location to where the file will be moved.
   * @return An {@link JActions} object with the new {@link JAction} added to its
   *         actions.
   */
  public JActions move(JFileLocation fileLocation, JFileLocation targetFileLocation);

  /**
   * Add a new {@link JLinkAction}.
   * 
   * @param encodedFileLocation
   *          The location of the file location to link to.
   * @param users
   *          The users who own the file.
   * @return An {@link JActions} object with the new {@link JAction} added to its
   *         actions.
   */
  public JActions link(JFileLocation encodedFileLocation, Iterable<JUser> users);

  /**
   * Add a new {@link JLinkAction}.
   * 
   * @param encodedFileLocation
   *          The location of the file location to unlink from.
   * @param users
   *          The users who own the file.
   * @return An {@link JActions} object with the new {@link JAction} added to its
   *         actions.
   */
  public JActions unlink(JFileLocation encodedFileLocation, Iterable<JUser> users);

  /**
   * Add a new {@link JChangeOwnerAction}.
   * 
   * @param flacFileLocation
   *          The location of the FLAC file whose ownership is to be changed.
   * @param musicFile
   *          The music file whose ownership is to be changed.
   * @param addOwners
   *          True if owners are to be added, false otherwise.
   * @param ownersToChange
   *          The owners who are being added or removed.
   * @return An {@link JActions} object with the new {@link JAction} added to its
   *         actions.
   */
  public JActions changeOwnership(
      JFileLocation flacFileLocation,
      JMusicFile musicFile,
      boolean addOwners,
      List<JUser> ownersToChange);

  /**
   * Add a new {@link JUpdateOwnershipAction}.
   */
  public JActions updateOwnership();

  /**
   * Add a new {@link JAction}.
   * 
   * @param action
   *          The {@link JAction} to add.
   * @return An {@link JActions} object with the new {@link JAction} added to its
   *         actions.
   */
  public JActions then(JAction action);

  /**
   * Add a new list of {@link JAction}s.
   * 
   * @param actions
   *          The {@link JActions} object containing all the {@link JAction}s.
   * @return An {@link JActions} object with the new {@link JAction}s added to its
   *         actions.
   */
  public JActions then(JActions actions);

}