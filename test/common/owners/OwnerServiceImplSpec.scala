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

package common.owners

import common.configuration.{User, UserDao}
import common.message.Messages._
import common.message.TestMessageService
import common.music.{CoverArt, Tags}
import common.collections.CollectionDao
import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.specification.Scope

/**
 * Created by alex on 15/11/14.
 */
class OwnerServiceImplSpec extends Specification with Mockito {

  trait Context extends Scope {
    lazy implicit val messageService = TestMessageService()
    lazy val musicBrainzClient: CollectionDao = mock[CollectionDao]
    lazy val ownerService = new OwnerServiceImpl(musicBrainzClient, userDao)
    val brian = User("Brian")
    val freddie = User("Freddie")
    val tags1 = Tags(
      album = "Metal: A Headbanger's Companion",
      albumArtist = "Various Artists",
      albumArtistId = "89ad4ac3-39f7-470e-963a-56509c546377",
      albumArtistSort = "Various Artists Sort",
      albumId = "6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e",
      artist = "Napalm Death",
      artistId = "ce7bba8b-026b-4aa6-bddb-f98ed6d595e4",
      artistSort = "Napalm Death Sort",
      asin = Some("B000Q66HUA"),
      title = "Suffer The Children",
      trackId = "5b0ef8e9-9b55-4a3e-aca6-d816d6bbc00f",
      coverArt = CoverArt(Array[Byte](), ""),
      discNumber = 1,
      totalDiscs = 6,
      totalTracks = 17,
      trackNumber = 3)
    val tags2 = Tags(
      title = "Incarnated Solvent Abuse",
      totalDiscs = 6,
      totalTracks = 17,
      albumArtistId = "89ad4ac3-39f7-470e-963a-56509c546377",
      discNumber = 1,
      asin = Some("B000Q66HUA"),
      albumArtistSort = "Various Artists",
      albumId = "6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e",
      albumArtist = "Various Artists",
      album = "Metal: A Headbanger's Companion",
      artistId = "d50a4b89-ff1f-4659-9fde-f76f8d5b3c89",
      artist = "Carcass",
      trackId = "1695180d-afb5-4eb0-9078-d5e74e0c1e21",
      artistSort = "Carcass",
      trackNumber = 5,
      coverArt = CoverArt(Array[Byte](), ""))
    val userDao = new UserDao {
      override def allUsers: Set[User] = Set(brian, freddie)
    }
  }

  "Finding who owns a track" should {
    "correctly identify whose collections it is in" in new Context {
      musicBrainzClient.releasesForOwner(brian) returns Seq("6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e")
      musicBrainzClient.releasesForOwner(freddie) returns Seq.empty
      ownerService.listCollections()(tags1) must beEqualTo(Set(brian))
      noMoreCallsTo(messageService)
    }
  }

  "Adding tracks to a collection" should {
    "delegate to the MusicBrainz client" in new Context {
      ownerService.own(freddie, Set(tags1, tags2))
      there was one(musicBrainzClient).addReleases(freddie, Set("6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e"))
      noMoreCallsTo(musicBrainzClient)
    }
  }

  "Removing tracks from a collection" should {
    "delegate to the MusicBrainz client" in new Context {
      ownerService.unown(freddie, Set(tags1, tags2))
      there was one(musicBrainzClient).removeReleases(freddie, Set("6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e"))
      noMoreCallsTo(musicBrainzClient)
    }
  }
}
