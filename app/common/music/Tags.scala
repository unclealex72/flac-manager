/*
 * Copyright 2014 Alex Jones
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
 */

package common.music

import java.nio.file.{Path, Paths}
import java.text.Normalizer

import common.files.Extension
import play.api.libs.json.Json

/**
 * Created by alex on 02/11/14.
 */
case class Tags(

                 /**
                  * Gets the string used to sort the artist for the album of this track.
                  *
                  * @return the string used to sort the artist for the album of this track
                  */
                 val albumArtistSort: String,

                 /**
                  * Gets the artist for the entire album.
                  *
                  * @return the artist for the entire album
                  */
                 val albumArtist: String,

                 /**
                  * Gets the name of the album for this track.
                  *
                  * @return the name of the album for this track
                  */
                 val album: String,

                 /**
                  * Gets the name of the artist who recorded this track.
                  *
                  * @return the name of the artist who recorded this track
                  */
                 val artist: String,

                 /**
                  * Gets the string used to sort the artist who recorded this track.
                  *
                  * @return the string used to sort the artist who recorded this track
                  */
                 val artistSort: String,

                 /**
                  * Gets the title of this track.
                  *
                  * @return the title of this track
                  */
                 val title: String,

                 /**
                  * Gets the total number of discs included in the release for this track.
                  *
                  * @return the total number of discs included in the release for this track
                  */
                 val totalDiscs: Int,

                 /**
                  * Gets the total number of tracks included in the release for this track.
                  *
                  * @return the total number of tracks included in the release for this track
                  */
                 val totalTracks: Int,

                 /**
                  * Gets the disc number of the disc that contains this track.
                  *
                  * @return the disc number of the disc that contains this track
                  */
                 val discNumber: Int,

                 /**
                  * Gets the MusicBrainz ID of the artist for the album for this track.
                  *
                  * @return the MusicBrainz ID of the artist for the album for this track
                  */
                 val albumArtistId: String,

                 /**
                  * Gets the MusicBrainz ID of the album for this track.
                  *
                  * @return the MusicBrainz ID of the album for this track
                  */
                 val albumId: String,

                 /**
                  * Gets the MusicBrainz ID of the artist who recorded this track.
                  *
                  * @return the MusicBrainz ID of the artist who recorded this track
                  */
                 val artistId: String,

                 /**
                  * Gets the MusicBrainz ID of this track.
                  *
                  * @return the MusicBrainz ID of this track
                  */
                 val trackId: String,

                 /**
                  * Gets the Amazon identifier for the album of this track.
                  *
                  * @return the Amazon identifier for the album of this track
                  */
                 val asin: Option[String],

                 /**
                  * Gets the number of this track on its album.
                  *
                  * @return the number of this track on its album
                  */
                 val trackNumber: Int,

                 /**
                  * Gets the cover art for this track.
                  *
                  * @return the cover art for this track
                  */
                 val coverArt: CoverArt
                 ) {

  /**
   * Create the following relative path:
   * firstLetterOfSortedAlbumArtist/sortedAlbumArtist/album (diskNumber)/trackNumber title.ext
   */
  def asPath(extension: Extension): Path = {
    def normalise(str: CharSequence): String = {
      val nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD)
      val nonAccentedString = """\p{InCombiningDiacriticalMarks}+""".r.replaceAllIn(nfdNormalizedString, "")
      val validCharacters = """(?:[a-zA-Z0-9]|\s)+""".r.findAllIn(nonAccentedString).mkString
      """\s+""".r.replaceAllIn(validCharacters, " ").trim
    }
    val normalisedAlbumArtistSort = normalise(albumArtistSort)
    val firstLetter = normalisedAlbumArtistSort.substring(0, 1);
    val discNumberSuffix: Option[String] = totalDiscs match {
      case 1 => None
      case _ => Some(f"${discNumber}%02d")
    }
    val normalisedAlbum = normalise(Seq(Some(album), discNumberSuffix).flatten.mkString(" "))
    val normalisedTitle = normalise(f"${trackNumber}%02d ${title}%s")
    Paths.get(firstLetter, normalisedAlbumArtistSort, normalisedAlbum, normalisedTitle + "." + extension.extension)
  }

  def toJson = Json.obj(
    "albumArtistSort" -> albumArtistSort,
    "albumArtist" -> albumArtist,
    "album" -> album,
    "artist" -> artist,
    "artistSort" -> artistSort,
    "title" -> title,
    "totalDiscs" -> totalDiscs,
    "totalTracks" -> totalTracks,
    "discNumber" -> discNumber,
    "albumArtistId" -> albumArtistId,
    "albumId" -> albumId,
    "artistId" -> artistId,
    "trackId" -> trackId,
    "asin" -> asin,
    "trackNumber" -> trackNumber,
    "coverArt" -> coverArt.toJson)
}

/**
 * Validation
 */
object Tags {

  import com.wix.accord.dsl._

  implicit val musicFileValidator = validator[Tags] { tags =>
    tags.albumArtistSort is notEmpty
    tags.albumArtist is notEmpty
    tags.album is notEmpty
    tags.artist is notEmpty
    tags.artistSort is notEmpty
    tags.title is notEmpty
    tags.totalDiscs is notEqualTo(0)
    tags.totalTracks is notEqualTo(0)
    tags.discNumber is notEqualTo(0)
    tags.albumArtistId is notEmpty
    tags.artistId is notEmpty
    tags.albumId is notEmpty
    tags.trackId is notEmpty
    tags.trackNumber is notEqualTo(0)
    tags.coverArt is notNull
  }
}