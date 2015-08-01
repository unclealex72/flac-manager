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

import com.typesafe.config.ConfigFactory
import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.specification.Scope
import play.api.Configuration
import sync.Device

import scalaz.Success

/**
 * Created by alex on 20/11/14.
 */
class PlayConfigurationUsersSpec extends Specification with Mockito {

  trait Context extends Scope {
    lazy val deviceLocator = mock[DeviceLocator]
    lazy val deviceA = mock[Device]
    lazy val deviceB = mock[Device]
    lazy val deviceC = mock[Device]
    deviceLocator.device("freddie", "deviceA") returns Success(deviceA)
    deviceLocator.device("brian", "deviceB") returns Success(deviceB)
    deviceLocator.device("brian", "deviceC") returns Success(deviceC)
  }

  "reading an empty configuration" should {
    "throw an exception" in new Context {
      PlayConfigurationUsers(Configuration.from(Map.empty[String, AnyRef]), deviceLocator).allUsers must throwA[IllegalArgumentException]
    }
  }

  "reading a configuration with no users" should {
    "throw an exception" in new Context {
      def conf =
        """
          |users = []
        """.stripMargin
      val config = Configuration(ConfigFactory.parseReader(new StringReader(conf)))
      PlayConfigurationUsers(config, deviceLocator).allUsers must throwA[IllegalArgumentException]
    }
  }

  "reading two users" should {
    "return the two users" in new Context {
      def conf =
        """
          |users = ["freddie", "brian"]
          |user.freddie.musicbrainz.username=freddie1
          |user.freddie.musicbrainz.password=fr3dd13
          |user.freddie.devices= ["deviceA"]
          |user.brian.musicbrainz.username=brian1
          |user.brian.musicbrainz.password="br1@n"
          |user.brian.devices= ["deviceB","deviceC"]
        """.stripMargin
      val config = Configuration(ConfigFactory.parseReader(new StringReader(conf)))
      PlayConfigurationUsers(config, deviceLocator).allUsers must beEqualTo(
        Set(User("freddie", "freddie1", "fr3dd13", Seq(deviceA)), User("brian", "brian1", "br1@n", Seq(deviceB, deviceC)))
      )
    }
  }
}
