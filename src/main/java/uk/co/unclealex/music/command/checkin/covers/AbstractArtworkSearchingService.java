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

package uk.co.unclealex.music.command.checkin.covers;

import java.io.IOException;
import java.net.URI;

import uk.co.unclealex.music.MusicFile;

/**
 * A base class for {@link ArtworkSearchingService}s that allow for null-safe
 * checking of a property of a {@link MusicFile}.
 * 
 * @author alex
 * 
 */
public abstract class AbstractArtworkSearchingService implements ArtworkSearchingService {

  /**
   * {@inheritDoc}
   */
  @Override
  public URI findArtwork(MusicFile musicFile) throws IOException {
    String albumIdentifier = locateAlbumIdentifier(musicFile);
    return albumIdentifier == null ? null : findArtwork(albumIdentifier);
  }

  /**
   * Find an album's artwork given an album identifier.
   * 
   * @param albumIdentifier
   *          The unique string used to find the album. This will not be null.
   * @return The URI for the album artwork or null if none could be found.
   * @throws IOException
   */
  protected abstract URI findArtwork(String albumIdentifier) throws IOException;

  /**
   * Locate an album identifier from a {@link MusicFile}.
   * 
   * @param musicFile
   *          The {@link MusicFile} being searched.
   * @return The album identifier or null if no identifier could be found and
   *         thus no artwork should be searched for.
   */
  protected abstract String locateAlbumIdentifier(MusicFile musicFile);
}
