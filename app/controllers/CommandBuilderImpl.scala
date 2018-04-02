/*
 * Copyright 2018 Alex Jones
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

import calibrate.CalibrateCommand
import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits._
import checkin.CheckinCommand
import checkout.CheckoutCommand
import com.typesafe.scalalogging.StrictLogging
import common.configuration.{User, UserDao}
import common.files.Directory._
import common.files._
import common.message.Messages._
import common.message.{Message, MessageService}
import initialise.InitialiseCommand
import io.circe.Json
import javax.inject.Inject
import json.RepositoryType.{FlacRepositoryType, StagingRepositoryType}
import json._
import multidisc.MultiDiscCommand
import own.OwnAction._
import own.OwnCommand
import play.api.libs.json._

import scala.collection.SortedSet
import scala.concurrent.Future

/**
  * The default implementation of [[CommandBuilder]].
  * @param checkinCommand The [[CheckinCommand]] for checking in flac files.
  * @param checkoutCommand The [[CheckoutCommand]] for checking out flac files.
  * @param ownCommand The [[OwnCommand]] for changing the ownership of albums.
  * @param initialiseCommand The [[InitialiseCommand]] used to initialise the database.
  * @param userDao The [[UserDao]] used to find the known users.
  */
class CommandBuilderImpl @Inject()(
                                    checkinCommand: CheckinCommand,
                                    checkoutCommand: CheckoutCommand,
                                    ownCommand: OwnCommand,
                                    initialiseCommand: InitialiseCommand,
                                    multiDiscCommand: MultiDiscCommand,
                                    calibrateCommand: CalibrateCommand,
                                    userDao: UserDao,
                                    repositories: Repositories
                                  )(implicit val fs: java.nio.file.FileSystem) extends CommandBuilder with StrictLogging {


  /**
    * Convert a play [[JsValue]] into a [[Parameters]] object
    * @param jsValue The [[JsValue]] to parse.
    * @return A [[Parameters]] object or a list of errors if the JSON object could not be parsed.
    */
  def jsonToParameters(jsValue: JsValue): ValidatedNel[Message, Parameters] = {
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
      case Left(error) => Validated.invalidNel(JSON_ERROR(error))
    }
  }

  /**
    * Make sure that all usernames are valid and at least one username was supplied.
    * @param usernames A list of usernames to check.
    * @return The users with the usernames or a list of errors.
    */
  def validateUsers(usernames: Seq[String]): ValidatedNel[Message, SortedSet[User]] = {
    val empty: ValidatedNel[Message, SortedSet[User]] = Valid(SortedSet.empty)
    val allUsers: Set[User] = userDao.allUsers()
    if (usernames.isEmpty) {
      Validated.invalidNel(NO_USERS)
    }
    else {
      usernames.foldLeft(empty) { (validatedUsers, username) =>
        val validatedUser: Validated[NonEmptyList[INVALID_USER], User] = allUsers.find(_.name == username) match {
          case Some(user) => Valid(user)
          case None => Invalid(INVALID_USER(username)).toValidatedNel
        }
        (validatedUsers |@| validatedUser).map(_ + _)
      }
    }
  }

  def validateMaximumNumberOfThreads(maybeMaximumNumberOfThreads: Option[Int]): ValidatedNel[Message, Option[Int]] = {
    maybeMaximumNumberOfThreads match {
      case Some(maximumNumberOfThreads) =>
        if (maximumNumberOfThreads < 1) {
          Invalid(NOT_ENOUGH_THREADS).toValidatedNel
        }
        else {
          Valid(maybeMaximumNumberOfThreads)
        }
      case None => Valid(None)
    }
  }
  /**
    * Make sure that all staging directories are valid.
    * @param pathAndRepositories A list of relative paths to check.
    * @return A list of [[StagingDirectory]]s or a list of errors.
    */
  def validateStagingDirectories(pathAndRepositories: Seq[PathAndRepository])(implicit messageService: MessageService): ValidatedNel[Message, SortedSet[StagingDirectory]] =
    validateDirectories("staging", pathAndRepositories) {
      case StagingRepositoryType => repositories.staging.directory
    }

  /**
    * Require a multi action to be provided.
    * @param maybeMultiAction The multi action that may have been provided.
    * @return The provided multi action or an error.
    */
  def requireMultiAction(maybeMultiAction: Option[MultiAction]): ValidatedNel[Message, MultiAction] = {
    maybeMultiAction.toValidNel(MULTI_ACTION_REQUIRED)
  }

  /**
    * Make sure that all flac directories are valid.
    * @param pathAndRepositories A list of relative paths to check.
    * @return A list of [[FlacDirectory]]s or a list of errors.
    */
  def validateFlacDirectories(pathAndRepositories: Seq[PathAndRepository])(implicit messageService: MessageService): ValidatedNel[Message, SortedSet[FlacDirectory]] =
    validateDirectories("flac", pathAndRepositories) {
      case FlacRepositoryType => repositories.flac.directory
    }

  /**
    * Make sure that all flac or staging directories are valid.
    * @param pathAndRepositories A list of relative paths to check.
    * @return A list of [[FlacDirectory]]s and [[StagingDirectory]]s or a list of errors.
    */
  def validateStagingOrFlacDirectories(pathAndRepositories: Seq[PathAndRepository])(implicit messageService: MessageService): ValidatedNel[Message, SortedSet[Either[StagingDirectory, FlacDirectory]]] =
    validateDirectories("staging or flac", pathAndRepositories) {
      case FlacRepositoryType => path => repositories.flac.directory(path).map(Right(_))
      case StagingRepositoryType => path => repositories.staging.directory(path).map(Left(_))
    }

  /**
    *
    * Make sure that all staging directories are valid. At this point all that is checked is that
    * the supplied list is not empty and each directory is of a specified type.
    * @param repositoryType The directory type name that will be reported in errors.
    * @param builder A function that converts a path containing object into an [[F]]
    * @param pathAndRepositories A list of relative paths to check.
    * @tparam F The result type.
    * @return A list of results or a list of errors.
    */
  def validateDirectories[F](repositoryType: String, pathAndRepositories: Seq[PathAndRepository])
                               (builder: PartialFunction[RepositoryType, Path => ValidatedNel[Message, F]])(implicit ord: Ordering[F]): ValidatedNel[Message, SortedSet[F]] = {
    val empty: ValidatedNel[Message, SortedSet[F]] = if (pathAndRepositories.isEmpty) {
      Validated.invalidNel(NO_DIRECTORIES(repositoryType))
    }
    else {
      Validated.valid(SortedSet.empty[F])
    }
    pathAndRepositories.foldLeft(empty) { (validatedLocations, pathAndRepository) =>
      val validatedLocationFunction: Path => ValidatedNel[Message, F] =
        builder.lift(pathAndRepository.repositoryType).
          getOrElse((path: Path) => Validated.invalidNel(NOT_A_DIRECTORY(path, repositoryType)))
      val validatedLocation = validatedLocationFunction(pathAndRepository.path)
      (validatedLocations |@| validatedLocation).map(_ + _)
    }
  }

  /**
    * @inheritdoc
    */
  override def apply(jsValue: JsValue)(implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]] = {
    logger.info(s"Received body $jsValue")
    val validatedParameters: ValidatedNel[Message, Parameters] = jsonToParameters(jsValue)
    def execute(parameters: Parameters): ValidatedNel[Message, Future[ValidatedNel[Message, Unit]]] = parameters match {
      case CheckinParameters(relativeDirectories, allowUnowned) =>
        validateStagingDirectories(relativeDirectories).map { fls =>
          checkinCommand.checkin(fls, allowUnowned)
        }
      case CheckoutParameters(relativeDirectories, unown) =>
        validateFlacDirectories(relativeDirectories).map { fls =>
          checkoutCommand.checkout(fls, unown)
        }
      case OwnParameters(relativeStagingOrFlacDirectories, usernames) =>
        (validateStagingOrFlacDirectories(relativeStagingOrFlacDirectories) |@| validateUsers(usernames)).map { (fls, us) =>
          ownCommand.changeOwnership(Own, us, fls)
        }
      case UnownParameters(relativeStagingOrFlacDirectories, usernames) =>
        (validateStagingOrFlacDirectories(relativeStagingOrFlacDirectories) |@| validateUsers(usernames)).map { (fls, us) =>
          ownCommand.changeOwnership(Unown, us, fls)
        }
      case InitialiseParameters() => Valid {
        initialiseCommand.initialiseDb(messageService)
      }
      case MultiDiscParameters(relativeDirectories, maybeMultiAction) =>
        (validateStagingDirectories(relativeDirectories) |@| requireMultiAction(maybeMultiAction)).map { (fls, multiAction) =>
          multiDiscCommand.mutateMultiDiscAlbum(fls, multiAction)(messageService)
        }
      case CalibrateParameters(maybeMaximumNumberOfThreads) =>
        validateMaximumNumberOfThreads(maybeMaximumNumberOfThreads).map { threads =>
          calibrateCommand.calibrate(threads)
        }
    }
    validatedParameters match {
      case Valid(parameters) =>
        execute(parameters) match {
          case Valid(future) => future
          case iv @ Invalid(_) => Future.successful(iv)
        }
      case iv @ Invalid(_) => Future.successful(iv)
    }
  }
}
