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

package controllers

import java.nio.file.Path
import javax.inject.Inject

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits._
import checkin.CheckinCommand
import checkout.CheckoutCommand
import common.commands.CommandExecution
import common.configuration.{Directories, User, UserDao}
import common.files.{FileLocation, FlacFileLocation, StagedFlacFileLocation}
import common.message.MessageService
import initialise.InitialiseCommand
import io.circe.Json
import json._
import own.{Own, OwnCommand, Unown}
import play.api.libs.json._

/**
  * A class used to create a command to run from a Json object.
  * Created by alex on 23/04/17
  **/
class CommandBuilderImpl @Inject()(
                                    checkinCommand: CheckinCommand,
                                    checkoutCommand: CheckoutCommand,
                                    ownCommand: OwnCommand,
                                    initialiseCommand: InitialiseCommand,
                                    userDao: UserDao
                                  )(implicit val directories: Directories) extends CommandBuilder {

  def jsonToParameters(jsValue: JsValue): ValidatedNel[String, Parameters] = {
    def jsValueToJson: JsValue => Json = {
      case JsArray(values) => Json.arr(values.map(jsValueToJson):_*)
      case JsNull => Json.Null
      case JsObject(map) => Json.obj(map.mapValues(jsValueToJson).toSeq:_*)
      case JsNumber(num) => Json.fromBigDecimal(num)
      case JsString(str) => Json.fromString(str)
      case JsBoolean(b) => Json.fromBoolean(b)
    }
    Parameters.parametersDecoder.decodeJson(jsValueToJson(jsValue)) match {
      case Right(parameters) => Valid(parameters)
      case Left(error) => Validated.invalidNel(error.getMessage())
    }
  }

  def validateUsers(usernames: Seq[String]): ValidatedNel[String, Seq[User]] = {
    val empty: ValidatedNel[String, Seq[User]] = Valid(Seq.empty)
    val allUsers = userDao.allUsers()
    if (usernames.isEmpty) {
      Validated.invalidNel("You must supply at least one user.")
    }
    else {
      usernames.foldLeft(empty) { (validatedUsers, username) =>
        val validatedUser = allUsers.find(_.name == username) match {
          case Some(user) => Valid(user)
          case None => Invalid(NonEmptyList.of(s"$username is not a valid user."))
        }
        (validatedUsers |@| validatedUser).map(_ :+ _)
      }
    }
  }

  def validateStagingDirectories(relativePaths: Seq[Path]): ValidatedNel[String, Seq[StagedFlacFileLocation]] =
    validateDirectories("staging", StagedFlacFileLocation(_), relativePaths)

  def validateFlacDirectories(relativePaths: Seq[Path]): ValidatedNel[String, Seq[FlacFileLocation]] =
    validateDirectories("flac", FlacFileLocation(_), relativePaths)

  def validateDirectories[F <: FileLocation](directoryType: String, builder: Path => F, relativePaths: Seq[Path]): ValidatedNel[String, Seq[F]] = {
    val empty: Seq[F] = Seq.empty
    val locations = relativePaths.foldLeft(empty) { (locations, relativePath) =>
      locations :+ builder(relativePath)
    }
    if (locations.isEmpty) {
      Validated.invalidNel(s"You must supply at least 1 $directoryType directory.")
    }
    else {
      Valid(locations)
    }
  }

  override def apply(jsValue: JsValue): ValidatedNel[String, (MessageService) => CommandExecution] = {
    val validatedParameters = jsonToParameters(jsValue)
    validatedParameters.andThen {
      case CheckinParameters(relativeStagingDirectories) =>
        validateStagingDirectories(relativeStagingDirectories).map { fls =>
          (messageService: MessageService) => checkinCommand.checkin(fls)(messageService) }
      case CheckoutParameters(relativeFlacDirectories, unown) =>
        validateFlacDirectories(relativeFlacDirectories).map { fls =>
          (messageService: MessageService) => checkoutCommand.checkout(fls, unown)(messageService)
        }
      case OwnParameters(relativeStagingDirectories, usernames) =>
        (validateStagingDirectories(relativeStagingDirectories) |@| validateUsers(usernames)).map { (fls, us) =>
          (messageService: MessageService) => ownCommand.changeOwnership(Own, us, fls)(messageService)
        }
      case UnownParameters(relativeStagingDirectories, usernames) =>
        (validateStagingDirectories(relativeStagingDirectories) |@| validateUsers(usernames)).map { (fls, us) =>
          (messageService: MessageService) => ownCommand.changeOwnership(Unown, us, fls)(messageService)
        }
      case InitialiseParameters() => Valid {
        (messageService: MessageService) => initialiseCommand.initialiseDb(messageService)
      }
    }
  }
}
