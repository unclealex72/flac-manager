package initialise

import common.message.MessageService

/**
 * Created by alex on 06/12/14.
 */
trait InitialiseCommand {

  /**
   * Initialise the datbase with all device files.
   */
  def initialiseDb(implicit messageService: MessageService): Unit
}
