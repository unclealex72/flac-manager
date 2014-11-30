package common.message

import com.typesafe.scalalogging.StrictLogging
import org.mockito.invocation.InvocationOnMock
import org.specs2.mock.Mockito

/**
 * Created by alex on 15/11/14.
 */
trait TestMessageService extends MessageService {

  def printMessage(template: MessageType): Unit
}

object TestMessageService extends Mockito with StrictLogging {

  def apply(): TestMessageService = {
    val loggingAnswer = (p1: InvocationOnMock) => logger.info(p1.toString)
    mock[TestMessageService].defaultAnswer(loggingAnswer)
  }
}