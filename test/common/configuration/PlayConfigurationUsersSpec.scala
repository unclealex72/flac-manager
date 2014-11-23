package common.configuration

import java.io.StringReader

import com.typesafe.config.ConfigFactory
import org.specs2.mutable._
import play.api.Configuration

/**
 * Created by alex on 20/11/14.
 */
class PlayConfigurationUsersSpec extends Specification {

  "reading an empty configuration" should {
    "throw an exception" in {
      PlayConfigurationUsers(Configuration.from(Map.empty[String, AnyRef])).allUsers must throwA[IllegalArgumentException]
    }
  }

  "reading a configuration with no users" should {
    "throw an exception" in {
      def conf =
        """
          |users = []
        """.stripMargin
      val config = Configuration(ConfigFactory.parseReader(new StringReader(conf)))
      PlayConfigurationUsers(config).allUsers must throwA[IllegalArgumentException]
    }
  }

  "reading two users" should {
    "return the two users" in {
      def conf =
        """
          |users = ["freddie", "brian"]
          |user.freddie.musicbrainz.username=freddie1
          |user.freddie.musicbrainz.password=fr3dd13
          |user.freddie.deviceId=12345
          |user.brian.musicbrainz.username=brian1
          |user.brian.musicbrainz.password="br1@n"
          |user.brian.deviceId=23456
        """.stripMargin
      val config = Configuration(ConfigFactory.parseReader(new StringReader(conf)))
      PlayConfigurationUsers(config).allUsers must beEqualTo(
        Set(User("freddie", "freddie1", "fr3dd13", "12345"), User("brian", "brian1", "br1@n", "23456"))
      )
    }
  }
}
