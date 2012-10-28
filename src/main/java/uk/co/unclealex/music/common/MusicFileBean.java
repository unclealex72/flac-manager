/**
 * Copyright 2011 Alex Jones
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

package uk.co.unclealex.music.common;

import java.io.IOException;


/**
 * A music file is an interface for classes that can read and write metadata to
 * a music file.
 * 
 * @author alex
 * 
 */
public class MusicFileBean implements MusicFile {

  /**
   * The string used to sort the artist for the album of this track.
   */
  private String albumArtistSort;
  
  /**
   * The artist for the entire album.
   */
  private String albumArtist;
  
  /**
   * The name of the album for this track.
   */
  private String album;

  /**
   * The name of the artist who recorded this track.
   */
  private String artist;
  
  /**
   * The string used to sort the artist who recorded this track.
   */
  private String artistSort;

  /**
   * The title of this track.
   */
  private String title;

  /**
   * The total number of discs included in the release for this track.
   */
  private Integer totalDiscs;
  
  /**
   * The total number of tracks included in the release for this track.
   */
  private Integer totalTracks;
  
  /**
   * The disc number of the disc that contains this track.
   */
  private Integer discNumber;

  /**
   * The MusicBrainz ID of the artist for the album for this track.
   */
  private String albumArtistId;
  
  /**
   * The MusicBrainz ID of the album for this track.
   */
  private String albumId;
  
  /**
   * The MusicBrainz ID of the artist who recorded this track.
   */
  private String artistId;
  
  /**
   * The MusicBrainz ID of this track.
   */
  private String trackId;

  /**
   * The Amazon identifier for the album of this track.
   */
  private String asin;
  
  /**
   * The number of this track on its album.
   */
  private Integer trackNumber;
  
  /**
   * The cover art for this track.
   */
  private CoverArt coverArt;

  /**
   * {@inheritDoc}
   */
  @Override
  public void commit() throws IOException {
    // Do nothing.
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getAlbumArtistSort() {
    return albumArtistSort;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAlbumArtistSort(String albumArtistSort) {
    this.albumArtistSort = albumArtistSort;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAlbumArtist() {
    return albumArtist;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAlbumArtist(String albumArtist) {
    this.albumArtist = albumArtist;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAlbum() {
    return album;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAlbum(String album) {
    this.album = album;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getArtist() {
    return artist;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setArtist(String artist) {
    this.artist = artist;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getArtistSort() {
    return artistSort;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setArtistSort(String artistSort) {
    this.artistSort = artistSort;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTitle() {
    return title;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getTotalDiscs() {
    return totalDiscs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTotalDiscs(Integer totalDiscs) {
    this.totalDiscs = totalDiscs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getTotalTracks() {
    return totalTracks;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTotalTracks(Integer totalTracks) {
    this.totalTracks = totalTracks;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getDiscNumber() {
    return discNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDiscNumber(Integer discNumber) {
    this.discNumber = discNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAlbumArtistId() {
    return albumArtistId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAlbumArtistId(String albumArtistId) {
    this.albumArtistId = albumArtistId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAlbumId() {
    return albumId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAlbumId(String albumId) {
    this.albumId = albumId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getArtistId() {
    return artistId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setArtistId(String artistId) {
    this.artistId = artistId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTrackId() {
    return trackId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTrackId(String trackId) {
    this.trackId = trackId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAsin() {
    return asin;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAsin(String asin) {
    this.asin = asin;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getTrackNumber() {
    return trackNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTrackNumber(Integer trackNumber) {
    this.trackNumber = trackNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CoverArt getCoverArt() {
    return coverArt;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCoverArt(CoverArt coverArt) {
    this.coverArt = coverArt;
  }
  
}
