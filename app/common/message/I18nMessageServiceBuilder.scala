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

import org.slf4j.LoggerFactory
import play.api.i18n.Messages

/**
 * A `MessageServiceBuilder` that builds messages using Play's bundle support.
 * Created by alex on 06/11/14.
 */
class I18nMessageServiceBuilder(printers: Seq[String => Unit], exceptionHandlers: Seq[Throwable => Unit], onFinishes: Seq[() => Unit]) extends MessageServiceBuilder {

  override def build: MessageService = new MessageService() {

    override def printMessage(template: MessageType): Unit = {
      val message = Messages(template.key, template.parameters :_*)
      printers.foreach(printer => printer(message))
    }

    override def exception(t: Throwable): Unit = {
      exceptionHandlers.foreach(exceptionHandler => exceptionHandler(t))
    }

    override def finish(): Unit = {
      onFinishes.foreach(block => block())
    }
  }

  override def withPrinter(printer: String => Unit): MessageServiceBuilder = {
    new I18nMessageServiceBuilder(printers :+ printer, exceptionHandlers, onFinishes)
  }

  override def withExceptionHandler(exceptionHandler: Throwable => Unit): MessageServiceBuilder = {
    new I18nMessageServiceBuilder(printers, exceptionHandlers :+ exceptionHandler, onFinishes)
  }

  override def withOnFinish(onFinish: () => Unit): MessageServiceBuilder = {
    new I18nMessageServiceBuilder(printers, exceptionHandlers, onFinishes :+ onFinish)
  }
}

object I18nMessageServiceBuilder {

  val logger = LoggerFactory.getLogger("messages")
  def apply(): MessageServiceBuilder =
    new I18nMessageServiceBuilder(Seq(), Seq(), Seq()).
      withPrinter(message => logger.info(message)).
      withExceptionHandler(t => logger.error("An unexpected exception occurred.", t)).
      withOnFinish(() => logger.info(s"Commmand has completed."))

}