/*
 * Copyright 2017 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.controllers

import java.nio.file.Paths
import java.util.concurrent.TimeUnit

import cats.data.NonEmptyList
import checkin.CheckinCommand
import checkout.CheckoutCommand
import common.commands.CommandType
import common.configuration.{TestDirectories, User, Users}
import common.files.{FlacFileLocation, StagedFlacFileLocation}
import common.message.{MessageService, NoOpMessageService}
import controllers.CommandBuilderImpl
import initialise.InitialiseCommand
import json._
import org.specs2.mutable.Specification
import own.{Own, OwnAction, OwnCommand}
import play.api.libs.json._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}
/**
  * Created by alex on 23/04/17
  **/
class CommandBuilderImplSpec extends Specification {

  "running the initialise command" should {
    "only require a command to call successfully" in {
      val validatedResult = execution {
        JsObject(Seq("command" -> JsString("initialise")))
      }
      validatedResult must beRight { (p: Parameters) =>
        p must be_==(InitialiseParameters())
      }
    }
  }

  "running the checkin command" should {
    "require directories" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkin"),
            "relativeStagingDirectories" -> JsArray()))
      }
      validatedResult must beLeft { (es: NonEmptyList[String]) =>
        es.toList must contain(exactly("You must supply at least 1 staging directory."))
      }
    }
    "pass on its parameters" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkin"),
            "relativeStagingDirectories" -> JsArray(Seq(JsString("A"), JsString("B")))))
      }
      validatedResult must beRight { (p: Parameters) =>
        p must be_==(CheckinParameters(List(Paths.get("A"), Paths.get("B"))))
      }
    }
  }

  "running the checkout command" should {
    "require directories" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkout"),
            "unown" -> JsBoolean(true),
            "relativeFlacDirectories" -> JsArray()))
      }
      validatedResult must beLeft { (es: NonEmptyList[String]) =>
        es.toList must contain(exactly("You must supply at least 1 flac directory."))
      }
    }
    "pass on its parameters" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkout"),
            "unown" -> JsBoolean(true),
            "relativeFlacDirectories" -> JsArray(Seq(JsString("A"), JsString("B")))))
      }
      validatedResult must beRight { (p: Parameters) =>
        p must be_==(CheckoutParameters(List(Paths.get("A"), Paths.get("B")), unown = true))
      }
    }
  }

  "running the own command" should {
    "require directories and users" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("own"),
            "users" -> JsArray(),
            "relativeStagingDirectories" -> JsArray()))
      }
      validatedResult must beLeft { (es: NonEmptyList[String]) =>
        es.toList must contain(exactly("You must supply at least 1 staging directory.", "You must supply at least one user."))
      }
    }
    "require valid users" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("own"),
            "users" -> JsArray(Seq(JsString("Roger"))),
            "relativeStagingDirectories" -> JsArray(Seq(JsString("A")))))
      }
      validatedResult must beLeft { (es: NonEmptyList[String]) =>
        es.toList must contain(exactly("Roger is not a valid user."))
      }
    }
    "pass on its parameters" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("own"),
            "users" -> JsArray(Seq(JsString("Brian"))),
            "relativeStagingDirectories" -> JsArray(Seq(JsString("A"), JsString("B")))))
      }
      validatedResult must beRight { (p: Parameters) =>
        p must be_==(OwnParameters(List(Paths.get("A"), Paths.get("B")), List("Brian")))
      }
    }
  }

  "running the unown command" should {
    "require directories and users" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("unown"),
            "users" -> JsArray(),
            "relativeStagingDirectories" -> JsArray()))
      }
      validatedResult must beLeft { (es: NonEmptyList[String]) =>
        es.toList must contain(exactly("You must supply at least 1 staging directory.", "You must supply at least one user."))
      }
    }
    "require valid users" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("unown"),
            "users" -> JsArray(Seq(JsString("Roger"))),
            "relativeStagingDirectories" -> JsArray(Seq(JsString("A")))))
      }
      validatedResult must beLeft { (es: NonEmptyList[String]) =>
        es.toList must contain(exactly("Roger is not a valid user."))
      }
    }
    "pass on its parameters" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("unown"),
            "users" -> JsArray(Seq(JsString("Brian"))),
            "relativeStagingDirectories" -> JsArray(Seq(JsString("A"), JsString("B")))))
      }
      validatedResult must beRight { (p: Parameters) =>
        p must be_==(UnownParameters(List(Paths.get("A"), Paths.get("B")), List("Brian")))
      }
    }
  }

  def execution(jsValue: JsValue): Either[NonEmptyList[String], Parameters] = {
    val promise = Promise[Parameters]
    def commandType(parameters: Parameters): CommandType = CommandType.asynchronous {
      promise.success(parameters)
    }

    val checkinCommand: CheckinCommand = new CheckinCommand {
      override def checkin(locations: Seq[StagedFlacFileLocation])(implicit messageService: MessageService): CommandType = {
        commandType(CheckinParameters(locations.map(_.relativePath)))
      }
    }
    val checkoutCommand: CheckoutCommand = new CheckoutCommand {
      override def checkout(locations: Seq[FlacFileLocation], unown: Boolean)
                           (implicit messageService: MessageService): CommandType = {
        commandType(CheckoutParameters(locations.map(_.relativePath), unown))
      }
    }
    val initialiseCommand: InitialiseCommand = new InitialiseCommand {
      override def initialiseDb(implicit messageService: MessageService): CommandType = {
        commandType(InitialiseParameters())
      }
    }
    val ownCommand: OwnCommand = new OwnCommand {
      override def changeOwnership(action: OwnAction, users: Seq[User], locations: Seq[StagedFlacFileLocation])
                                  (implicit messageService: MessageService): CommandType = {
        commandType {
          if (action == Own) {
            OwnParameters(locations.map(_.relativePath), users.map(_.name))
          }
          else {
            UnownParameters(locations.map(_.relativePath), users.map(_.name))
          }
        }
      }
    }

    implicit val directories = TestDirectories(datum = "/music/.datum")
    val users = new Users {
      override def allUsers: Set[User] = Set("Freddie", "Brian").map(User)
    }
    val commandBuilder = new CommandBuilderImpl(
      checkinCommand, checkoutCommand, ownCommand, initialiseCommand, users)
    commandBuilder(jsValue).map { builder =>
      builder(NoOpMessageService).execute()
      Await.result(promise.future, Duration(1, TimeUnit.SECONDS))
    }.toEither
  }
}
