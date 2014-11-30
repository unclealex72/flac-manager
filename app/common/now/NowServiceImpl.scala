package common.now

import org.joda.time.DateTime

/**
 * Created by alex on 30/11/14.
 */
class NowServiceImpl extends NowService {
  /**
   * Get the current date and time.
   * @return
   */
  override def now(): DateTime = new DateTime()
}
