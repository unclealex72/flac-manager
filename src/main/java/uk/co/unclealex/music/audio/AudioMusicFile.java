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

package uk.co.unclealex.music.audio;

import static org.jaudiotagger.tag.FieldKey.ALBUM;
import static org.jaudiotagger.tag.FieldKey.ALBUM_ARTIST;
import static org.jaudiotagger.tag.FieldKey.ALBUM_ARTIST_SORT;
import static org.jaudiotagger.tag.FieldKey.AMAZON_ID;
import static org.jaudiotagger.tag.FieldKey.ARTIST;
import static org.jaudiotagger.tag.FieldKey.ARTIST_SORT;
import static org.jaudiotagger.tag.FieldKey.DISC_NO;
import static org.jaudiotagger.tag.FieldKey.DISC_TOTAL;
import static org.jaudiotagger.tag.FieldKey.MUSICBRAINZ_ARTISTID;
import static org.jaudiotagger.tag.FieldKey.MUSICBRAINZ_RELEASEARTISTID;
import static org.jaudiotagger.tag.FieldKey.MUSICBRAINZ_RELEASEID;
import static org.jaudiotagger.tag.FieldKey.MUSICBRAINZ_TRACK_ID;
import static org.jaudiotagger.tag.FieldKey.TITLE;
import static org.jaudiotagger.tag.FieldKey.TRACK;
import static org.jaudiotagger.tag.FieldKey.TRACK_TOTAL;

import java.io.IOException;
import java.nio.file.Path;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.FixedID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;

import uk.co.unclealex.music.CoverArt;
import uk.co.unclealex.music.MusicFile;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

// TODO: Auto-generated Javadoc
/**
 * An implementation of {@link MusicFile} that gets and sets information from
 * and to an {@link AudioFile}.
 * 
 * @author alex
 * 
 */
public class AudioMusicFile implements MusicFile {

  /**
   * The picture type for front cover art.
   */
  private static final int COVER_ART = 3;

  /**
   * The {@link AudioFile} that provides the tags for this music file.
   */
  private final AudioFile audioFile;

  /**
   * The {@link Path} that contains the audio file information.
   */
  private final Path audioPath;
  
  /**
   * The {@link Tag} information for the {@link AudioFile}.
   */
  private final Tag tag;
  
