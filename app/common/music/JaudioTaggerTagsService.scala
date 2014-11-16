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

import java.nio.file.Path

import org.jaudiotagger.audio.{AudioFileIO, AudioFile}
import org.jaudiotagger.tag.datatype.Artwork
import org.jaudiotagger.tag.{FieldKey, Tag}
import org.jaudiotagger.tag.id3.{FixedID3v23Tag, ID3v23Tag}
import org.jaudiotagger.tag.FieldKey.{ALBUM => J_ALBUM}
import org.jaudiotagger.tag.FieldKey.{ALBUM_ARTIST => J_ALBUM_ARTIST}
import org.jaudiotagger.tag.FieldKey.{ALBUM_ARTIST_SORT => J_ALBUM_ARTIST_SORT}
import org.jaudiotagger.tag.FieldKey.{AMAZON_ID => J_AMAZON_ID}
import org.jaudiotagger.tag.FieldKey.{ARTIST => J_ARTIST}
import org.jaudiotagger.tag.FieldKey.{ARTIST_SORT => J_ARTIST_SORT}
import org.jaudiotagger.tag.FieldKey.{DISC_NO => J_DISC_NO}
import org.jaudiotagger.tag.FieldKey.{DISC_TOTAL => J_DISC_TOTAL}
import org.jaudiotagger.tag.FieldKey.{MUSICBRAINZ_ARTISTID => J_MUSICBRAINZ_ARTISTID}
import org.jaudiotagger.tag.FieldKey.{MUSICBRAINZ_RELEASEARTISTID => J_MUSICBRAINZ_RELEASEARTISTID}
import org.jaudiotagger.tag.FieldKey.{MUSICBRAINZ_RELEASEID => J_MUSICBRAINZ_RELEASEID}
import org.jaudiotagger.tag.FieldKey.{MUSICBRAINZ_TRACK_ID => J_MUSICBRAINZ_TRACK_ID}
import org.jaudiotagger.tag.FieldKey.{TITLE => J_TITLE}
import org.jaudiotagger.tag.FieldKey.{TRACK => J_TRACK}
import org.jaudiotagger.tag.FieldKey.{TRACK_TOTAL => J_TRACK_TOTAL}
import scala.util.Try

/**
 * A TagsService that uses JAudioTagger.
 * Created by alex on 02/11/14.
 */
class JaudioTaggerTagsService extends TagsService {

  override def read(path: Path): Tags = {
    val audioFile = loadAudioFile(path)
    val tag = audioFile.getTag
    implicit def get[V](singleTag: SingleTag[V]): V = singleTag.get(tag)
    Tags(
      ALBUM_ARTIST_SORT, ALBUM_ARTIST, ALBUM, ARTIST,
      ARTIST_SORT, TITLE, TOTAL_DISCS, TOTAL_TRACKS, DISC_NUMBER,
      ALBUM_ARTIST_ID, ALBUM_ID, ARTIST_ID, TRACK_ID, ASIN, TRACK_NUMBER, COVER_ART)
  }

  override def write(path: Path, tags: Tags): Unit = Try {
    val audioFile = loadAudioFile(path)
    implicit val tag = audioFile.getTag
    ALBUM_ARTIST_SORT.set(tags.albumArtistSort)
    ALBUM_ARTIST.set(tags.albumArtist)
    ALBUM.set(tags.album)
    ARTIST.set(tags.artist)
    ARTIST_SORT.set(tags.artistSort)
    TITLE.set(tags.title)
    TOTAL_DISCS.set(tags.totalDiscs)
    TOTAL_TRACKS.set(tags.totalTracks)
    DISC_NUMBER.set(tags.discNumber)
    ALBUM_ARTIST_ID.set(tags.albumArtistId)
    ALBUM_ID.set(tags.albumId)
    ARTIST_ID.set(tags.artistId)
    TRACK_ID.set(tags.trackId)
    ASIN.set(tags.asin)
    TRACK_NUMBER.set(tags.trackNumber)
    COVER_ART.set(tags.coverArt)
    audioFile.commit
  }

