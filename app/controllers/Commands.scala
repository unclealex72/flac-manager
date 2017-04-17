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

import checkin.CheckinCommand
import checkout.CheckoutCommand
import common.commands.CommandType
import common.commands.CommandType._
import common.message.MessageTypes._
import common.message.{MessageService, MessageServiceBuilder, Messaging}
import initialise.InitialiseCommand
import own.{Own, OwnCommand, Unown}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.{Concurrent, Enumerator}
import play.api.mvc.{Action, Controller}

import scala.util.Try


/**
 * Created by alex on 06/11/14.
 */
@Singleton
class Commands @Inject()(
                messageServiceBuilder: MessageServiceBuilder,
                parameterBuilders: ParameterBuilders,
                checkinCommand: CheckinCommand,
                checkoutCommand: CheckoutCommand,
                ownCommand: OwnCommand,
                initialiseCommand: InitialiseCommand
                ) extends Controller with Messaging {

  def initialise = command[Parameters](
    "initialise", parameterBuilders.initialiseParametersBuilder, p => ms => initialiseCommand.initialiseDb(ms))

  def own = command[OwnerParameters](
    "own", parameterBuilders.ownerParametersBuilder, p => ms => ownCommand.changeOwnership(Own, p.owners, p.stagedFileLocations)(ms))

  def unown = command[OwnerParameters](
    "unown", parameterBuilders.ownerParametersBuilder, p => ms => ownCommand.changeOwnership(Unown, p.owners, p.stagedFileLocations)(ms))

  def checkin = command[CheckinParameters](
    "checkin", parameterBuilders.checkinParametersBuilder, p => ms => checkinCommand.checkin(p.stagedFileLocations)(ms))

  def checkout = command[CheckoutParameters](
    "checkout", parameterBuilders.checkoutParametersBuilder, p => ms => checkoutCommand.checkout(p.fileLocations, p.unown)(ms))

  /**
   * Call with:
   *
   * curl --data-urlencode owners[0]=Alex
   * --data-urlencode datum=/mnt/.datum
   * --data-urlencode directories[0]=/mnt/flac
   * --data-urlencode directories[1]=/mnt/encoded
   * localhost:9000/command
   * @return
   */
  def command[P <: Parameters](loggerName: String, parameterBuilder: ParameterBuilder[P], cmd: P => MessageService => CommandType) = Action { implicit request =>
    val enumerator: (MessageService => CommandType) => Enumerator[String] = cmd => Concurrent.unicast[String](onStart = channel => {
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
        commandType.execute
        if (commandType.requiresFinish) {
          messageService.finish
        }
      }.recover {
        case e => {
          messageService.exception(e)
          messageService.finish
        }
      }
    })
    parameterBuilder.bindFromRequest match {
      case Right(parameters) => Ok.chunked(enumerator { messageService =>
        cmd(parameters)(messageService)
      })
      case Left(formErrors) => BadRequest.chunked(enumerator { implicit messageService =>
        synchronous { formErrors.foreach { formError =>
          log(INVALID_PARAMETERS(formError.key, formError.message, formError.args))
        }}
      })
    }
  }
}
