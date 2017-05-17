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

package client

import java.nio.file.Path

import cats.data.NonEmptyList
import cats.syntax.either._
import json._

/**
  * A typeclass used to generate parameters from command line options.
  * @tparam P A [[Parameters]] type.
  */
trait ParametersBuilder[P <: Parameters] {

  /**
    * Add an extra directory specified in the command line to the parameters object.
    * @param parameters The current parameters object
    * @param datumFilename The name of the server's datum file.
    * @param repositoryTypes The types of directory, either staging or flac.
    * @param directory The directory to add.
    * @return either a new parameters object with the extra directory or a list of errors.
    */
  def withExtraDirectory(
                          parameters: P,
                          datumFilename: String,
                          repositoryTypes: Seq[RepositoryType],
                          directory: Path): Either[NonEmptyList[String], P]

  /**
    * Add users specified in the command line to the parameters object.
    * @param parameters The current parameters object
    * @param users The names of the users to add.
    * @return either a new parameters object with the users or a list of errors.
    */
  def withUsers(parameters: P, users: Seq[String]): Either[NonEmptyList[String], P]


  /**
    * Add an unown flag in the command line to the parameters object.
    * @param parameters The current parameters object
    * @param unown The value of the unown flag.
    * @return either a new parameters object with the unown flag or a list of errors.
    */
  def withUnown(parameters: P, unown: Boolean): Either[NonEmptyList[String], P]
}

/**
  * A base for [[ParametersBuilder]] classes. This class assumes that everything fails so subclasses only need
  * to override the methods that are pursuant to the the parameter type P.
  * @param commandName The name of the command to be used in reporting errors and help messages.
  * @tparam P A [[Parameters]] type.
  */
class FailingParametersBuilder[P <: Parameters](val commandName: String) extends ParametersBuilder[P] {
  
  private def fail(message: String): Either[NonEmptyList[String], P] = {
    Left(NonEmptyList.of(s"The $commandName command does not take $message."))
  }

  /**
    * Fail to add an extra directory.
    * @param parameters The current parameters object
    * @param datumFilename The name of the server's datum file.
    * @param repositoryTypes The type of directory, either staging or flac.
    * @param directory The directory to add.
    * @return either a new parameters object with the extra directory or a list of errors.
    */
  override def withExtraDirectory(
                                   parameters: P,
                                   datumFilename: String,
                                   repositoryTypes: Seq[RepositoryType],
                                   directory: Path): Either[NonEmptyList[String], P] = {
    fail("directory parameters")
  }

  /**
    * Fail to add any users.
    * @param parameters The current parameters object
    * @param users The names of the users to add.
    * @return either a new parameters object with the users or a list of errors.
    */
  override def withUsers(parameters: P, users: Seq[String]): Either[NonEmptyList[String], P] = {
    fail("user parameters")
  }

  /**
    * Fail to add an unown flag.
    * @param parameters The current parameters object
    * @param unown The value of the unown flag.
    * @return either a new parameters object with the unown flag or a list of errors.
    */
  override def withUnown(parameters: P, unown: Boolean): Either[NonEmptyList[String], P] = {
    fail("an unown flag")
  }

  /**
    * Convert a directory in to a relative path and add it to a list of relative directories.
    * @param relativeDirectories The current list of relative directories.
    * @param datumFilename The name of the server's datum file.
    * @param repositoryTypes The directory type, either staging or flac.
    * @param directory The absolute directory to add.
    * @return Either a new list of relative directories containing the new directory or a list of errors.
    */
  def extraDirectory(
                      relativeDirectories: Seq[PathAndRepository],
                      datumFilename: String,
                      repositoryTypes: Seq[RepositoryType],
                      directory: Path): Either[NonEmptyList[String], Seq[PathAndRepository]] = {
    DirectoryRelativiser.relativise(datumFilename, repositoryTypes, directory).map { relativeDirectory =>
      relativeDirectories :+ relativeDirectory
    }
  }
}

/**
  * Build parameters for the `checkin` command.
  */
object CheckinParametersBuilder extends FailingParametersBuilder[CheckinParameters]("checkin") {

  /**
    * @inheritdoc
    */
  override def withExtraDirectory(
                                   parameters: CheckinParameters,
                                   datumFilename: String,
                                   repositoryTypes: Seq[RepositoryType],
                                   directory: Path): Either[NonEmptyList[String], CheckinParameters] = {
    extraDirectory(parameters.relativeDirectories, datumFilename, repositoryTypes, directory).map { paths =>
      parameters.copy(relativeDirectories = paths)
    }
  }
}

/**
  * Build parameters for the `checkout` command.
  */
object CheckoutParametersBuilder extends FailingParametersBuilder[CheckoutParameters]("checkout") {

  /**
    * @inheritdoc
    */
  override def withExtraDirectory(
                                   parameters: CheckoutParameters,
                                   datumFilename: String,
                                   repositoryTypes: Seq[RepositoryType],
                                   directory: Path): Either[NonEmptyList[String], CheckoutParameters] = {
    extraDirectory(parameters.relativeDirectories, datumFilename: String, repositoryTypes, directory).map { paths =>
      parameters.copy(relativeDirectories = paths)
    }
  }

  /**
    * @inheritdoc
    */
  override def withUnown(parameters: CheckoutParameters, unown: Boolean): Either[NonEmptyList[String], CheckoutParameters] = {
    Right(parameters.copy(unown = unown))
  }
}

/**
  * Build parameters for the `own` command.
  */
object OwnParametersBuilder extends FailingParametersBuilder[OwnParameters]("own") {

  /**
    * @inheritdoc
    */
  override def withUsers(
                          parameters: OwnParameters, 
                          users: Seq[String]): Either[NonEmptyList[String], OwnParameters] = {
    Right(parameters.copy(users = users))
  }

  /**
    * @inheritdoc
    */
  override def withExtraDirectory(
                                   parameters: OwnParameters,
                                   datumFilename: String,
                                   repositoryTypes: Seq[RepositoryType],
                                   directory: Path): Either[NonEmptyList[String], OwnParameters] = {
    extraDirectory(parameters.relativeDirectories, datumFilename, repositoryTypes, directory).map { paths =>
      parameters.copy(relativeDirectories = paths)
    }
  }
}

/**
  * Build parameters for the `unown` command.
  */
object UnownParametersBuilder extends FailingParametersBuilder[UnownParameters]("unown") {

  /**
    * @inheritdoc
    */
  override def withUsers(
                          parameters: UnownParameters,
                          users: Seq[String]): Either[NonEmptyList[String], UnownParameters] = {
    Right(parameters.copy(users = users))
  }

  /**
    * @inheritdoc
    */
  override def withExtraDirectory(
                                   parameters: UnownParameters,
                                   datumFilename: String,
                                   repositoryTypes: Seq[RepositoryType],
                                   directory: Path): Either[NonEmptyList[String], UnownParameters] = {
    extraDirectory(parameters.relativeDirectories, datumFilename, repositoryTypes, directory).map { paths =>
      parameters.copy(relativeDirectories = paths)
    }
  }
}
