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
import java.nio.file.Path
import javax.inject.{Inject, Singleton}

import akka.stream.scaladsl.Source
import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits._
import cats.syntax.either._
import checkin.CheckinCommand
import checkout.CheckoutCommand
import common.commands.CommandExecution
import common.commands.CommandExecution._
import common.configuration.{Directories, User, UserDao}
import common.files.{FileLocation, FlacFileLocation, StagedFlacFileLocation}
import common.message.Messages._
import common.message.{MessageService, MessageServiceBuilder, Messaging}
import initialise.InitialiseCommand
import io.circe.Json
import json._
import own.{Own, OwnCommand, Unown}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.{Concurrent, Enumerator}
import play.api.libs.json._
import play.api.libs.streams.Streams
import play.api.mvc._

import scala.util.Try

/**
 * Created by alex on 06/11/14.
 */
@Singleton
class Commands @Inject()(
                          messageServiceBuilder: MessageServiceBuilder,
                          commandBuilder: CommandBuilder) extends Controller with Messaging {


  def commands: Action[JsValue] = Action(parse.json) { implicit request =>
    val validatedCommandTypeBuilder = commandBuilder(request.body)
    val enumerator: (MessageService => CommandExecution) => Enumerator[String] = cmd => Concurrent.unicast[String](onStart = channel => {
      val messageService = messageServiceBuilder.
        withPrinter(message => channel.push(message + "\n")).
        withExceptionHandler { t =>
          val writer = new StringWriter
          t.printStackTrace(new PrintWriter(writer))
          channel.push(writer.toString + "\n")
        }.
        withOnFinish {
          channel.eofAndEnd
        }.
        build
      Try {
        val commandType = cmd(messageService)
        commandType.execute()
        if (commandType.requiresFinish) {
          messageService.finish()
        }
      }.recover {
        case e =>
          messageService.exception(e)
          messageService.finish()
      }
    })
    validatedCommandTypeBuilder match {
      case Valid(commandTypeBuilder) => Ok.chunked(Source.fromPublisher(Streams.enumeratorToPublisher(enumerator(commandTypeBuilder))))
      case Invalid(errors) => BadRequest.chunked(Source.fromPublisher(Streams.enumeratorToPublisher(enumerator { implicit messageService =>
        synchronous {
          errors.toList.foreach { error =>
            log(INVALID_PARAMETERS(error))
          }
        }
      })))
    }
  }
}
