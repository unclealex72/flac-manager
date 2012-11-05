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

import uk.co.unclealex.music.CoverArt;
import uk.co.unclealex.music.MusicFile;

/**
 * An interface for classes that can add {@link CoverArt} to a {@link MusicFile}.
 * @author alex
 *
 */
public interface ArtworkService {

  /**
   * Add artwork to a {@link MusicFile}.
   * @param musicFile The tagging information to which cover art is to be added.
   * @param coverArtUri The URI of the cover art to use.
   * @throws IOException 
   */
  public void addArwork(MusicFile musicFile, URI coverArtUri) throws IOException;

}
