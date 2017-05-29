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

package controllers

import java.io.{PrintWriter, StringWriter}
import javax.inject.{Inject, Singleton}

import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Source
import cats.data.Validated.{Invalid, Valid}
import com.typesafe.scalalogging.StrictLogging
import common.message.{MessageService, MessageServiceBuilder, Messaging}
import play.api.libs.EventSource
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future, Promise}

/**
  * The controller that executes commands that alter repositories.
  * @param messageServiceBuilder The [[MessageServiceBuilder]] used to build a feedback mechanism.
  * @param commandBuilder A [[CommandBuilder]] used to build a command from a JSON RPC payload.
  * @param ec The execution context used to execute the command.
  */
@Singleton
class Commands @Inject()(
                          messageServiceBuilder: MessageServiceBuilder,
                          commandBuilder: CommandBuilder)
                        (implicit ec: ExecutionContext) extends Controller with Messaging with StrictLogging {


  /**
    * Take a JSON RPC payload an execute a command. The feedback from the command will be sent back to the
    * client using an HTTP chunked response so that feedback is received and processed as soon as possible.
    * @return A HTTP chunked response containing the feedback from the command.
    */
  def commands: Action[JsValue] = Action(parse.json) { implicit request =>
    val validatedCommandTypeBuilder = commandBuilder(request.body)
    validatedCommandTypeBuilder match {
      case Valid(commandFactory) =>
        val responseSource: Source[String, _] = generateResponse(commandFactory)
        Ok.chunked(responseSource).as(TEXT)
      case Invalid(neMessages) =>
        val messages = neMessages.toList
        messages.foreach { message =>
          logger.error(message)
        }
        BadRequest(messages.mkString("\n")).as(TEXT)
    }
  }

  def generateResponse(commandFactory: MessageService => Future[_]): Source[String, _] = {
    val (queueSource, futureQueue) = peekMatValue(Source.queue[String](128, OverflowStrategy.backpressure))
    futureQueue.map { queue =>
      def offer(message: Any) = queue.offer(s"$message\n")
      def printer(message: String): Unit = offer(message)
      def exceptionHandler(t: Throwable): Unit = {
        val sw = new StringWriter()
        t.printStackTrace(new PrintWriter(sw))
        offer(sw)
      }

      val messageService = messageServiceBuilder.
        withPrinter(printer).
        withExceptionHandler(exceptionHandler).build

      commandFactory(messageService).andThen {
        case _ =>
          printer("The command has completed successfully.")
          queue.complete()
      }.recover {
        case t =>
          exceptionHandler(t)
          queue.complete()
      }
    }
    queueSource
  }

  def peekMatValue[T, M](src: Source[T, M]): (Source[T, M], Future[M]) = {
    val p = Promise[M]
    val s = src.mapMaterializedValue { m =>
      p.trySuccess(m)
      m
    }
    (s, p.future)
  }
}
