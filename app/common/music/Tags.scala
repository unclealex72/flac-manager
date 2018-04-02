/*
 * Copyright 2018 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.music

import java.nio.file.{FileSystem, Path}
import java.text.Normalizer

import cats.data._
import cats.implicits._
import common.files.Extension
import common.message.Message
import common.message.Messages.INVALID_TAGS
import play.api.libs.json._

import scala.collection.Map
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
                 trackId: Option[String],
                 asin: Option[String],
                 trackNumber: Int,
                 coverArt: CoverArt) {

  /**
   * Create the following relative path:
   * `firstLetterOfSortedAlbumArtist/sortedAlbumArtist/album (diskNumber)/trackNumber title.ext` in ASCII
    * @param fs The underlying file system.
    * @param extension The extension for the path.
   */
  def asPath(fs: FileSystem, extension: Extension): Path = {
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
    fs.getPath(firstLetter, normalisedAlbumArtistSort, normalisedAlbum, normalisedTitle + "." + extension.extension)
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

  import JsonSupport._

  def fromJson(json: JsValue): ValidatedNel[Message, Tags] = {
    def coverArtO(fields: Map[String, JsValue], field: String): ValidatedNel[Message, Option[CoverArt]] =
      extract(fields, field) { case coverArtJson => CoverArt.fromJson(coverArtJson) }
    val coverArt = mandatory(coverArtO) _
    json match {
      case JsObject(fields) =>
        (str(fields, "albumArtistSort") |@|
          str(fields, "albumArtist") |@|
          str(fields, "album") |@|
          str(fields, "artist") |@|
          str(fields, "artistSort") |@|
          str(fields, "title") |@|
          int(fields, "totalDiscs") |@|
          int(fields, "totalTracks") |@|
          int(fields, "discNumber") |@|
          str(fields, "albumArtistId") |@|
          str(fields, "albumId") |@|
          str(fields, "artistId") |@|
          strO(fields, "trackId") |@|
          strO(fields, "asin") |@|
          int(fields, "trackNumber") |@|
          coverArt(fields, "coverArt")).map { (albumArtistSort, albumArtist, album, artist, artistSort, title,
                                               totalDiscs, totalTracks, discNumber, albumArtistId, albumId,
                                               artistId, trackId, asin, trackNumber, coverArt) =>
            Tags(
              albumArtistSort = albumArtistSort,
              albumArtist = albumArtist,
              album = album,
              artist = artist,
              artistSort = artistSort,
              title = title,
              totalDiscs = totalDiscs,
              totalTracks = totalTracks,
              discNumber = discNumber,
              albumArtistId = albumArtistId,
              albumId = albumId,
              artistId = artistId,
              trackId = trackId,
              asin = asin,
              trackNumber = trackNumber,
              coverArt = coverArt
            )
        }

      case _ => Validated.invalidNel(INVALID_TAGS("Cannot parse tags."))
    }

  }

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
   def validate(tags: Tags): ValidatedNel[String, Tags] = {
     def v[V](p: V => Boolean)(v: V, message: String): ValidatedNel[String, V] = Option(v).filter(p).toValidNel(message)
     def notNull[V] = v[V](_ => true) _
     def notEmpty = v[String](s => !s.isEmpty) _
     def positive = v[Int](n => n > 0) _

     (notEmpty(tags.albumArtistSort, "Album artist sort is required") |@|
      notEmpty(tags.albumArtist, "Album artist is required") |@|
      notEmpty(tags.album, "Album is required") |@|
      notEmpty(tags.artistSort, "Artist sort is required") |@|
      notEmpty(tags.title, "Title is required") |@|
      positive(tags.totalDiscs, "Total discs must be greater than zero") |@|
      positive(tags.totalTracks, "Total tracks must be greater than zero") |@|
      positive(tags.discNumber, "Disc number must be greater than zero") |@|
      notEmpty(tags.albumArtistId, "Album artist ID is required") |@|
      notEmpty(tags.artistId, "Artist ID is required") |@|
      notEmpty(tags.albumId, "Album ID is required") |@|
      positive(tags.discNumber, "Track number must be greater than zero") |@|
      notNull(tags.coverArt, "Cover art is required")).map((_, _, _, _, _, _, _, _, _, _, _, _, _) => tags)
  }
}