  /**
   * Instantiates a new audio music file.
   *
   * @param audioFile the audio file
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public AudioMusicFile(Path audioFile) throws IOException {
    super();
    try {
      this.audioPath = audioFile;
      this.audioFile = AudioFileIO.read(audioFile.toFile());
      Tag tag = this.audioFile.getTag();
      if (tag == null) {
        tag = this.audioFile.createDefaultTag();
        this.audioFile.setTag(tag);
      }
      if (tag instanceof ID3v23Tag) {
        tag = new FixedID3v23Tag((ID3v23Tag) tag);
        this.audioFile.setTag(tag);
      }
      this.tag = tag;
    }
    catch (IOException | CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
      throw new IOException("Cannot read audio file " + audioFile, e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void commit() throws IOException {
    try {
      AudioFileIO.write(getAudioFile());
    }
    catch (CannotWriteException e) {
      throw new IOException("Cannot write to audio file " + getAudioFile().getFile(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAlbumArtistSort() {
    return getTag(ALBUM_ARTIST_SORT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAlbumArtistSort(String albumArtistSort) {
    setTag(ALBUM_ARTIST_SORT, albumArtistSort);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAlbumArtist() {
    return getTag(ALBUM_ARTIST);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAlbumArtist(String albumArtist) {
    setTag(ALBUM_ARTIST, albumArtist);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAlbum() {
    return getTag(ALBUM);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAlbum(String album) {
    setTag(ALBUM, album);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getArtist() {
    return getTag(ARTIST);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setArtist(String artist) {
    setTag(ARTIST, artist);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getArtistSort() {
    return getTag(ARTIST_SORT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setArtistSort(String artistSort) {
    setTag(ARTIST_SORT, artistSort);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTitle() {
    return getTag(TITLE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTitle(String title) {
    setTag(TITLE, title);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getTotalDiscs() {
    return getIntegerTag(DISC_TOTAL);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTotalDiscs(Integer totalDiscs) {
    setTag(DISC_TOTAL, totalDiscs);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getTotalTracks() {
    return getIntegerTag(TRACK_TOTAL);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTotalTracks(Integer totalTracks) {
    setTag(TRACK_TOTAL, totalTracks);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getDiscNumber() {
    return getIntegerTag(DISC_NO);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDiscNumber(Integer discNumber) {
    setTag(DISC_NO, discNumber);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAlbumArtistId() {
    return getTag(MUSICBRAINZ_RELEASEARTISTID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAlbumArtistId(String albumArtistId) {
    setTag(MUSICBRAINZ_RELEASEARTISTID, albumArtistId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAlbumId() {
    return getTag(MUSICBRAINZ_RELEASEID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAlbumId(String albumId) {
    setTag(MUSICBRAINZ_RELEASEID, albumId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getArtistId() {
    return getTag(MUSICBRAINZ_ARTISTID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setArtistId(String artistId) {
    setTag(MUSICBRAINZ_ARTISTID, artistId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTrackId() {
    return getTag(MUSICBRAINZ_TRACK_ID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTrackId(String trackId) {
    setTag(MUSICBRAINZ_TRACK_ID, trackId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAsin() {
    return getTag(AMAZON_ID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAsin(String asin) {
    setTag(AMAZON_ID, asin);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getTrackNumber() {
    return getIntegerTag(TRACK);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTrackNumber(Integer trackNumber) {
    setTag(TRACK, trackNumber);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CoverArt getCoverArt() {
    Predicate<Artwork> artworkPredicate = new Predicate<Artwork>() {
      @Override
      public boolean apply(Artwork artwork) {
        return COVER_ART == artwork.getPictureType();
      }
    };
    Artwork artwork = Iterables.find(getTag().getArtworkList(), artworkPredicate, null);
    return artwork == null ? null : new CoverArt(artwork.getBinaryData(), artwork.getMimeType());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCoverArt(CoverArt coverArt) {
    if (coverArt == null) {
      getTag().deleteArtworkField();
    }
    else {
      Artwork artwork = new Artwork();
      artwork.setPictureType(COVER_ART);
      artwork.setBinaryData(coverArt.getImageData());
      artwork.setMimeType(coverArt.getMimeType());
      try {
        getTag().setField(artwork);
      }
      catch (FieldDataInvalidException e) {
        throw new IllegalArgumentException("Cover art was invalid.", e);
      }
    }
  }

  /**
   * Get a value stored in the tag.
   * 
   * @param fieldKey
   *          The key of the value to look for.
   * @return The value stored in the tag with the field key or null if it does
   *         not exist.
   */
  public String getTag(FieldKey fieldKey) {
    String value = getTag().getFirst(fieldKey);
    return Strings.isNullOrEmpty(value) ? null : value;
  }

  /**
   * Get a value stored in the tag in integer form.
   * 
   * @param fieldKey
   *          The key of the value to look for.
   * @return The value stored in the tag with the field key or null if it does
   *         not exist.
   */
  public Integer getIntegerTag(FieldKey fieldKey) {
    String value = getTag(fieldKey);
    return Strings.isNullOrEmpty(value) ? null : Integer.valueOf(value);
  }

  /**
   * Sets the tag.
   *
   * @param fieldKey the field key
   * @param value the value
   */
  public void setTag(FieldKey fieldKey, Object value) {
    Tag tag = getTag();
    if (value == null) {
      tag.deleteField(fieldKey);
    }
    else {
      try {
        tag.setField(fieldKey, value.toString());
      }
      catch (KeyNotFoundException | FieldDataInvalidException e) {
        throw new IllegalArgumentException("Data " + value + " is invalid for tag " + fieldKey, e);
      }
    }
  }

  /**
   * Gets the {@link AudioFile} that provides the tags for this music file.
   *
   * @return the {@link AudioFile} that provides the tags for this music file
   */
  public AudioFile getAudioFile() {
    return audioFile;
  }

  /**
   * Gets the {@link Path} that contains the audio file information.
   *
   * @return the {@link Path} that contains the audio file information
   */
  public Path getAudioPath() {
    return audioPath;
  }
  
  /**
   * Gets the {@link Tag} information for the {@link AudioFile}.
   *
   * @return the {@link Tag} information for the {@link AudioFile}
   */
  public Tag getTag() {
    return tag;
  }

}
