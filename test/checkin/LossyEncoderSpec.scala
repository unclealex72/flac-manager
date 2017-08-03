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

package checkin

import java.nio.file.{Files, Path, Paths}

import common.music.{JaudioTaggerTagsService, Tags, TagsService}
import org.specs2.matcher.Matcher
import org.specs2.mutable.Specification

/**
  * Created by alex on 23/07/17
  **/
class LossyEncoderSpec extends Specification {

  sequential

  val taggedFlacFile: Path = Option(getClass.getClassLoader.getResource("tone.flac")).map(u => Paths.get(u.toURI)).getOrElse {
    throw new IllegalStateException("Cannot find tagged flac file.")
  }

  val tagsService: TagsService = new JaudioTaggerTagsService()

  val tags: Tags =
    tagsService.readTags(taggedFlacFile)

  "encoding to an mp3 file" should {
    "produce a valid mp3 file" in {
      val encodedFile = encodeFile(new LameMp3Encoder() {override val logLevel: String = "verbose"})
      encodedFile must beRight(haveTagsAndMagicNumber(tags, 0, 0x49, 0x44, 0x33))
    }
  }

  "encoding to an m4a file" should {
    "produce a valid m4a file" in {
      val encodedFile = encodeFile(new FdkaacM4AEncoder() {override val logLevel: String = "verbose"})
      encodedFile must beRight(haveTagsAndMagicNumber(tags, 4, 0x66, 0x74, 0x79, 0x70, 0x4D, 0x34, 0x41, 0x20))
    }
  }

  def haveTagsAndMagicNumber(tags: Tags, offset: Int, magicNumber: Int*): Matcher[EncodedFile] = {
    haveMagicNumber(offset, magicNumber: _*) and haveTags(tags)
  }

  def haveMagicNumber(offset: Int, magicNumber: Int*): Matcher[EncodedFile] = {
    def toHex(numbers: Seq[Int]): String = numbers.map(Integer.toHexString).mkString("")
    ((encodedFile:EncodedFile) => toHex(encodedFile.content.slice(offset, offset + magicNumber.length))) ^^ be_===(toHex(magicNumber))
  }

  def haveTags(tags: Tags): Matcher[EncodedFile] = {
    def beTags(tags: Tags): Matcher[Tags] = {
      def haveTag[V](msg: String, extractor: Tags => V): Matcher[Tags] = be_===(extractor(tags)) ^^ { (tags: Tags) => extractor(tags) aka msg }
      def haveArtist = haveTag("the album artist", _.albumArtist)
      def haveAlbum = haveTag("the album", _.album)
      def haveTrackNumber = haveTag("the track number", _.trackNumber)
      def haveTitle = haveTag("the track title", _.title)
      haveArtist and haveAlbum and haveTrackNumber and haveTitle
    }
    beTags(tags) ^^ { (ef: EncodedFile) => ef.tags aka "tags" }
  }

  def encodeFile(lossyEncoder: LossyEncoder): Either[String, EncodedFile] = {
    val out = Files.createTempFile("lossy-encoder-test", s".${lossyEncoder.encodesTo}")
    def encode(in: Path): Either[String, Unit] = {
      val result = lossyEncoder.encode(in, out)
      if (result == 0) Right({}) else Left(s"The encoding command returned error code $result")
    }
    try {
      for {
        _ <- encode(taggedFlacFile)

      } yield {
        EncodedFile(Files.readAllBytes(out).map(java.lang.Byte.toUnsignedInt).toSeq, tagsService.readTags(out))
      }
    }
    finally {
      Files.deleteIfExists(out)
    }
  }

  case class EncodedFile(content: Seq[Int], tags: Tags) {
    override def toString: String = f"${tags.albumArtist}, ${tags.artist}, ${tags.trackNumber}%02d ${tags.title}"
  }
}
