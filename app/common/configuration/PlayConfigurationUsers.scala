package common.configuration

import java.nio.file.Paths

import play.api.Configuration

/**
 * Get the users using Play configuration
 * Created by alex on 20/11/14.
 */
case class PlayConfigurationUsers(override val configuration: Configuration) extends PlayConfiguration[Set[User]](configuration) with Users {

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
