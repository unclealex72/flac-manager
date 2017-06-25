/*
 * Copyright 2017 Alex Jones
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

package common.files

import java.nio.file.{Files, Path, FileSystem => JFileSystem}

import cats.data.Validated.{Invalid, Valid}
import com.typesafe.scalalogging.StrictLogging
import common.configuration.{Directories, TestDirectories}
import common.files.Extension.{FLAC, MP3}
import common.message.{MessageService, NoOpMessageService}
import common.music.{Tags, TagsService}
import play.api.libs.json.Json
import testfilesystem._
/**
  * Created by alex on 17/06/17
  **/
trait TestRepositories[T] extends FS[T] with StrictLogging {

  implicit val messageService: MessageService = NoOpMessageService(this)

  final override def setup(fs: JFileSystem): T = {
    val directories: Directories = TestDirectories(fs)
    Files.createDirectories(directories.temporaryPath)
    val tagsService: TagsService = new TagsService {
      override def readTags(path: Path): Tags = {
        val json = Json.parse(Files.readAllBytes(path))
        Tags.fromJson(json) match {
          case Valid(_json) => _json
          case Invalid(messages) =>
            throw new IllegalStateException(s"Could not parse JSON for $path:\n${messages.toList.mkString("\n")}")
        }
      }

      override def write(path: Path, tags: Tags): Unit = {
        Files.write(path, Json.toBytes(tags.toJson(true)))
      }
    }
    val flacFileChecker: FlacFileChecker = (path: Path) => path.getFileName.toString.endsWith(".flac")
    generate(fs, new RepositoriesImpl(directories, tagsService, flacFileChecker))
  }

  def generate(fs: JFileSystem, repositories: Repositories): T
}

case class UserEntryBuilder(user: String, artistsEntryBuilder: ArtistsEntryBuilder)
case class ArtistsEntryBuilder(artistEntryBuilders: Seq[ArtistEntryBuilder])
case class ArtistEntryBuilder(artist: String, albumEntryBuilders: Seq[AlbumEntryBuilder])
case class AlbumEntryBuilder(album: String, discEntryBuilders: Seq[DiscEntryBuilder])
case class DiscEntryBuilder(albumId: String, trackEntryBuilders: Seq[TrackEntryBuilder])
case class TrackEntryBuilder(track: String)

object RepositoryEntry {
  object Builder extends Builder
  trait Builder extends FS.Builder {

    def toFsEntryBuilder(userEntryBuilder: UserEntryBuilder, extension: Extension, link: Boolean): FsEntryBuilder = {
      FsDirectoryBuilder(userEntryBuilder.user, toFsEntryBuilders(userEntryBuilder.artistsEntryBuilder, extension, link))
    }

    def toFsEntryBuilders(artistsEntry: ArtistsEntryBuilder, extension: Extension, link: Boolean): Seq[FsEntryBuilder] = {
      val artistEntriesByInitial = artistsEntry.artistEntryBuilders.groupBy(_.artist.substring(0, 1))
      artistEntriesByInitial.toSeq.map { case (initial, artistEntries) =>
        FsDirectoryBuilder(initial, convertArtistEntries(initial, artistEntries, extension, link))
      }
    }

    private def convertArtistEntries(initial: String, artistEntryBuilders: Seq[ArtistEntryBuilder], extension: Extension, link: Boolean): Seq[FsEntryBuilder] = {
      artistEntryBuilders.map { artistEntryBuilder =>
        val artist = artistEntryBuilder.artist
        FsDirectoryBuilder(artist, convertAlbumEntries(initial, artist, artistEntryBuilder.albumEntryBuilders, extension, link))
      }
    }

    private def convertAlbumEntries(initial: String, artist: String, albumEntryBuilders: Seq[AlbumEntryBuilder], extension: Extension, link: Boolean): Seq[FsEntryBuilder] = {
      albumEntryBuilders.flatMap { albumEntryBuilder =>
        val album = albumEntryBuilder.album
        convertDiscEntries(initial, artist, album, albumEntryBuilder.discEntryBuilders, extension, link)
      }
    }

    private def convertDiscEntries(initial: String, artist: String, album: String, discEntryBuilders: Seq[DiscEntryBuilder], extension: Extension, link: Boolean): Seq[FsEntryBuilder] = {
      val totalDiscs = discEntryBuilders.size
      discEntryBuilders.zipWithIndex.map { case (discEntryBuilder, idx) =>
        convertDiscEntry(initial, artist, album, discEntryBuilder.albumId, totalDiscs, idx + 1, discEntryBuilder, extension, link)
      }
    }

    private def convertDiscEntry(initial: String, artist: String, album: String, albumId: String, totalDiscs: Int, discNumber: Int, discEntryBuilder: DiscEntryBuilder, extension: Extension, link: Boolean): FsEntryBuilder = {
      val albumDirectory = if (totalDiscs == 1) album else f"$album $discNumber%02d"
      FsDirectoryBuilder(albumDirectory, convertTrackEntries(initial, artist, albumDirectory, album, albumId, totalDiscs, discNumber, discEntryBuilder.trackEntryBuilders, extension, link))
    }

