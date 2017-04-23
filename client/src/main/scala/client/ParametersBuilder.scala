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
  * Created by alex on 20/04/17
  **/
trait ParametersBuilder[P <: Parameters] {

  def withExtraDirectory(
                          parameters: P,
                          datumFilename: String,
                          directoryType: DirectoryType,
                          directory: Path): Either[NonEmptyList[String], P]
  def withUsers(parameters: P, users: Seq[String]): Either[NonEmptyList[String], P]
  def withUnown(parameters: P, unown: Boolean): Either[NonEmptyList[String], P]
}

class FailingParametersBuilder[P <: Parameters](val commandName: String) extends ParametersBuilder[P] {
  
  private def fail(message: String): Either[NonEmptyList[String], P] = {
    Left(NonEmptyList.of(s"The $commandName command does not take $message."))
  }
  override def withExtraDirectory(
                                   parameters: P,
                                   datumFilename: String,
                                   directoryType: DirectoryType,
                                   directory: Path): Either[NonEmptyList[String], P] = {
    fail("directory parameters")
  }
  override def withUsers(parameters: P, users: Seq[String]): Either[NonEmptyList[String], P] = {
    fail("user parameters")
  }

  override def withUnown(parameters: P, unown: Boolean): Either[NonEmptyList[String], P] = {
    fail("an unown flag")
  }

  def extraDirectory(
                      relativeDirectories: Seq[Path],
                      datumFilename: String,
                      directoryType: DirectoryType,
                      directory: Path): Either[NonEmptyList[String], Seq[Path]] = {
    DirectoryRelativiser.relativise(datumFilename, directoryType, directory).map { relativeDirectory =>
      relativeDirectories :+ relativeDirectory
    }
  }
}

object CheckinParametersBuilder extends FailingParametersBuilder[CheckinParameters]("checkin") {

  override def withExtraDirectory(
                                parameters: CheckinParameters,
                                datumFilename: String,
                                directoryType: DirectoryType,
                                directory: Path): Either[NonEmptyList[String], CheckinParameters] = {
    extraDirectory(parameters.relativeStagingDirectories, datumFilename, directoryType, directory).map { paths =>
      parameters.copy(relativeStagingDirectories = paths)
    }
  }
}

object CheckoutParametersBuilder extends FailingParametersBuilder[CheckoutParameters]("checkout") {
  override def withExtraDirectory(
                                parameters: CheckoutParameters,
                                datumFilename: String,
                                directoryType: DirectoryType,
                                directory: Path): Either[NonEmptyList[String], CheckoutParameters] = {
    extraDirectory(parameters.relativeFlacDirectories, datumFilename: String, directoryType, directory).map { paths =>
      parameters.copy(relativeFlacDirectories = paths)
    }
  }

  override def withUnown(parameters: CheckoutParameters, unown: Boolean): Either[NonEmptyList[String], CheckoutParameters] = {
    Right(parameters.copy(unown = unown))
  }
}

object OwnParametersBuilder extends FailingParametersBuilder[OwnParameters]("own") {

  override def withUsers(
                          parameters: OwnParameters, 
                          users: Seq[String]): Either[NonEmptyList[String], OwnParameters] = {
    Right(parameters.copy(users = users))
  }

  override def withExtraDirectory(
                                   parameters: OwnParameters,
                                   datumFilename: String,
                                   directoryType: DirectoryType,
                                   directory: Path): Either[NonEmptyList[String], OwnParameters] = {
    extraDirectory(parameters.relativeStagingDirectories, datumFilename, directoryType, directory).map { paths =>
      parameters.copy(relativeStagingDirectories = paths)
    }
  }
}

object UnownParametersBuilder extends FailingParametersBuilder[UnownParameters]("unown") {

  override def withUsers(
                          parameters: UnownParameters,
                          users: Seq[String]): Either[NonEmptyList[String], UnownParameters] = {
    Right(parameters.copy(users = users))
  }

  override def withExtraDirectory(
                                   parameters: UnownParameters,
                                   datumFilename: String,
                                   directoryType: DirectoryType,
                                   directory: Path): Either[NonEmptyList[String], UnownParameters] = {
    extraDirectory(parameters.relativeStagingDirectories, datumFilename, directoryType, directory).map { paths =>
      parameters.copy(relativeStagingDirectories = paths)
    }
  }
}
