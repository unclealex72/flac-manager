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

package common.message

import logging.ApplicationLogging
import org.slf4j.{Logger, LoggerFactory}
import play.api.i18n.MessagesApi

/**
 * A `MessageServiceBuilder` that builds messages using Play's bundle support.
 * Created by alex on 06/11/14.
 */
class I18nMessageServiceBuilder(messagesApi: MessagesApi, printers: Seq[String => Unit], exceptionHandlers: Seq[Throwable => Unit], onFinishes: Seq[() => Unit]) extends MessageServiceBuilder with ApplicationLogging {

  override def build: MessageService = new MessageService() {

    override def printMessage(template: Message): Unit = {
      val message = messagesApi(template.key, template.parameters :_*)
      logger.debug(message)
      printers.foreach(printer => printer(message))
    }

    override def exception(t: Throwable): Unit = {
      logger.error("An unexpected error occurred.", t)
      exceptionHandlers.foreach(exceptionHandler => exceptionHandler(t))
    }

    override def finish(): Unit = {
      logger.info("Command finished")
      onFinishes.foreach(block => block())
    }
  }

  override def withPrinter(printer: String => Unit): MessageServiceBuilder = {
    new I18nMessageServiceBuilder(messagesApi, printers :+ printer, exceptionHandlers, onFinishes)
  }

  override def withExceptionHandler(exceptionHandler: Throwable => Unit): MessageServiceBuilder = {
    new I18nMessageServiceBuilder(messagesApi, printers, exceptionHandlers :+ exceptionHandler, onFinishes)
  }

  override def withOnFinish(onFinish: () => Unit): MessageServiceBuilder = {
    new I18nMessageServiceBuilder(messagesApi, printers, exceptionHandlers, onFinishes :+ onFinish)
  }
}

object I18nMessageServiceBuilder {

  val logger: Logger = LoggerFactory.getLogger("messages")
  def apply(messagesApi: MessagesApi): MessageServiceBuilder =
    new I18nMessageServiceBuilder(messagesApi, Seq(), Seq(), Seq()).
      withPrinter(message => logger.info(message)).
      withExceptionHandler(t => logger.error("An unexpected exception occurred.", t)).
      withOnFinish(() => logger.info(s"Command has completed."))

}