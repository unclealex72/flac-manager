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
import enumeratum.{Enum, EnumEntry}
import io.circe._
import json._
import scopt.{OptionParser, Read}

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
    */
  val maybeUsersDescriptionText: Option[String]

  /**
    * The text to describe files, or none if not applicable.
    */
  val maybeDirectoriesDescriptionText: Option[String]

  /**
    * The text to describe the unown flag, or none if not applicable.
    */
  val maybeUnownText: Option[String]

  /**
    * The text to describe the allow unowned flag, or none if not applicable.
    */
  val maybeAllowUnownedText: Option[String]

  /**
    * The text to describe the number of threads for calibration, or none if not applicable.
    */
  val maybeNumberOfThreadsText: Option[String]

  /**
    * The text for multi action flags.
    */
  val multiActionTexts: Map[MultiAction, String]

  /**
    * Parse arguments from the command line.
    * @param arguments The command line arguments.
    * @param datumFilename The datum filename supplied by the server.
    * @param fs The Java NIO file system.
    * @return Either a JSON RPC payload that can be sent to the server to execute a command or a list of errors.
    */
  def parseArguments(
                      arguments: Seq[String],
                      datumFilename: String)
                    (implicit fs: FileSystem): Either[NonEmptyList[String], Json]
}

/**
  * Used to enumerate and build the available commands.
  */
object Command extends Enum[Command] {

  private[Command] abstract class ParametersParser[P <: Parameters](
                                                   val emptyParameters: P,
                                                   val parametersBuilder: ParametersBuilder[P]) extends Command {

    def parseArguments(
                        arguments: Seq[String],
                        datumFilename: String)
                      (implicit fs: FileSystem): Either[NonEmptyList[String], Json] = {

      implicit val pathParameterReader: Read[Path] = Read.reads(fs.getPath(_))

      val parser: OptionParser[Either[NonEmptyList[String], P]] =
        new scopt.OptionParser[Either[NonEmptyList[String], P]](s"flacman-$name") {
        head(usageText)

        maybeUsersDescriptionText.foreach { usersDescriptionText =>
          opt[Seq[String]]('u', "users").required().valueName("<user1>,<user2>...").action { (users, eParameters) =>
            eParameters.flatMap { parameters => parametersBuilder.withUsers(parameters, users)
            }
          }.text(usersDescriptionText)
        }

        maybeUnownText.foreach { unownText =>
          opt[Unit]("unown").optional().action { (_, eParameters) =>
            eParameters.flatMap { parameters => parametersBuilder.withUnown(parameters, unown = true) }
          }.text(unownText)
        }

        maybeAllowUnownedText.foreach { allowUnownedText =>
          opt[Unit]("allow-unowned").optional().action { (_, eParameters) =>
            eParameters.flatMap { parameters => parametersBuilder.withAllowUnowned(parameters, allowUnowned = true) }
          }.text(allowUnownedText)
        }

        multiActionTexts.foreach {
          case (action, text) =>
            opt[Unit](action.identifier).action { (_, eParameters) =>
              eParameters.flatMap { parameters => parametersBuilder.withMultiAction(parameters, action) }
            }.text(text)
        }

        help("help").text("Prints this usage text.")

        maybeDirectoriesDescriptionText.foreach { directoriesDescriptionText =>
          arg[Path]("<file>...").unbounded().required().action { (dir, eParameters) =>
            eParameters.flatMap {
              //noinspection MatchToPartialFunction
              parameters =>
                val repositoryTypes: Seq[RepositoryType] = Parameters.repositoryTypes(parameters)
                if (repositoryTypes.isEmpty) {
                  Left(NonEmptyList.of(s"Command $name does not take file parameters."))
                }
                else {
                  parametersBuilder.withExtraDirectory(parameters, datumFilename, repositoryTypes, dir)
                }
            }
          }.text(directoriesDescriptionText)
        }

        maybeNumberOfThreadsText.foreach { numberOfThreadsText =>
          arg[Int]("<numberOfThreads>").maxOccurs(1).optional().action { (threads, eParameters) =>
            eParameters.flatMap { parameters =>
              parametersBuilder.withNumberOfThreads(parameters, threads)
            }
          }.text(numberOfThreadsText)
        }
      }

      parser.parse(arguments, Right(emptyParameters)) match {
        case Some(eParameters) => eParameters.flatMap { parameters =>
          parametersBuilder.checkValid(parameters).map { validParameters =>
            Parameters.parametersEncoder.apply(validParameters)
          }
        }
        case None => Left(NonEmptyList.of("Arguments could not be parsed."))
      }
    }
  }

