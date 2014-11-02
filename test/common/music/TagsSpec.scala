/**
 * Copyright 2011 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * @author unclealex72
 *
 */

package common.music

import java.nio.file.Paths

import common.files.FLAC
import org.specs2.mutable._

import scala.util.{Success, Try}

/**
 * @author alex
 *
 */
class TagsSpec extends Specification {

  "A track on a single disc" should {
    "not print a disc number suffix" in {
      SimpleTags("Mötörhead", "Good - Stuff ", 1, 1, 2, "The Ace of Spades").asPath(FLAC) must be equalTo (
        Paths.get("M", "Motorhead", "Good Stuff", "02 The Ace of Spades.flac"))
    }
  }

  "A track on a non-single disc" should {
    "print a disc number suffix" in {
      SimpleTags("Mötörhead", "Good - Stuff ", 1, 2, 2, "The Ace of Spades").asPath(FLAC) must be equalTo (
        Paths.get("M", "Motorhead", "Good Stuff 01", "02 The Ace of Spades.flac"))
    }
  }

  object SimpleTags {
    def apply(albumArtistSort: String, album: String, discNumber: Int, totalDiscs: Int, trackNumber: Int, title: String): Tags = Tags(
      albumArtistSort, "", album, "", "", title,
      totalDiscs, 0, discNumber, "", "", "", "",
      Some(""), trackNumber, null)
  }

}
