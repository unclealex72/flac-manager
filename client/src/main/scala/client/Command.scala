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

import java.nio.file.{FileSystem, Path}

import cats.data.NonEmptyList
import cats.syntax.either._
import enumeratum.{Enum, EnumEntry}
import io.circe._
import json._
import scopt.Read

import scala.collection.immutable

/**
  *  A trait that encapsulates how arguments from the command line are parsed.
  *  Created by alex on 17/04/17
  **/
sealed trait Command extends EnumEntry {

  /**
    * The name of the command as seen by client users.
    */
  val name: String

  /**
    * Text on what this command does.
    */
  val usageText: String

  /**
    * The text to show for the users options, or none if not applicable.
    * @return
    */
  val usersDescriptionText: Option[String]

  /**
    * The text to describe files, or none if not applicable.
    */
  val directoriesDescriptionText: Option[String]

  /**
    * The text to describe the unown flag, or none if not applicable.
    */
  val unownText: Option[String]

  def parseArguments(arguments: Seq[String], datumFilename: String)(implicit fs: FileSystem): Either[NonEmptyList[String], Json]
}

object Command extends Enum[Command] {

  private[Command] abstract class ParametersParser[P <: Parameters](
                                                   val emptyParameters: P,
                                                   val parametersBuilder: ParametersBuilder[P]) extends Command {

    def parseArguments(
                        arguments: Seq[String],
                        datumFilename: String)
                      (implicit fs: FileSystem): Either[NonEmptyList[String], Json] = {

      implicit val pathParameterReader: Read[Path] = Read.reads(fs.getPath(_))

      val parser = new scopt.OptionParser[Either[NonEmptyList[String], P]](s"flacman-$name") {
        head(usageText)

        usersDescriptionText.foreach { usersDescriptionText =>
          opt[Seq[String]]('u', "users").required().valueName("<user1>,<user2>...").action { (users, eParameters) =>
            eParameters.flatMap { parameters => parametersBuilder.withUsers(parameters, users)
            }
          }.text(usersDescriptionText)
        }

        unownText.foreach { unownText =>
          opt[Unit]("unown").optional().action { (_, eParameters) =>
            eParameters.flatMap { parameters => parametersBuilder.withUnown(parameters, unown = true) }
          }.text(unownText)
        }

        help("help").text("Prints this usage text.")

        directoriesDescriptionText.foreach { directoriesDescriptionText =>
          arg[Path]("<file>...").unbounded().required().action { (dir, eParameters) =>
            eParameters.flatMap {
              //noinspection MatchToPartialFunction
              parameters =>
                Parameters.maybeDirectoryType(parameters) match {
                case Some(directoryType) =>
                  parametersBuilder.withExtraDirectory(parameters, datumFilename, directoryType, dir)
                case _ =>
                  Left(NonEmptyList.of(s"Command $name does not take file parameters."))
              }
            }
          }.text(directoriesDescriptionText)
        }
      }
      parser.parse(arguments, Right(emptyParameters)) match {
        case Some(eParameters) => eParameters.map { parameters =>
          Parameters.parametersEncoder(parameters)
        }
        case None => Left(NonEmptyList.of(""))
      }
    }
  }

  object OwnCommand extends ParametersParser[OwnParameters](
    OwnParameters(),
    OwnParametersBuilder) with Command {
    val name = "own"
    val usageText = "Add owners to staged flac files."
    val usersDescriptionText: Option[String] = Some("The users who will own the files.")
    val directoriesDescriptionText: Option[String] = Some("The files to be owned.")
    val unownText: Option[String] = None
  }

  object UnOwnCommand extends ParametersParser[UnownParameters](
    UnownParameters(),
    UnownParametersBuilder) with Command {
    val name = "unown"
    val usageText = "Remove owners from staged flac files."
    val usersDescriptionText: Option[String] = Some("The users who will no longer own the files.")
    val directoriesDescriptionText: Option[String] = Some("The files to be unowned.")
    val unownText: Option[String] = None
  }

  object CheckInCommand extends ParametersParser[CheckinParameters](
    CheckinParameters(),
    CheckinParametersBuilder) with Command {
    val name = "checkin"
    val usageText = "Check in staged flac files."
    val usersDescriptionText: Option[String] = None
    val directoriesDescriptionText: Option[String] = Some("The files to be checked in.")
    val unownText: Option[String] = None
  }

  object CheckOutCommand extends ParametersParser[CheckoutParameters](
    CheckoutParameters(),
    CheckoutParametersBuilder) with Command {
    val name = "checkout"
    val usageText = "Check out flac files."
    val usersDescriptionText: Option[String] = None
    val directoriesDescriptionText: Option[String] = Some("The files to be checked out.")
    val unownText: Option[String] = Some("Also unown any checked out files.")
  }

  val values: immutable.IndexedSeq[Command] = findValues

  /**
    * Find a command by its name.
    * @param commandName The name of the command to find.
    * @return The command with the given name or none if no command could be found.
    */
  def apply(commandName: String): Either[NonEmptyList[String], Command] =
    values.find(_.name == commandName).toRight(NonEmptyList.of(s"$commandName is not a valid command."))
}