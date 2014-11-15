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
    val tags = Tags(
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
      ownerService.listCollections()(messageService)(tags) must beEqualTo(Set(brian))
      there was one(messageService).printMessage(READING_COLLECTION(brian))
      there was one(messageService).printMessage(READING_COLLECTION(freddie))
      noMoreCallsTo(messageService)
    }
  }
}
