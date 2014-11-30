package common.now

import org.joda.time.DateTime

/**
 * Created by alex on 30/11/14.
 */
trait NowService {

  /**
   * Get the current date and time.
   * @return
   */
  def now(): DateTime
}
