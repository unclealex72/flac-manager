package common.owners

import common.configuration.{User, Users}
import common.message.MessageTypes._
import common.message.TestMessageService
import common.music.{CoverArt, Tags}
import common.musicbrainz.MusicBrainzClient
import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.specification.Scope

import scala.concurrent._

/**
 * Created by alex on 15/11/14.
 */
class OwnerServiceImplSpec extends Specification with Mockito {

  trait Context extends Scope {
    val brian = User("Brian", "", "", "")
    val freddie = User("Freddie", "", "", "")
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
    val users = new Users {
      override def allUsers: Seq[User] = Seq(brian, freddie)
    }

    lazy implicit val messageService = mock[TestMessageService]
    lazy val musicBrainzClient = mock[MusicBrainzClient]
    lazy val ownerService = new OwnerServiceImpl(musicBrainzClient, users)
  }

  "Finding who owns a track" should {
    "correctly identify whose collections it is in" in new Context {
      musicBrainzClient.relasesForOwner(brian) returns Future {
        Seq("6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e")
      }
      musicBrainzClient.relasesForOwner(freddie) returns Future {
        Seq.empty
      }
      ownerService.listCollections()(messageService)(tags1) must beEqualTo(Set(brian))
      there was one(messageService).printMessage(READING_COLLECTION(brian))
      there was one(messageService).printMessage(READING_COLLECTION(freddie))
      noMoreCallsTo(messageService)
    }
  }

  "Adding tracks to a collection" should {
    "delegate to the MusicBrainz client" in new Context {
      musicBrainzClient.addReleases(any[User], any[Set[String]]) returns Future {}
      ownerService.own(freddie, Seq(tags1, tags2))
      there was one(musicBrainzClient).addReleases(freddie, Set("6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e"))
      noMoreCallsTo(musicBrainzClient)
    }
  }

  "Removing tracks from a collection" should {
    "delegate to the MusicBrainz client" in new Context {
      musicBrainzClient.removeReleases(any[User], any[Set[String]]) returns Future {}
      ownerService.unown(freddie, Seq(tags1, tags2))
      there was one(musicBrainzClient).removeReleases(freddie, Set("6fe49afc-94b5-4214-8dd9-a5b7b1a1e77e"))
      noMoreCallsTo(musicBrainzClient)
    }
  }
}
