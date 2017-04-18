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

package flacman.client

import java.nio.file.Path

import cats.data.Validated._
import cats.data._
import cats.implicits._

/**
  * Created by alex on 17/04/17
  **/
object BodyBuilder {

  private type ValidatedBody = Validated[NonEmptyList[String], Map[String, String]]

  /**
    * Create a map of key value pairs to be sent to the server as a form.
    * @param config The config garnered from the command line
    * @param datumFilename The name of the datum file.
    * @return A validated map of key value pairs.
    */
  def apply(config: Config, datumFilename: String): Either[NonEmptyList[String], Map[String, String]] = {
    val absoluteDirectories = config.absoluteDirectories
    val datumFilesByAbsoluteDirectory = config.findDatumFiles(datumFilename)
    val missingDirectories = absoluteDirectories -- datumFilesByAbsoluteDirectory.keySet
    val distinctDatumPaths = datumFilesByAbsoluteDirectory.values.toSet
    validateMissingDirectories(missingDirectories) |@|
    validateDistinctDatumPaths(distinctDatumPaths) |@|
    Valid(createBody(config, distinctDatumPaths)) map { _ ++ _ ++ _ }
  }.toEither

  private def validateMissingDirectories(missingDirectories: Set[Path]): ValidatedBody = {
    NonEmptyList.fromList(missingDirectories.toList) match {
      case None => Valid(Map.empty)
      case Some(nonEmptyMissingDirectories) => Invalid(nonEmptyMissingDirectories.map { missingDirectory =>
        s"Cannot find a datum file for directory $missingDirectory"
      })
    }
  }

  private def validateDistinctDatumPaths(datumPaths: Set[Path]): ValidatedBody = {
    if (datumPaths.size < 2) {
      Valid(Map.empty)
    }
    else {
      Invalid(NonEmptyList.fromListUnsafe(datumPaths.toList).map { datumPath =>
        s"Found non-unique datum path $datumPath"
      })
    }
  }

  private def createBody(config: Config, datumPaths: Set[Path]): Map[String, String] = {
    def buildListParameters(key: String, values: Iterable[_]): Map[String, String] = {
      values.zipWithIndex.map {
        case (value, idx) => s"$key[$idx]" -> value.toString
      }.toMap
    }
    buildListParameters("owners", config.users) ++
      buildListParameters("directories", config.absoluteDirectories) ++
      datumPaths.map("datum" -> _.toString).toMap
  }
}
