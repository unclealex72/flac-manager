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

import java.io.File
import java.nio.file.{Files, Path}

import cats.data.NonEmptyList
import cats.syntax.either._

/**
  * A class containing the command line arguments.
  * @param users
  * @param directories
  * @param unown
  */
case class Config(command: Command, users: Seq[String] = Seq.empty, directories: Seq[File] = Seq.empty, unown: Boolean = false) {

  lazy val absoluteDirectories: Set[Path] = directories.map { directory =>
    directory.getCanonicalFile.toPath
  }.toSet

  def findDatumFiles(datumFilename: String): Map[Path, Path] = {
    def datumFileForDirectory(dir: Path): Option[Path] = {
      val possibleDatumPath = dir.resolve(datumFilename)
      if (Files.exists(possibleDatumPath)) {
        Some(possibleDatumPath)
      }
      else {
        Option(dir.getParent).flatMap(datumFileForDirectory)
      }
    }
    val empty: Map[Path, Path] = Map.empty
    absoluteDirectories.foldLeft(empty) { (map, dir) =>
      map ++ datumFileForDirectory(dir).map(df => dir -> df)
    }
  }
}

/**
  * Created by alex on 17/04/17
  **/
object Config {

  def apply(args: Seq[String]): Either[NonEmptyList[String], Config] = {
    for {
      commandName <- args.headOption.toRight(NonEmptyList.of("No arguments have been supplied."))
      command <- Command(commandName)
      config <- parse(command, args.tail)
    } yield {
      config
    }
  }

  private def parse(command: Command, args: Seq[String]): Either[NonEmptyList[String], Config] = {
    val parser = new scopt.OptionParser[Config](s"flacman-${command.name}") {
      head(command.usage)

      command.usersDescriptionText.foreach { usersDescriptionText =>
        opt[Seq[String]]('u', "users").required().valueName("<user1>,<user2>...").action((users, config) =>
          config.copy(users = users)).text(usersDescriptionText)
      }

      command.unownText.foreach { unownText =>
        opt[Unit]("unown").optional().action((_, config) =>
          config.copy(unown = true)).text(unownText)

      }

      help("help").text("Prints this usage text.")

      command.directoriesDescriptionText.foreach { directoriesDescriptionText =>
        arg[File]("<file>...").unbounded().required().action((dir, config) =>
          config.copy(directories = config.directories :+ dir)).text(directoriesDescriptionText)
      }
    }
    parser.parse(args, Config(command)).toRight(NonEmptyList.of(""))
  }

}
