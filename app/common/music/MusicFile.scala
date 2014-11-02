package common.music

import java.nio.file.{Paths, Path}
import java.text.Normalizer

import common.files.Extension

import scala.util.Try

/**
 * A type safe and validatable interface that expose all mandatory music tags.
 * @author alex
 *
 */
trait MusicFile {

  /**
   * Commit any changes to this music file to disk.
   * @throws IOException
   */
  def commit(): Try[Unit]

  /**
   * Gets the string used to sort the artist for the album of this track.
   *
   * @return the string used to sort the artist for the album of this track
   */
  def albumArtistSort: String

  /**
   * Gets the artist for the entire album.
   *
   * @return the artist for the entire album
   */
  def albumArtist: String

  /**
   * Gets the name of the album for this track.
   *
   * @return the name of the album for this track
   */
  def album: String

  /**
   * Gets the name of the artist who recorded this track.
   *
   * @return the name of the artist who recorded this track
   */
  def artist: String

  /**
   * Gets the string used to sort the artist who recorded this track.
   *
   * @return the string used to sort the artist who recorded this track
   */
  def artistSort: String

  /**
   * Gets the title of this track.
   *
   * @return the title of this track
   */
  def title: String

  /**
   * Gets the total number of discs included in the release for this track.
   *
   * @return the total number of discs included in the release for this track
   */
  def totalDiscs: Int

  /**
   * Gets the total number of tracks included in the release for this track.
   *
   * @return the total number of tracks included in the release for this track
   */
  def totalTracks: Int

  /**
   * Gets the disc number of the disc that contains this track.
   *
   * @return the disc number of the disc that contains this track
   */
  def discNumber: Int

  /**
   * Gets the MusicBrainz ID of the artist for the album for this track.
   *
   * @return the MusicBrainz ID of the artist for the album for this track
   */
  def albumArtistId: String

  /**
   * Gets the MusicBrainz ID of the album for this track.
   *
   * @return the MusicBrainz ID of the album for this track
   */
  def albumId: String

  /**
   * Gets the MusicBrainz ID of the artist who recorded this track.
   *
   * @return the MusicBrainz ID of the artist who recorded this track
   */
  def artistId: String

  /**
   * Gets the MusicBrainz ID of this track.
   *
   * @return the MusicBrainz ID of this track
   */
  def trackId: String;

  /**
   * Gets the Amazon identifier for the album of this track.
   *
   * @return the Amazon identifier for the album of this track
   */
  def asin: Option[String]

  /**
   * Gets the number of this track on its album.
   *
   * @return the number of this track on its album
   */
  def trackNumber: Int

  /**
   * Gets the cover art for this track.
   *
   * @return the cover art for this track
   */
  def coverArt: CoverArt

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
      case _ => Some(f"$discNumber%02d")
    }
    val normalisedAlbum = normalise(Seq(Some(album), discNumberSuffix).flatten.mkString(" "))
    val normalisedTitle = normalise(f"$trackNumber%02d $title%s")
    Paths.get(firstLetter, normalisedAlbumArtistSort, normalisedAlbum, normalisedTitle + "." + extension.extension)
  }
}

/**
 * Validation
 */
object MusicFile {

  import com.wix.accord.dsl._

  implicit val musicFileValidator = validator[MusicFile] { m =>
    m.albumArtistSort is notEmpty
    m.albumArtist is notEmpty
    m.album is notEmpty
    m.artist is notEmpty
    m.artistSort is notEmpty
    m.title is notEmpty
    m.totalDiscs is notEqualTo(0)
    m.totalTracks is notEqualTo(0)
    m.discNumber is notEqualTo(0)
    m.albumArtistId is notEmpty
    m.artistId is notEmpty
    m.albumId is notEmpty
    m.trackId is notEmpty
    m.trackNumber is notEqualTo(0)
    m.coverArt is notNull
  }
}