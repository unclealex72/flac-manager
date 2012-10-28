package uk.co.unclealex.music.common;

import java.io.IOException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * A type safe and validatable interface that expose all mandatory and optional music tags.
 * @author alex
 *
 */
public interface MusicFile {

  /**
   * Commit any changes to this music file to disk.
   * @throws IOException
   */
  public void commit() throws IOException;

  /**
   * Gets the string used to sort the artist for the album of this track.
   *
   * @return the string used to sort the artist for the album of this track
   */
  @NotEmpty
  public String getAlbumArtistSort();

  /**
   * Sets the string used to sort the artist for the album of this track.
   *
   * @param albumArtistSort the new string used to sort the artist for the album of this track
   */
  public void setAlbumArtistSort(String albumArtistSort);

  /**
   * Gets the artist for the entire album.
   *
   * @return the artist for the entire album
   */
  @NotEmpty
  public String getAlbumArtist();

  /**
   * Sets the artist for the entire album.
   *
   * @param albumArtist the new artist for the entire album
   */
  public void setAlbumArtist(String albumArtist);

  /**
   * Gets the name of the album for this track.
   *
   * @return the name of the album for this track
   */
  @NotEmpty
  public String getAlbum();

  /**
   * Sets the name of the album for this track.
   *
   * @param album the new name of the album for this track
   */
  public void setAlbum(String album);

  /**
   * Gets the name of the artist who recorded this track.
   *
   * @return the name of the artist who recorded this track
   */
  @NotEmpty
  public String getArtist();

  /**
   * Sets the name of the artist who recorded this track.
   *
   * @param artist the new name of the artist who recorded this track
   */
  public void setArtist(String artist);

  /**
   * Gets the string used to sort the artist who recorded this track.
   *
   * @return the string used to sort the artist who recorded this track
   */
  @NotEmpty
  public String getArtistSort();

  /**
   * Sets the string used to sort the artist who recorded this track.
   *
   * @param artistSort the new string used to sort the artist who recorded this track
   */
  public void setArtistSort(String artistSort);

  /**
   * Gets the title of this track.
   *
   * @return the title of this track
   */
  @NotEmpty
  public String getTitle();

  /**
   * Sets the title of this track.
   *
   * @param title the new title of this track
   */
  public void setTitle(String title);

  /**
   * Gets the total number of discs included in the release for this track.
   *
   * @return the total number of discs included in the release for this track
   */
  @NotNull
  public Integer getTotalDiscs();

  /**
   * Sets the total number of discs included in the release for this track.
   *
   * @param totalDiscs the new total number of discs included in the release for this track
   */
  public void setTotalDiscs(Integer totalDiscs);

  /**
   * Gets the total number of tracks included in the release for this track.
   *
   * @return the total number of tracks included in the release for this track
   */
  @NotNull
  public Integer getTotalTracks();

  /**
   * Sets the total number of tracks included in the release for this track.
   *
   * @param totalTracks the new total number of tracks included in the release for this track
   */
  public void setTotalTracks(Integer totalTracks);

  /**
   * Gets the disc number of the disc that contains this track.
   *
   * @return the disc number of the disc that contains this track
   */
  @NotNull
  public Integer getDiscNumber();

  /**
   * Sets the disc number of the disc that contains this track.
   *
   * @param discNumber the new disc number of the disc that contains this track
   */
  public void setDiscNumber(Integer discNumber);

  /**
   * Gets the MusicBrainz ID of the artist for the album for this track.
   *
   * @return the MusicBrainz ID of the artist for the album for this track
   */
  @NotEmpty
  public String getAlbumArtistId();

  /**
   * Sets the MusicBrainz ID of the artist for the album for this track.
   *
   * @param albumArtistId the new MusicBrainz ID of the artist for the album for this track
   */
  public void setAlbumArtistId(String albumArtistId);

  /**
   * Gets the MusicBrainz ID of the album for this track.
   *
   * @return the MusicBrainz ID of the album for this track
   */
  @NotEmpty
  public String getAlbumId();

  /**
   * Sets the MusicBrainz ID of the album for this track.
   *
   * @param albumId the new MusicBrainz ID of the album for this track
   */
  public void setAlbumId(String albumId);

  /**
   * Gets the MusicBrainz ID of the artist who recorded this track.
   *
   * @return the MusicBrainz ID of the artist who recorded this track
   */
  @NotEmpty
  public String getArtistId();

  /**
   * Sets the MusicBrainz ID of the artist who recorded this track.
   *
   * @param artistId the new MusicBrainz ID of the artist who recorded this track
   */
  public void setArtistId(String artistId);

  /**
   * Gets the MusicBrainz ID of this track.
   *
   * @return the MusicBrainz ID of this track
   */
  @NotEmpty
  public String getTrackId();

  /**
   * Sets the MusicBrainz ID of this track.
   *
   * @param trackId the new MusicBrainz ID of this track
   */
  public void setTrackId(String trackId);

  /**
   * Gets the Amazon identifier for the album of this track.
   *
   * @return the Amazon identifier for the album of this track
   */
  public String getAsin();

  /**
   * Sets the Amazon identifier for the album of this track.
   *
   * @param asin the new Amazon identifier for the album of this track
   */
  public void setAsin(String asin);

  /**
   * Gets the number of this track on its album.
   *
   * @return the number of this track on its album
   */
  @NotNull
  public Integer getTrackNumber();

  /**
   * Sets the number of this track on its album.
   *
   * @param trackNumber the new number of this track on its album
   */
  public void setTrackNumber(Integer trackNumber);

  /**
   * Gets the cover art for this track.
   *
   * @return the cover art for this track
   */
  @Valid
  public CoverArt getCoverArt();

  /**
   * Sets the cover art for this track.
   *
   * @param coverArt the new cover art for this track
   */
  public void setCoverArt(CoverArt coverArt);

}