  /**
    * The `own` command
    */
  object OwnCommand extends ParametersParser[OwnParameters](
    OwnParameters(),
    OwnParametersBuilder) with Command {
    val name = "own"
    val usageText = "Add owners to staged flac files."
    val maybeUsersDescriptionText: Option[String] = Some("The users who will own the files.")
    val maybeDirectoriesDescriptionText: Option[String] = Some("The files to be owned.")
    val maybeUnownText: Option[String] = None
    val maybeAllowUnownedText: Option[String] = None
    val maybeNumberOfThreadsText: Option[String] = None
    val multiActionTexts: Map[MultiAction, String] = Map.empty
  }

  /**
    * The `unown` command
    */
  object UnOwnCommand extends ParametersParser[UnownParameters](
    UnownParameters(),
    UnownParametersBuilder) with Command {
    val name = "unown"
    val usageText = "Remove owners from staged flac files."
    val maybeUsersDescriptionText: Option[String] = Some("The users who will no longer own the files.")
    val maybeDirectoriesDescriptionText: Option[String] = Some("The files to be unowned.")
    val maybeUnownText: Option[String] = None
    val maybeAllowUnownedText: Option[String] = None
    val maybeNumberOfThreadsText: Option[String] = None
    val multiActionTexts: Map[MultiAction, String] = Map.empty
  }

  /**
    * The `checkin` command
    */
  object CheckInCommand extends ParametersParser[CheckinParameters](
    CheckinParameters(),
    CheckinParametersBuilder) with Command {
    val name = "checkin"
    val usageText = "Check in staged flac files."
    val maybeUsersDescriptionText: Option[String] = None
    val maybeDirectoriesDescriptionText: Option[String] = Some("The files to be checked in.")
    val maybeUnownText: Option[String] = None
    val maybeAllowUnownedText: Option[String] = Some("Allow files without owners to be checked in.")
    val maybeNumberOfThreadsText: Option[String] = None
    val multiActionTexts: Map[MultiAction, String] = Map.empty
  }

  /**
    * The `checkout` command
    */
  object CheckOutCommand extends ParametersParser[CheckoutParameters](
    CheckoutParameters(),
    CheckoutParametersBuilder) with Command {
    val name = "checkout"
    val usageText = "Check out flac files."
    val maybeUsersDescriptionText: Option[String] = None
    val maybeDirectoriesDescriptionText: Option[String] = Some("The files to be checked out.")
    val maybeUnownText: Option[String] = Some("Also unown any checked out files.")
    val maybeAllowUnownedText: Option[String] = None
    val maybeNumberOfThreadsText: Option[String] = None
    val multiActionTexts: Map[MultiAction, String] = Map.empty
  }

  /**
    * The `checkin` command
    */
  object MultiCommand extends ParametersParser[MultiDiscParameters](
    MultiDiscParameters(),
    MultiParametersBuilder) with Command {
    val name = "multidisc"
    val usageText = "Split or join multiple disc albums."
    val maybeUsersDescriptionText: Option[String] = None
    val maybeDirectoriesDescriptionText: Option[String] = Some("The files to be split or joined")
    val maybeUnownText: Option[String] = None
    val maybeAllowUnownedText: Option[String] = None
    val maybeNumberOfThreadsText: Option[String] = None
    val multiActionTexts: Map[MultiAction, String] = Map(
      MultiAction.Join -> "Join albums into one long album",
      MultiAction.Split -> "Keep albums split but with (Extra) as a prefix for any discs but the first."
    )
  }

  /**
    * The `calibrate` command
    */
  object CalibrateCommand extends ParametersParser[CalibrateParameters](
    CalibrateParameters(),
    CalibrateParametersBuilder) with Command {
    val name = "calibrate"
    val usageText = "Test to find the optimal number of threads for encoding."
    val maybeUsersDescriptionText: Option[String] = None
    val maybeDirectoriesDescriptionText: Option[String] = None
    val maybeUnownText: Option[String] = None
    val maybeAllowUnownedText: Option[String] = None
    val maybeNumberOfThreadsText: Option[String] = Some("The maximum number of threads to test.")
    val multiActionTexts: Map[MultiAction, String] = Map.empty
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