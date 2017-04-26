/*
 * Copyright 2014 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.music

import java.nio.file.{Path, Paths}
import java.text.Normalizer

import com.wix.accord.transform.ValidationTransform
import common.files.Extension
import play.api.libs.json.{JsObject, Json}

/**
  * A case class that encapsulates information about a music file.
  * @param albumArtistSort The string used to sort the artist for the album of this track.
  * @param albumArtist The artist for the entire album
  * @param album The name of the album for this track
  * @param artist The name of the artist who recorded this track
  * @param artistSort The string used to sort the artist who recorded this track
  * @param title The title of this track
  * @param totalDiscs The total number of discs included in the release for this track
  * @param totalTracks The total number of tracks included in the release for this track
  * @param discNumber The disc number of the disc that contains this track
  * @param albumArtistId The [[http://www.musicbrainz.org MusicBrainz]] ID of the artist for the album for this track
  * @param albumId The [[http://www.musicbrainz.org MusicBrainz]] ID of the album for this track
  * @param artistId The [[http://www.musicbrainz.org MusicBrainz]] ID of the artist who recorded this track
  * @param trackId The [[http://www.musicbrainz.org MusicBrainz]] ID of this track
  * @param asin The Amazon identifier for the album of this track
  * @param trackNumber The number of this track on its album
  * @param coverArt The cover art for this track
  */
case class Tags(
                 albumArtistSort: String,
                 albumArtist: String,
                 album: String,
                 artist: String,
                 artistSort: String,
                 title: String,
                 totalDiscs: Int,
                 totalTracks: Int,
                 discNumber: Int,
                 albumArtistId: String,
                 albumId: String,
                 artistId: String,
                 trackId: String,
                 asin: Option[String],
                 trackNumber: Int,
                 coverArt: CoverArt) {

  /**
   * Create the following relative path:
   * `firstLetterOfSortedAlbumArtist/sortedAlbumArtist/album (diskNumber)/trackNumber title.ext` in ASCII
    * @param extension The extension for the path.
   */
  def asPath(extension: Extension): Path = {
    def normalise(str: CharSequence): String = {
      val nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD)
      val nonAccentedString = """\p{InCombiningDiacriticalMarks}+""".r.replaceAllIn(nfdNormalizedString, "")
      val validCharacters = """(?:[a-zA-Z0-9]|\s)+""".r.findAllIn(nonAccentedString).mkString
      """\s+""".r.replaceAllIn(validCharacters, " ").trim
    }
    val normalisedAlbumArtistSort = normalise(albumArtistSort)
    val firstLetter = normalisedAlbumArtistSort.substring(0, 1)
    val discNumberSuffix: Option[String] = totalDiscs match {
      case 1 => None
      case _ => Some(f"$discNumber%02d")
    }
    val normalisedAlbum = normalise(Seq(Some(album), discNumberSuffix).flatten.mkString(" "))
    val normalisedTitle = normalise(f"$trackNumber%02d $title%s")
    Paths.get(firstLetter, normalisedAlbumArtistSort, normalisedAlbum, normalisedTitle + "." + extension.extension)
  }

  /**
    * Convert these tags to a JSON object.
    * @param includeCoverArt True if cover art should be included (as a mime-type and base 64 encoded data)
    *                        or false otherwise.
    * @return A JSON object containing all tagging information.
    */
  def toJson(includeCoverArt: Boolean): JsObject = {
    val obj = Json.obj(
      "albumArtistSort" -> albumArtistSort, //String
      "albumArtist" -> albumArtist, //String
      "album" -> album, //String
      "artist" -> artist, //String
      "artistSort" -> artistSort, //String
      "title" -> title, //String
      "totalDiscs" -> totalDiscs, //int
      "totalTracks" -> totalTracks, //int
      "discNumber" -> discNumber, //int
      "albumArtistId" -> albumArtistId, //String
      "albumId" -> albumId, //String
      "artistId" -> artistId, //String
      "trackId" -> trackId, //String
      "asin" -> asin, //Option[String]
      "trackNumber" -> trackNumber) //Int
    if (includeCoverArt) obj + ("coverArt" -> coverArt.toJson) else obj
  }
}

/**
 * Used to generate a validator for tags.
 */
object Tags {

  import com.wix.accord.dsl._

  /**
    * A validator for tags. The following must be present:
    *   1. - album artist sort,
    *   1. - album artist,
    *   1. - album,
    *   1. - artist,
    *   1. - artist sort,
    *   1. - title,
    *   1. - total disks,
    *   1. - disc number,
    *   1. - album artist ID,
    *   1. - artist ID,
    *   1. - album ID,
    *   1. - track ID,
    *   1. - track number,
    *   1. - cover art.
    */
  implicit val musicFileValidator: ValidationTransform.TransformedValidator[Tags] = validator[Tags] { tags =>
    tags.albumArtistSort is notEmpty
    tags.albumArtist is notEmpty
    tags.album is notEmpty
    tags.artist is notEmpty
    tags.artistSort is notEmpty
    tags.title is notEmpty
    tags.totalDiscs is >(0)
    tags.totalTracks is >(0)
    tags.discNumber is >(0)
    tags.albumArtistId is notEmpty
    tags.artistId is notEmpty
    tags.albumId is notEmpty
    tags.trackId is notEmpty
    tags.trackNumber is >(0)
    tags.coverArt is notNull
  }
}