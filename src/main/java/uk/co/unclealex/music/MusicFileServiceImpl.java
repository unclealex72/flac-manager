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

package uk.co.unclealex.music;

import java.io.IOException;

/**
 * The default implementation of {@link MusicFileService}.
 * @author alex
 *
 */
public class MusicFileServiceImpl implements MusicFileService {

  /**
   * {@inheritDoc}
   */
  @Override
  public void transfer(MusicFile source, MusicFile target) throws IOException {
    target.setAlbum(source.getAlbum());
    target.setAlbumArtist(source.getAlbumArtist());
    target.setAlbumArtistId(source.getAlbumArtistId());
    target.setAlbumArtistSort(source.getAlbumArtistSort());
    target.setAlbumId(source.getAlbumId());
    target.setArtist(source.getArtist());
    target.setArtistId(source.getArtistId());
    target.setArtistSort(source.getAlbumArtistSort());
    target.setAsin(source.getAsin());
    target.setCoverArt(source.getCoverArt());
    target.setDiscNumber(source.getDiscNumber());
    target.setTitle(source.getTitle());
    target.setTotalDiscs(source.getTotalDiscs());
    target.setTotalTracks(source.getTotalTracks());
    target.setTrackId(source.getTrackId());
    target.setTrackNumber(source.getTrackNumber());
    target.commit();
  }

}
