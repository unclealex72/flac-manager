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

import java.nio.file.FileSystem

import cats.data.NonEmptyList
import cats.syntax.either._
import io.circe.Json

/**
  *
  * Created by alex on 17/04/17
  **/
object ParametersParser {

  def apply(
             datumFilename: String,
             args: Seq[String])(implicit fs: FileSystem): Either[NonEmptyList[String], Json] = {
    for {
      commandName <- args.headOption.toRight(NonEmptyList.of("No arguments have been supplied."))
      command <- Command(commandName)
      body <- command.parseArguments(args.tail, datumFilename)
    } yield {
      body
    }
  }

}
