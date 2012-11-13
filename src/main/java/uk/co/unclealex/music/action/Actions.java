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

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.files.FileLocation;

import com.google.common.base.Supplier;

/**
 * An interface for building lists of {@link Action}s.
 * 
 * @author alex
 * 
 */
public interface Actions extends Supplier<List<Action>>, Iterable<Action> {

  /**
   * Add a new {@link AddArtworkAction}.
   * 
   * @param fileLocation
   *          The {@link FileLocation} that requires cover art to be downloaded.
   * @param coverArtUri
   *          The location where cover art can be downloaded from.
   * @return An {@link Actions} object with the new {@link Action} added to its
   *         actions.
   */
  public Actions addArtwork(FileLocation fileLocation, URI coverArtUri);

  /**
   * Add a new {@link CoverArtAction}.
   * 
   * @param flacFileLocation
   *          The location of the FLAC file that needs cover art downloaded.
   * @return An {@link Actions} object with the new {@link Action} added to its
   *         actions.
   */
  public Actions coverArt(FileLocation flacFileLocation);

  /**
   * Add a new {@link DeleteAction}.
   * 
   * @param fileLocation
   *          The location of the file to remove.
   * @return An {@link Actions} object with the new {@link Action} added to its
   *         actions.
   */
  public Actions delete(FileLocation fileLocation);

  /**
   * Add a new {@link EncodeAction}.
   * 
   * @param fileLocation
   *          The location of the FLAC file to encode.
   * @param encodedFileLocation
   *          The location of newly encoded MP3 file.
   * @param flacMusicFile
   *          The tagging information of the FLAC file.
   * @return An {@link Actions} object with the new {@link Action} added to its
   *         actions.
   */
  public Actions encode(FileLocation fileLocation, FileLocation encodedFileLocation, MusicFile flacMusicFile);

  /**
   * Add a new {@link FailureAction}.
   * 
   * @param fileLocation
   *          The location of the file that has caused a failure.
   * @param messageTemplate
   *          The template of the message to display to the user.
   * @param parameters
   *          A list of parameters used to construct the message.
   * @return An {@link Actions} object with the new {@link Action} added to its
   *         actions.
   */
  public Actions fail(FileLocation fileLocation, String messageTemplate, Object... parameters);

  /**
   * Add a new {@link MoveAction}.
   * 
   * @param fileLocation
   *          The location of the file to move.
   * @param targetFileLocation
   *          The location to where the file will be moved.
   * @return An {@link Actions} object with the new {@link Action} added to its
   *         actions.
   */
  public Actions move(FileLocation fileLocation, FileLocation targetFileLocation);

  /**
   * Add a new {@link LinkAction}.
   * 
   * @param encodedFileLocation
   *          The location of the file location to link to.
   * @param users
   *          The users who own the file.
   * @return An {@link Actions} object with the new {@link Action} added to its
   *         actions.
   */
  public Actions link(FileLocation encodedFileLocation, Iterable<User> users);

  /**
   * Add a new {@link LinkAction}.
   * 
   * @param encodedFileLocation
   *          The location of the file location to unlink from.
   * @param users
   *          The users who own the file.
   * @return An {@link Actions} object with the new {@link Action} added to its
   *         actions.
   */
  public Actions unlink(FileLocation encodedFileLocation, Iterable<User> users);

  /**
   * Add a new {@link Action}.
   * 
   * @param action
   *          The {@link Action} to add.
   * @return An {@link Actions} object with the new {@link Action} added to its
   *         actions.
   */
  public Actions then(Action action);

  /**
   * Add a new list of {@link Action}s.
   * 
   * @param actions
   *          The {@link Actions} object containing all the {@link Action}s.
   * @return An {@link Actions} object with the new {@link Action}s added to its
   *         actions.
   */
  public Actions then(Actions actions);

}