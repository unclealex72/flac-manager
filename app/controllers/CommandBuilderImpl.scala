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
import json.RepositoryType.{FlacRepositoryType, StagingRepositoryType}
import json._
import multidisc.MultiDiscCommand
import own.OwnAction._
import own.OwnCommand
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
                                    multiDiscCommand: MultiDiscCommand,
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
    * @param pathAndRepositories A list of relative paths to check.
    * @return A list of [[StagedFlacFileLocation]]s or a list of errors.
    */
  def validateStagingDirectories(pathAndRepositories: Seq[PathAndRepository]): ValidatedNel[String, Seq[StagedFlacFileLocation]] =
    validateDirectories("staging", pathAndRepositories) { pathAndRepository =>
      pathAndRepository.repositoryType match {
        case StagingRepositoryType => Validated.valid(StagedFlacFileLocation(pathAndRepository.path))
        case _ => Validated.invalid(s"${pathAndRepository.path} is not a staging directory.")
      }
    }

  /**
    * Require a multi action to be provided.
    * @param maybeMultiAction The multi action that may have been provided.
    * @return The provided multi action or an error.
    */
  def requireMultiAction(maybeMultiAction: Option[MultiAction]): ValidatedNel[String, MultiAction] = {
    maybeMultiAction.toValidNel("You must include an action to deal with multiple disc albums.")
  }

  /**
    * Make sure that all flac directories are valid.
    * @param pathAndRepositories A list of relative paths to check.
    * @return A list of [[FlacFileLocation]]s or a list of errors.
    */
  def validateFlacDirectories(pathAndRepositories: Seq[PathAndRepository]): ValidatedNel[String, Seq[FlacFileLocation]] =
    validateDirectories("flac", pathAndRepositories) { pathAndRepository =>
      pathAndRepository.repositoryType match {
        case FlacRepositoryType => Validated.valid(FlacFileLocation(pathAndRepository.path))
        case _ => Validated.invalid(s"${pathAndRepository.path} is not a flac directory.")
      }
    }

  /**
    * Make sure that all flac or staging directories are valid.
    * @param pathAndRepositories A list of relative paths to check.
    * @return A list of [[FlacFileLocation]]s and [[StagedFlacFileLocation]]s or a list of errors.
    */
  def validateStagingOrFlacDirectories(pathAndRepositories: Seq[PathAndRepository]): ValidatedNel[String, Seq[Either[StagedFlacFileLocation, FlacFileLocation]]] =
    validateDirectories("staging or flac", pathAndRepositories) { pathAndRepository =>
      pathAndRepository.repositoryType match {
        case FlacRepositoryType => Validated.valid(Right(FlacFileLocation(pathAndRepository.path)))
        case StagingRepositoryType => Validated.valid(Left(StagedFlacFileLocation(pathAndRepository.path)))
      }
    }

  /**
    *
    * Make sure that all staging directories are valid. At this point all that is checked is that
    * the supplied list is not empty and each directory is of a specified type.
    * @param repositoryType The directory type name that will be reported in errors.
    * @param builder A function that converts a path containing object into a [[FileLocation]]
    * @param pathAndRepositories A list of relative paths to check.
    * @tparam R The result type.
    * @return A list of results or a list of errors.
    */
  def validateDirectories[R](repositoryType: String, pathAndRepositories: Seq[PathAndRepository])
                               (builder: PathAndRepository => Validated[String, R]): ValidatedNel[String, Seq[R]] = {
    val empty: ValidatedNel[String, Seq[R]] = if (pathAndRepositories.isEmpty) {
      Validated.invalidNel(s"You must supply at least 1 $repositoryType directory.")
    }
    else {
      Validated.valid(Seq.empty)
    }
    pathAndRepositories.foldLeft(empty) { (validatedLocations, pathAndRepository) =>
      val validatedLocation = builder(pathAndRepository)
      (validatedLocations |@| validatedLocation.toValidatedNel).map(_ :+ _)
    }
  }

  /**
    * @inheritdoc
    */
  override def apply(jsValue: JsValue): ValidatedNel[String, (MessageService) => CommandExecution] = {
    val validatedParameters = jsonToParameters(jsValue)
    validatedParameters.andThen {
      case CheckinParameters(relativeDirectories, allowUnowned) =>
        validateStagingDirectories(relativeDirectories).map { fls =>
          (messageService: MessageService) => checkinCommand.checkin(fls, allowUnowned)(messageService) }
      case CheckoutParameters(relativeDirectories, unown) =>
        validateFlacDirectories(relativeDirectories).map { fls =>
          (messageService: MessageService) => checkoutCommand.checkout(fls, unown)(messageService)
        }
      case OwnParameters(relativeStagingOrFlacDirectories, usernames) =>
        (validateStagingOrFlacDirectories(relativeStagingOrFlacDirectories) |@| validateUsers(usernames)).map { (fls, us) =>
          (messageService: MessageService) => ownCommand.changeOwnership(Own, us, fls)(messageService)
        }
      case UnownParameters(relativeStagingOrFlacDirectories, usernames) =>
        (validateStagingOrFlacDirectories(relativeStagingOrFlacDirectories) |@| validateUsers(usernames)).map { (fls, us) =>
          (messageService: MessageService) => ownCommand.changeOwnership(Unown, us, fls)(messageService)
        }
      case InitialiseParameters() => Valid {
        (messageService: MessageService) => initialiseCommand.initialiseDb(messageService)
      }
      case MultiDiscParameters(relativeDirectories, maybeMultiAction) =>
        (validateStagingDirectories(relativeDirectories) |@| requireMultiAction(maybeMultiAction)).map { (fls, multiAction) =>
          (messageService: MessageService) => multiDiscCommand.mutateMultiDiscAlbum(fls, multiAction)(messageService)
        }
    }
  }
}
