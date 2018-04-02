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

import java.nio.file.{FileSystem, FileSystems, Paths}

import cats.data.NonEmptyList
import common.files.Extension.FLAC
import common.message.Message
import org.specs2.mutable._

/**
 * @author alex
 *
 */
class TagsSpec extends Specification {

  val fs: FileSystem = FileSystems.getDefault

  "A track on a single disc" should {
    "not print a disc number suffix" in {
      SimpleTags("Mötörhead", "Good - Stuff ", 1, 1, 2, "The Ace of Spades").asPath(fs, FLAC) must be equalTo
        Paths.get("M", "Motorhead", "Good Stuff", "02 The Ace of Spades.flac")
    }
  }

  "A track on a non-single disc" should {
    "print a disc number suffix" in {
      SimpleTags("Mötörhead", "Good - Stuff ", 1, 2, 2, "The Ace of Spades").asPath(fs, FLAC) must be equalTo
        Paths.get("M", "Motorhead", "Good Stuff 01", "02 The Ace of Spades.flac")
    }
  }

  "validating a totally empty tag" should {
    "report every single violation" in {
      Tags.validate(SimpleTags("", "", 0, 0, 0, "")).toEither must beLeft { (errors: NonEmptyList[String]) =>
        errors.toList must contain(exactly(
          "Album artist sort is required",
          "Album artist is required",
          "Album is required",
          "Artist sort is required",
          "Title is required",
          "Total discs must be greater than zero",
          "Total tracks must be greater than zero",
          "Disc number must be greater than zero",
          "Album artist ID is required",
          "Artist ID is required",
          "Album ID is required",
          "Track number must be greater than zero",
          "Cover art is required"
        ))
      }
    }
  }

  "validating a well-defined tag" should {
    "succeed" in {
      val validTag = Tags(
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
        Some("5b0ef8e9-9b55-4a3e-aca6-d816d6bbc00f"),
        Some("B000Q66HUA"),
        3,
        CoverArt(new Array[Byte](0), "mime"))
      Tags.validate(validTag).toEither must beRight
    }
  }

  object SimpleTags {
    def apply(albumArtistSort: String, album: String, discNumber: Int, totalDiscs: Int, trackNumber: Int, title: String): Tags = Tags(
      albumArtistSort, "", album, "", "", title,
      totalDiscs, 0, discNumber, "", "", "", Some(""),
      Some(""), trackNumber, null)
  }

}
