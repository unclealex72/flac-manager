package common.configuration

import java.io.StringReader
import java.nio.file.Paths

import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification
import play.api.Configuration

/**
 * Created by alex on 20/11/14.
 */
class PlayConfigurationDirectoriesSpec extends Specification {

  "reading in a list of directories" should {
    "correctly read the directories" in {
      def conf =
        """
          |directories.flac=/flac
          |directories.tmp=/tmp
          |directories.staging=/staging
          |directories.encoded=/encoded
          |directories.devices=/devices
        """.stripMargin
      val config = Configuration(ConfigFactory.parseReader(new StringReader(conf)))
      val directories = PlayConfigurationDirectories(config)
      directories.flacPath must be equalTo Paths.get("/flac")
      directories.stagingPath must be equalTo Paths.get("/staging")
      directories.encodedPath must be equalTo Paths.get("/encoded")
      directories.devicesPath must be equalTo Paths.get("/devices")
      directories.temporaryPath must be equalTo Paths.get("/tmp")

    }
  }
}
