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

import java.nio.file.Paths

import com.wix.accord._
import common.files.FLAC
import org.specs2.mutable._

/**
 * @author alex
 *
 */
class TagsSpec extends Specification {

  "A track on a single disc" should {
    "not print a disc number suffix" in {
      SimpleTags("Mötörhead", "Good - Stuff ", 1, 1, 2, "The Ace of Spades").asPath(FLAC) must be equalTo
        Paths.get("M", "Motorhead", "Good Stuff", "02 The Ace of Spades.flac")
    }
  }

  "A track on a non-single disc" should {
    "print a disc number suffix" in {
      SimpleTags("Mötörhead", "Good - Stuff ", 1, 2, 2, "The Ace of Spades").asPath(FLAC) must be equalTo
        Paths.get("M", "Motorhead", "Good Stuff 01", "02 The Ace of Spades.flac")
    }
  }

  "validating a totally empty tag" should {
    "report every single violation" in {
      val result = validate(SimpleTags("", "", 0, 0, 0, ""))
      result must beAnInstanceOf[Failure]
      result match {
        case Success =>
        case Failure(violations: Set[Violation]) => {
          val descriptions = violations.flatMap(_.description)
          descriptions must contain(exactly(
            "totalTracks",
            "album",
            "albumArtistId",
            "discNumber",
            "artistSort",
            "albumId",
            "title",
            "coverArt",
            "albumArtist",
            "artist",
            "trackId",
            "artistId",
            "trackNumber",
            "totalDiscs",
            "albumArtistSort"))
        }
      }
      true must beTrue
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
        "5b0ef8e9-9b55-4a3e-aca6-d816d6bbc00f",
        Some("B000Q66HUA"),
        3,
        CoverArt(new Array[Byte](0), "mime"))
      validate(validTag) must not(beAnInstanceOf[Failure])
    }
  }
  object SimpleTags {
    def apply(albumArtistSort: String, album: String, discNumber: Int, totalDiscs: Int, trackNumber: Int, title: String): Tags = Tags(
      albumArtistSort, "", album, "", "", title,
      totalDiscs, 0, discNumber, "", "", "", "",
      Some(""), trackNumber, null)
  }

}