    def convertTrackEntries(initial: String, artist: String, albumDirectory: String, albumTitle: String, albumId: String, totalDiscs: Int, discNumber: Int, trackEntryBuilders: Seq[TrackEntryBuilder], extension: Extension, link: Boolean): Seq[FsEntryBuilder] = {
      val totalTracks = trackEntryBuilders.size
      trackEntryBuilders.zipWithIndex.map { case (trackEntryBuilder, idx) =>
        val trackNumber = idx + 1
        val track = trackEntryBuilder.track
        val filename = f"$trackNumber%02d $track.$extension"
        if (link) {
          val target = s"../../../../../encoded/$initial/$artist/$albumDirectory/$filename"
          FsLinkBuilder(filename, target)
        }
        else {
          val trackTags = tags(
            artist = artist, album = albumTitle, albumId = albumId, totalDiscs = totalDiscs,
            discNumber = discNumber, totalTracks = totalTracks, trackNumber = trackNumber, track = track)
          FsFileBuilder(filename, Some(trackTags))
        }
      }
    }

  }

  object Dsl extends Dsl
  trait Dsl extends Builder with FS.Dsl {

    case class Users(artistsByUser: (String, Artists)*)
    case class Artists(albumsByArtist: (String, Seq[Album])*)
    case class Album(title: String, discs: Discs)
    object Album {
      def apply(title: String, tracks: Tracks): Album = Album(title, Discs(tracks))
    }
    object Albums {
      def apply(albums: Album*): Seq[Album] = albums
    }
    case class Discs(sameId: Boolean = false, tracks: Seq[Tracks]) {
      def withSameId: Discs = Discs(sameId = true, tracks)
    }
    object Discs {
      def apply(tracks: Tracks*): Discs = Discs(sameId = false, tracks)
    }
    case class Tracks(titles: String*)
    implicit def tracksToSingleDisc(tracks: Tracks): Seq[Discs] = Seq(Discs(tracks))
    sealed trait ArtistsOrEntries
    case class ArtistsOrEntries_Artists(artists: Artists) extends ArtistsOrEntries
    case class ArtistsOrEntries_Entries(entryBuilders: Seq[FsEntryBuilder]) extends ArtistsOrEntries

    implicit def artistsToArtistsOrEntries(artists: Artists): ArtistsOrEntries =
      ArtistsOrEntries_Artists(artists)
    implicit def entriesToArtistsOrEntries(entryBuilders: Seq[FsEntryBuilder]): ArtistsOrEntries =
      ArtistsOrEntries_Entries(entryBuilders)

    object Repos {

      private def artistsToArtistsEntry(artists: Artists): ArtistsEntryBuilder = {
        val artistEntries: Seq[ArtistEntryBuilder] = artists.albumsByArtist.map { case (artist, albums) =>
          val albumEntries: Seq[AlbumEntryBuilder] = albums.map { album =>
            val discs = album.discs
            val discTracks = discs.tracks
            val keepSameId = discs.sameId || discTracks.size == 1
            val albumTitle = album.title
            val discEntries: Seq[DiscEntryBuilder] = discTracks.zipWithIndex.map { case (tracks, idx) =>
              val discNumber = idx + 1
              val albumId = if (keepSameId) albumTitle else f"$albumTitle $discNumber%02d"
              DiscEntryBuilder(albumId, tracks.titles.map(TrackEntryBuilder))
            }
            AlbumEntryBuilder(albumTitle, discEntries)
          }
          ArtistEntryBuilder(artist, albumEntries)
        }
        ArtistsEntryBuilder(artistEntries)
      }

      private def entryBuilder(artists: Artists, extension: Extension, link: Boolean): Seq[FsEntryBuilder] = {
        toFsEntryBuilders(artistsToArtistsEntry(artists), extension, link)
      }

      private def entryBuilder(artistsOrEntries: ArtistsOrEntries, extension: Extension, link: Boolean): Seq[FsEntryBuilder] = {
        artistsOrEntries match {
          case ArtistsOrEntries_Entries(entries) => entries
          case ArtistsOrEntries_Artists(artists) => entryBuilder(artists, extension, link)
        }
      }

      private def entryBuilder(users: Users, extension: Extension, link: Boolean): Seq[FsEntryBuilder] = {
        users.artistsByUser.map { case (user, artists) =>
          val userEntry = UserEntryBuilder(user, artistsToArtistsEntry(artists))
          toFsEntryBuilder(userEntry, extension, link)
        }
      }

      def apply(flac: Artists = Artists(),
                encoded: Artists = Artists(),
                staging: ArtistsOrEntries = ArtistsOrEntries_Entries(Seq.empty),
                devices: Users = Users()): Seq[FsEntryBuilder] = {
        Seq(
          FsReadOnlyBuilder(FsDirectoryBuilder("flac", entryBuilder(flac, FLAC, link = false))),
          FsReadOnlyBuilder(FsDirectoryBuilder("encoded", entryBuilder(encoded, MP3, link = false))),
          FsReadWriteBuilder(FsDirectoryBuilder("staging", entryBuilder(staging, FLAC, link = false))),
          FsReadOnlyBuilder(FsDirectoryBuilder("devices", entryBuilder(devices, MP3, link = true))))
      }
    }

    implicit class RepositoryFileSystemExtensions(fs: JFS) {
      def staging(fsEntryBuilders: FsEntryBuilder*): Unit = {
        fs.add(Repos(staging = ArtistsOrEntries_Entries(fsEntryBuilders)) :_*)
      }
      def staging(artists: Artists): Unit = {
        fs.add(Repos(staging = artists) :_*)
      }
      def flac(artists: Artists): Unit = {
        fs.add(Repos(flac = artists) :_*)
      }
      def encoded(artists: Artists): Unit = {
        fs.add(Repos(encoded = artists) :_*)
      }
      def devices(devices: Users): Unit = {
        fs.add(Repos(devices = devices) :_*)
      }
    }
  }
}