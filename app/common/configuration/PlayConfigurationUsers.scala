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

import java.nio.file.Paths

import com.typesafe.scalalogging.StrictLogging
import play.api.Configuration

/**
 * Get the users using Play configuration
 * Created by alex on 20/11/14.
 */
case class PlayConfigurationUsers(override val configuration: Configuration) extends PlayConfiguration[Set[User]](configuration) with Users with StrictLogging {

  def load(configuration: Configuration): Option[Set[User]] = {
    configuration.getStringSeq("users").map { usernames =>
      val users = for {
        username <- usernames
        userConfig <- configuration.getConfig(s"user.$username")
        musicbrainzUsername <- userConfig.getString("musicbrainz.username")
        musicbrainzPassword <- userConfig.getString("musicbrainz.password")
        mountPoint <- userConfig.getString("mountPoint")
      } yield User(username, musicbrainzUsername, musicbrainzPassword, Paths.get(mountPoint))
      users.toSet
    }.flatMap(users => if (users.size == 0) None else Some(users))
  }

  override def allUsers = result
}
