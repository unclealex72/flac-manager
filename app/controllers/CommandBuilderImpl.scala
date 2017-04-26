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
  * The default implementation of [[CommandBuilder]].
  * @param checkinCommand The [[CheckinCommand]] for checking in flac files.
  * @param checkoutCommand The [[CheckoutCommand]] for checking out flac files.
  * @param ownCommand The [[OwnCommand]] for changing the ownership of albums.
  * @param initialiseCommand The [[InitialiseCommand]] used to initialise the database.
  * @param userDao The [[UserDao]] used to find the known users.
  * @param directories The [[Directories]] used to locate the repositories.
  */
class CommandBuilderImpl @Inject()(
                                    checkinCommand: CheckinCommand,
                                    checkoutCommand: CheckoutCommand,
                                    ownCommand: OwnCommand,
                                    initialiseCommand: InitialiseCommand,
                                    userDao: UserDao
                                  )(implicit val directories: Directories) extends CommandBuilder {


  /**
    * Convert a play [[JsValue]] into a [[Parameters]] object
    * @param jsValue The [[JsValue]] to parse.
    * @return A [[Parameters]] object or a list of errors if the JSON object could not be parsed.
    */
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

  /**
    * Make sure that all usernames are valid and at least one username was supplied.
    * @param usernames A list of usernames to check.
    * @return The users with the usernames or a list of errors.
    */
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

  /**
    * Make sure that all staging directories are valid.
    * @param relativePaths A list of relative paths to check.
    * @return A list of [[StagedFlacFileLocation]]s or a list of errors.
    */
  def validateStagingDirectories(relativePaths: Seq[Path]): ValidatedNel[String, Seq[StagedFlacFileLocation]] =
    validateDirectories("staging", StagedFlacFileLocation(_), relativePaths)

  /**
    * Make sure that all flac directories are valid.
    * @param relativePaths A list of relative paths to check.
    * @return A list of [[FlacFileLocation]]s or a list of errors.
    */
  def validateFlacDirectories(relativePaths: Seq[Path]): ValidatedNel[String, Seq[FlacFileLocation]] =
    validateDirectories("flac", FlacFileLocation(_), relativePaths)

  /**
    * Make sure that all staging directories are valid. At this point all that is checked is that
    * the supplied list is not empty.
    * @param directoryType The directory type name that will be reported in errors.
    * @param builder A function that converts a path into a [[FileLocation]]
    * @param relativePaths A list of relative paths to check.
    * @tparam F A type of [[FileLocation]]
    * @return A list of [[FileLocation]]s or a list of errors.
    */
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

  /**
    * @inheritdoc
    */
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
