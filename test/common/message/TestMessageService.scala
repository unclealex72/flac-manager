package common.message

/**
 * Created by alex on 15/11/14.
 */
trait TestMessageService extends MessageService {

  def printMessage(template: MessageType): Unit
}
