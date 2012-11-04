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

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.files.FileLocation;

/**
 * An action used add artwork to a FLAC file.
 * 
 * @author alex
 * 
 */
public class AddArtworkAction extends AbstractAction implements Action {

  /**
   * The {@link MusicFile} associated with the file {@link #location}.
   */
  private final MusicFile musicFile;

  /**
   * The URL where cover art information is available.
   */
  private final String coverArtUrl;
    
  /**
   * Instantiates a new adds the artwork action.
   *
   * @param fileLocation the location
   * @param musicFile the music file
   * @param coverArtUrl the cover art url
   */
  public AddArtworkAction(FileLocation fileLocation, MusicFile musicFile, String coverArtUrl) {
    super(fileLocation);
    this.musicFile = musicFile;
    this.coverArtUrl = coverArtUrl;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(ActionVisitor actionVisitor) throws IOException {
    actionVisitor.visit(this);
  }

  /**
   * Gets the {@link MusicFile} associated with the file {@link #location}.
   *
   * @return the {@link MusicFile} associated with the file {@link #location}
   */
  public MusicFile getMusicFile() {
    return musicFile;
  }

  /**
   * Gets the URL where cover art information is available.
   *
   * @return the URL where cover art information is available
   */
  public String getCoverArtUrl() {
    return coverArtUrl;
  }
}