  def loadAudioFile(path: Path): AudioFile = {
    val audioFile = AudioFileIO.read(path.toFile)
    var tag: Tag = audioFile.getTag
    if (tag == null) {
      tag = audioFile.createDefaultTag
      audioFile.setTag(tag)
    }
    if (tag.isInstanceOf[ID3v23Tag]) {
      tag = new FixedID3v23Tag(tag.asInstanceOf[ID3v23Tag])
      audioFile.setTag(tag)
    }
    audioFile
  }
}

sealed abstract class SingleTag[V] {
  def get(tag: Tag): V

  def set(value: V)(implicit tag: Tag): Unit
}

sealed abstract class SimpleTag[V](fieldKey: FieldKey) extends SingleTag[Option[V]] {

  def get(tag: Tag): Option[V] = Option(tag.getFirst(fieldKey)).map(v => parse(v))

  def set(value: Option[V])(implicit tag: Tag) = {
    value.foreach(value => tag.setField(fieldKey, value.toString))
  }

  def parse(str: String): V
}

sealed abstract class MandatoryTag[V](singleTag: SingleTag[Option[V]], default: V) extends SingleTag[V] {

  def get(tag: Tag) = singleTag.get(tag).getOrElse(default)

  def set(value: V)(implicit tag: Tag) = singleTag.set(Some(value))
}

sealed class OptionalStringTag(fieldKey: FieldKey) extends SimpleTag[String](fieldKey) {
  def parse(str: String) = str
}

sealed class OptionalIntTag(fieldKey: FieldKey) extends SimpleTag[Int](fieldKey) {
  def parse(str: String) = str.toInt
}

sealed class StringTag(fieldKey: FieldKey) extends MandatoryTag[String](new OptionalStringTag(fieldKey), "")

sealed class IntTag(fieldKey: FieldKey) extends MandatoryTag[Int](new OptionalIntTag(fieldKey), 0)

object ALBUM extends StringTag(J_ALBUM)

object ALBUM_ARTIST extends StringTag(J_ALBUM_ARTIST)

object ALBUM_ARTIST_SORT extends StringTag(J_ALBUM_ARTIST_SORT)

object ASIN extends OptionalStringTag(J_AMAZON_ID)

object ARTIST extends StringTag(J_ARTIST)

object ARTIST_SORT extends StringTag(J_ARTIST_SORT)

object DISC_NUMBER extends IntTag(J_DISC_NO)

object TOTAL_DISCS extends IntTag(J_DISC_TOTAL)

object ARTIST_ID extends StringTag(J_MUSICBRAINZ_ARTISTID)

object ALBUM_ARTIST_ID extends StringTag(J_MUSICBRAINZ_RELEASEARTISTID)

object ALBUM_ID extends StringTag(J_MUSICBRAINZ_RELEASEID)

object TRACK_ID extends StringTag(J_MUSICBRAINZ_TRACK_ID)

object TITLE extends StringTag(J_TITLE)

object TRACK_NUMBER extends IntTag(J_TRACK)

object TOTAL_TRACKS extends IntTag(J_TRACK_TOTAL)

object COVER_ART extends SingleTag[CoverArt] {

  import scala.collection.JavaConversions._

  private val FRONT_COVER_ART: Int = 3

  def get(tag: Tag): CoverArt = {
    val artwork = tag.getArtworkList.find(a => FRONT_COVER_ART == a.getPictureType)
    artwork.map { artwork =>
      CoverArt(artwork.getBinaryData, artwork.getMimeType)
    }.getOrElse(null.asInstanceOf[CoverArt])
  }

  override def set(coverArt: CoverArt)(implicit tag: Tag): Unit = {
    val artwork: Artwork = new Artwork
    artwork.setPictureType(FRONT_COVER_ART)
    artwork.setBinaryData(coverArt.imageData)
    artwork.setMimeType(coverArt.mimeType)
    tag.setField(artwork)
  }
}