package common.configuration

import play.api.Configuration

/**
 * Created by alex on 20/11/14.
 */
abstract class PlayConfiguration[T](val configuration: Configuration) {

  val optionalResult = load(configuration)

  lazy val result: T = optionalResult.getOrElse {
    throw new IllegalArgumentException("Could not read configuration for " + getClass)
  }

  def load(configuration: Configuration): Option[T]
}
