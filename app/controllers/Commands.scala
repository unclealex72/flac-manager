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
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import javax.inject.{Inject, Singleton}

import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Source, SourceQueue}
import cats.data.Validated.{Invalid, Valid}
import com.typesafe.scalalogging.StrictLogging
import common.async.CommandExecutionContext
import common.configuration.HttpStreams
import common.message.{MessageService, MessageServiceBuilder, Messaging}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
  * The controller that executes commands that alter repositories.
  * @param messageServiceBuilder The [[MessageServiceBuilder]] used to build a feedback mechanism.
  * @param commandBuilder A [[CommandBuilder]] used to build a command from a JSON RPC payload.
  * @param ec The execution context used to execute the command.
  */
@Singleton
class Commands @Inject()(
                          val messageServiceBuilder: MessageServiceBuilder,
                          val commandBuilder: CommandBuilder,
                          val controllerComponents: ControllerComponents)
                        (implicit commandExecutionContext: CommandExecutionContext) extends BaseController with Messaging with StrictLogging {


  /**
    * Take a JSON RPC payload an execute a command. The feedback from the command will be sent back to the
    * client using an HTTP chunked response so that feedback is received and processed as soon as possible.
    * @return A HTTP chunked response containing the feedback from the command.
    */
  def commands: Action[JsValue] = Action(parse.json) { implicit request: Request[JsValue] =>
    val (queueSource, eventualQueue) = peekMatValue(Source.queue[String](128, OverflowStrategy.backpressure))
    eventualQueue.map { queue =>
      def offer(message: Any) = queue.offer(s"$message\n")
      def keepAlive() = queue.offer(HttpStreams.KEEP_ALIVE)
      def printer(message: String): Unit = offer(message)
      def exceptionHandler(t: Throwable): Unit = {
        val sw = new StringWriter()
        t.printStackTrace(new PrintWriter(sw))
        offer(sw)
      }
      // Create a keep alive message for slower machines that may take a while to encode files.
      val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
      val scheduledFuture = scheduler.scheduleAtFixedRate(() => keepAlive(), 10, 10, TimeUnit.SECONDS)
      def complete(): Unit = {
        scheduledFuture.cancel(true)
        scheduler.shutdownNow()
        queue.complete()
      }
      implicit val messageService =
        messageServiceBuilder.
          withPrinter(printer).
          withExceptionHandler(exceptionHandler).build
      commandBuilder(request.body).andThen {
        case Success(Valid(_)) =>
          queue.offer("Success").map(_ => complete())
        case Success(Invalid(messages)) =>
          messages.foldLeft(Future.successful({})) { (acc, message) =>
            acc.map(_ => log(message))
          }.map(_ => complete())
        case Failure(e) =>
          messageService.exception(e)
          complete()
      }
    }
    Ok.chunked(queueSource).as(TEXT)
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
