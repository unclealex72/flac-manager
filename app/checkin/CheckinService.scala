package checkin

import common.message.MessageService

/**
 * Created by alex on 15/11/14.
 */
trait CheckinService {
  def checkin(action: Action)(implicit messagingService: MessageService): Unit

}
