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
