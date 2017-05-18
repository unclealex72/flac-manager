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
import common.commands.CommandExecution
import common.configuration.{TestDirectories, User, UserDao}
import common.files.{FlacFileLocation, StagedFlacFileLocation}
import common.message.{MessageService, NoOpMessageService}
import controllers.CommandBuilderImpl
import initialise.InitialiseCommand
import json.RepositoryType.{FlacRepositoryType, StagingRepositoryType}
import json._
import org.specs2.mutable.Specification
import own.OwnAction._
import own.{OwnAction, OwnCommand}
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
            "allowUnowned" -> JsBoolean(false),
            "relativeDirectories" -> JsArray()))
      }
      validatedResult must beLeft { (es: NonEmptyList[String]) =>
        es.toList must contain(exactly("You must supply at least 1 staging directory."))
      }
    }
    "not allow flac directories" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkin"),
            "allowUnowned" -> JsBoolean(false),
            "relativeDirectories" -> JsArray(Seq(flacObj("A"), stagingObj("B")))))
      }
      validatedResult must beLeft { (es: NonEmptyList[String]) =>
        es.toList must contain(exactly("A is not a staging directory."))
      }
    }
    "pass on its parameters" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkin"),
            "allowUnowned" -> JsBoolean(true),
            "relativeDirectories" -> JsArray(Seq(stagingObj("A"), stagingObj("B")))))
      }
      validatedResult must beRight { (p: Parameters) =>
        p must be_==(CheckinParameters(List(staging("A"), staging("B")), allowUnowned = true))
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
            "relativeDirectories" -> JsArray()))
      }
      validatedResult must beLeft { (es: NonEmptyList[String]) =>
        es.toList must contain(exactly("You must supply at least 1 flac directory."))
      }
    }
    "not allow staging directories" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkout"),
            "unown" -> JsBoolean(true),
            "relativeDirectories" -> JsArray(Seq(flacObj("A"), stagingObj("B")))))
      }
      validatedResult must beLeft { (es: NonEmptyList[String]) =>
        es.toList must contain(exactly("B is not a flac directory."))
      }
    }
    "pass on its parameters" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkout"),
            "unown" -> JsBoolean(true),
            "relativeDirectories" -> JsArray(Seq(flacObj("A"), flacObj("B")))))
      }
      validatedResult must beRight { (p: Parameters) =>
        p must be_==(CheckoutParameters(List(flac("A"), flac("B")), unown = true))
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
            "relativeDirectories" -> JsArray()))
      }
      validatedResult must beLeft { (es: NonEmptyList[String]) =>
        es.toList must contain(exactly("You must supply at least 1 staging or flac directory.", "You must supply at least one user."))
      }
    }
    "require valid users" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("own"),
            "users" -> JsArray(Seq(JsString("Roger"))),
            "relativeDirectories" -> JsArray(Seq(flacObj("A")))))
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
            "relativeDirectories" -> JsArray(Seq(flacObj("A"), stagingObj("B")))))
      }
      validatedResult must beRight { (p: Parameters) =>
        p must be_==(OwnParameters(List(flac("A"), staging("B")), List("Brian")))
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
            "relativeDirectories" -> JsArray()))
      }
      validatedResult must beLeft { (es: NonEmptyList[String]) =>
        es.toList must contain(exactly("You must supply at least 1 staging or flac directory.", "You must supply at least one user."))
      }
    }
    "require valid users" in {
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("unown"),
            "users" -> JsArray(Seq(JsString("Roger"))),
            "relativeDirectories" -> JsArray(Seq(flacObj("A")))))
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
            "relativeDirectories" -> JsArray(Seq(flacObj("A"), stagingObj("B")))))
      }
      validatedResult must beRight { (p: Parameters) =>
        p must be_==(UnownParameters(List(flac("A"), staging("B")), List("Brian")))
      }
    }
  }

  def execution(jsValue: JsValue): Either[NonEmptyList[String], Parameters] = {
    val promise = Promise[Parameters]
    def commandType(parameters: Parameters): CommandExecution = CommandExecution.asynchronous {
      promise.success(parameters)
    }

    val checkinCommand: CheckinCommand = new CheckinCommand {
      override def checkin(locations: Seq[StagedFlacFileLocation], allowUnowned: Boolean)(implicit messageService: MessageService): CommandExecution = {
        commandType(CheckinParameters(locations.map(l => PathAndRepository(l.relativePath, StagingRepositoryType)), allowUnowned))
      }
    }
    val checkoutCommand: CheckoutCommand = new CheckoutCommand {
      override def checkout(locations: Seq[FlacFileLocation], unown: Boolean)
                           (implicit messageService: MessageService): CommandExecution = {
        commandType(CheckoutParameters(locations.map(l => PathAndRepository(l.relativePath, FlacRepositoryType)), unown))
      }
    }
    val initialiseCommand: InitialiseCommand = new InitialiseCommand {
      override def initialiseDb(implicit messageService: MessageService): CommandExecution = {
        commandType(InitialiseParameters())
      }
    }
    val ownCommand: OwnCommand = new OwnCommand {
      def changeOwnership(action: OwnAction, users: Seq[User], locations: Seq[Either[StagedFlacFileLocation, FlacFileLocation]])
                         (implicit messageService: MessageService): CommandExecution = {
        commandType {
          val pathAndRepositories = locations.map {
            case Left(sffl) => PathAndRepository(sffl.relativePath, StagingRepositoryType)
            case Right(ffl) => PathAndRepository(ffl.relativePath, FlacRepositoryType)
          }
          if (action == Own) {
            OwnParameters(pathAndRepositories, users.map(_.name))
          }
          else {
            UnownParameters(pathAndRepositories, users.map(_.name))
          }
        }
      }
    }

    implicit val directories = TestDirectories(datum = "/music/.datum")
    val userDao = new UserDao {
      override def allUsers(): Set[User] = Set("Freddie", "Brian").map(User(_))
    }
    val commandBuilder = new CommandBuilderImpl(
      checkinCommand, checkoutCommand, ownCommand, initialiseCommand, userDao)
    commandBuilder(jsValue).map { builder =>
      builder(NoOpMessageService).execute()
      Await.result(promise.future, Duration(1, TimeUnit.SECONDS))
    }.toEither
  }

  def staging(path: String): PathAndRepository = PathAndRepository(Paths.get(path), StagingRepositoryType)
  def flac(path: String): PathAndRepository = PathAndRepository(Paths.get(path), FlacRepositoryType)

  def stagingObj: (String) => JsObject = pathObj(StagingRepositoryType)
  def flacObj: (String) => JsObject = pathObj(FlacRepositoryType)
  def pathObj(repositoryType: RepositoryType)(path: String): JsObject = {
    JsObject(Seq("path" -> JsString(path), "repositoryType" -> JsString(repositoryType.identifier)))
  }
}
