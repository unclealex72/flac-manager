package common.changes

import org.joda.time.DateTime
import org.specs2.matcher.MustMatchers

/**
 * Matchers for changes.
 * Created by alex on 30/11/14.
 */
trait ChangeMatchers extends MustMatchers {

  def beTheSameChangeAs = (be_==(_:(String, String, Long, String))) ^^^ ((c: Change) => (c.user, c.action, c.at.getMillis, c.relativePath))
}
