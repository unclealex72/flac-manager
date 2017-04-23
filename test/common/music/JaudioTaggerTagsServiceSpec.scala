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

import java.io.{ByteArrayOutputStream, InputStream}
import java.nio.file.{Files, StandardCopyOption}

import com.google.common.io.ByteStreams
import io.github.marklister.base64.Base64._
import org.slf4j.bridge.SLF4JBridgeHandler
import org.specs2.mutable._
import tempfs.DefaultTempFileSystem

import scala.sys.process._

/**
 * @author alex
 *
 */
class JaudioTaggerTagsServiceSpec extends Specification {

  SLF4JBridgeHandler.removeHandlersForRootLogger()
  SLF4JBridgeHandler.install()
  val tagsService = new JaudioTaggerTagsService

  val coverIn: InputStream = getClass.getClassLoader.getResourceAsStream("cover.jpg")
  val coverOut = new ByteArrayOutputStream
  ByteStreams.copy(coverIn, coverOut)
  coverIn.close()
  val coverArt = CoverArt(coverOut.toByteArray, "image/jpeg")

  "Reading a tagged file" should {
    "correctly read all the tags" in new DefaultTempFileSystem {
      val flacIn = getClass.getClassLoader.getResourceAsStream("tagged.flac")
      val tempMusicFile = rootDirectory.resolve("tagged.flac")
      Files.copy(flacIn, tempMusicFile, StandardCopyOption.REPLACE_EXISTING)
      flacIn.close
      val tags: Tags = tagsService.readTags(tempMusicFile)
      tags.album must be equalTo "Metal: A Headbanger's Companion"
      tags.albumArtist must be equalTo "Various Artists"
      tags.albumArtistId must be equalTo "89ad4ac3-39f7-470e-963a-56509c546377"
      tags.albumArtistSort must be equalTo "Various Artists Sort"
      tags.albumId must be equalTo "6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e"
      tags.artist must be equalTo "Napalm Death"
      tags.artistId must be equalTo "ce7bba8b-026b-4aa6-bddb-f98ed6d595e4"
      tags.artistSort must be equalTo "Napalm Death Sort"
      tags.asin must be equalTo Some("B000Q66HUA")
      tags.title must be equalTo "Suffer The Children"
      tags.trackId must be equalTo "5b0ef8e9-9b55-4a3e-aca6-d816d6bbc00f"
      tags.coverArt.imageData.toBase64 must be equalTo coverArt.imageData.toBase64
      tags.coverArt.mimeType must be equalTo coverArt.mimeType
      tags.discNumber.intValue must be equalTo 1
      tags.totalDiscs.intValue must be equalTo 6
      tags.totalTracks.intValue must be equalTo 17
      tags.trackNumber.intValue must be equalTo 3
    }
  }

  val tagsToWrite = Tags(
    "Various Artists Sort",
    "Various Artists",
    "Metal: A Headbanger's Companion",
    "Napalm Death",
    "Napalm Death Sort",
    "Suffer The Children",
    6,
    17,
    1,
    "89ad4ac3-39f7-470e-963a-56509c546377",
    "6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e",
    "ce7bba8b-026b-4aa6-bddb-f98ed6d595e4",
    "5b0ef8e9-9b55-4a3e-aca6-d816d6bbc00f",
    Some("B000Q66HUA"),
    3,
    coverArt)

  "Writing a tagged mp3 file" should {
    "correctly write all the tags" in new DefaultTempFileSystem {
      val flacIn = getClass.getClassLoader.getResourceAsStream("untagged.mp3")
      val tempMusicFile = rootDirectory.resolve("tagged.mp3")
      Files.copy(flacIn, tempMusicFile, StandardCopyOption.REPLACE_EXISTING)
      flacIn.close
      tagsService.write(tempMusicFile, tagsToWrite)
      val lines = Seq("id3v2", "-l", tempMusicFile.toString).lineStream.toList
      lines must contain(
        "UFID (Unique file identifier): http://musicbrainz.org, 36 bytes",
        "TIT2 (Title/songname/content description): Suffer The Children",
        "TPE1 (Lead performer(s)/Soloist(s)): Napalm Death",
        "TALB (Album/Movie/Show title): Metal: A Headbanger's Companion",
        "TRCK (Track number/Position in set): 3/17",
        "TPE2 (Band/orchestra/accompaniment): Various Artists",
        "TXXX (User defined text information): (MusicBrainz Album Artist Id): 89ad4ac3-39f7-470e-963a-56509c546377",
        "TXXX (User defined text information): (MusicBrainz Album Id): 6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e",
        "TXXX (User defined text information): (MusicBrainz Artist Id): ce7bba8b-026b-4aa6-bddb-f98ed6d595e4",
        "TXXX (User defined text information): (ASIN): B000Q66HUA",
        "TSO2 ():  frame",
        "TSOP ():  frame",
        "TPOS (Part of a set): 1/6",
        "APIC (Attached picture): ()[, 3]: image/jpeg, 61820 bytes")
    }
  }

  "Writing a tagged flac file" should {
    "correctly write all the tags" in new DefaultTempFileSystem {
      val flacIn = getClass.getClassLoader.getResourceAsStream("untagged.flac")
      val tempMusicFile = rootDirectory.resolve("tagged.flac")
      Files.copy(flacIn, tempMusicFile, StandardCopyOption.REPLACE_EXISTING)
      flacIn.close
      tagsService.write(tempMusicFile, tagsToWrite)
      val lines = Seq("metaflac", "--export-tags-to=-", tempMusicFile.toString).lineStream.toList
      lines must contain(
        "ALBUMARTISTSORT=Various Artists Sort",
        "ALBUMARTIST=Various Artists",
        "ALBUM=Metal: A Headbanger's Companion",
        "ARTIST=Napalm Death", "ARTISTSORT=Napalm Death Sort",
        "TITLE=Suffer The Children",
        "DISCTOTAL=6",
        "TRACKTOTAL=17",
        "DISCNUMBER=1",
        "MUSICBRAINZ_ALBUMARTISTID=89ad4ac3-39f7-470e-963a-56509c546377",
        "MUSICBRAINZ_ALBUMID=6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e",
        "MUSICBRAINZ_ARTISTID=ce7bba8b-026b-4aa6-bddb-f98ed6d595e4",
        "MUSICBRAINZ_TRACKID=5b0ef8e9-9b55-4a3e-aca6-d816d6bbc00f",
        "ASIN=B000Q66HUA",
        "TRACKNUMBER=3")
    }
  }
}