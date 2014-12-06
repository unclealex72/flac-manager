/*
 * Copyright 2014 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package controllers

import java.io.{PrintWriter, StringWriter}

import checkin.CheckinCommand
import checkout.CheckoutCommand
import common.message.MessageTypes._
import common.message.{MessageService, MessageServiceBuilder, Messaging}
import initialise.InitialiseCommand
import own.{Own, OwnCommand, Unown}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.{Concurrent, Enumerator}
import play.api.mvc.{Action, Controller}
import sync.SyncCommand

import scala.util.Try

/**
 * Created by alex on 06/11/14.
 */
class Commands(
                messageServiceBuilder: MessageServiceBuilder,
                parameterBuilders: ParameterBuilders,
                syncCommand: SyncCommand,
                checkinCommand: CheckinCommand,
                checkoutCommand: CheckoutCommand,
                ownCommand: OwnCommand,
                initialiseCommand: InitialiseCommand
                ) extends Controller with Messaging {
  def sync = command[Parameters](
    "sync", parameterBuilders.syncParametersBuilder, p => ms => syncCommand.synchronise(ms))

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
   * --data-urlencode mtab@/etc/mtab
   * --data-urlencode directories[0]=/mnt/flac
   * --data-urlencode directories[1]=/mnt/encoded
   * localhost:9000/command
   * @return
   */
  def command[P <: Parameters](loggerName: String, parameterBuilder: ParameterBuilder[P], cmd: P => MessageService => Unit) = Action { implicit request =>
    val enumerator: (MessageService => Unit) => Enumerator[String] = cmd => Concurrent.unicast[String](onStart = channel => {
      val messageService = messageServiceBuilder.
        withPrinter(message => channel.push(message + "\n")).
        withExceptionHandler { t =>
          val writer = new StringWriter
          t.printStackTrace(new PrintWriter(writer))
          channel.push(writer.toString + "\n")
        }.
        build
      Try {
        cmd(messageService)
      }.recover {
        case e => messageService.exception(e)
      }
      channel.eofAndEnd
    })
    parameterBuilder.bindFromRequest match {
      case Right(parameters) => Ok.chunked(enumerator { messageService =>
        cmd(parameters)(messageService)
      })
      case Left(formErrors) => BadRequest.chunked(enumerator { implicit messageService =>
        formErrors.foreach { formError =>
          log(ERROR(formError.key, formError.message, formError.args))
        }
      })
    }
  }
}
