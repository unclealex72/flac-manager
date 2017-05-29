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
  * A [[MessageServiceBuilder]] that builds messages using Play's bundle support.
  * @param messagesApi Play's messages API.
  * @param printers A list of callbacks to call when a message needs to be printed.
  * @param exceptionHandlers A list of callbacks to call when an exception needs to be handled.
  */
class I18nMessageServiceBuilder(
                                 messagesApi: MessagesApi,
                                 printers: Seq[String => Unit],
                                 exceptionHandlers: Seq[Throwable => Unit]) extends MessageServiceBuilder with ApplicationLogging {

  /**
    * @inheritdoc
    */
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

  }

  /**
    * @inheritdoc
    */
  override def withPrinter(printer: String => Unit): MessageServiceBuilder = {
    new I18nMessageServiceBuilder(messagesApi, printers :+ printer, exceptionHandlers)
  }

  /**
    * @inheritdoc
    */
  override def withExceptionHandler(exceptionHandler: Throwable => Unit): MessageServiceBuilder = {
    new I18nMessageServiceBuilder(messagesApi, printers, exceptionHandlers :+ exceptionHandler)
  }

}

/**
  * Create new instances of [[MessageServiceBuilder]]
  */
object I18nMessageServiceBuilder {

  private val logger: Logger = LoggerFactory.getLogger("messages")

  /**
    * Create a new [[MessageServiceBuilder]]
    * @param messagesApi Play's messages API.
    * @return A new [[MessageServiceBuilder]] that logs its output.
    */
  def apply(messagesApi: MessagesApi): MessageServiceBuilder =
    new I18nMessageServiceBuilder(messagesApi, Seq(), Seq()).
      withPrinter(message => logger.info(message)).
      withExceptionHandler(t => logger.error("An unexpected exception occurred.", t))

}