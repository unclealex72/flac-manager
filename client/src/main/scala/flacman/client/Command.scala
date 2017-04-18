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

import cats.data.NonEmptyList
import enumeratum.{Enum, EnumEntry}

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
  val usage: String

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

}

object Command extends Enum[Command] {

  object OwnCommand extends Command {
    val name = "own"
    val usage = "Add owners to staged flac files."
    val usersDescriptionText: Option[String] = Some("The users who will own the files.")
    val directoriesDescriptionText: Option[String] = Some("The files to be owned.")
    val unownText: Option[String] = None
  }

  object UnOwnCommand extends Command {
    val name = "unown"
    val usage = "Remove owners from staged flac files."
    val usersDescriptionText: Option[String] = Some("The users who will no longer own the files.")
    val directoriesDescriptionText: Option[String] = Some("The files to be unowned.")
    val unownText: Option[String] = None
  }

  object CheckInCommand extends Command {
    val name = "checkin"
    val usage = "Check in staged flac files."
    val usersDescriptionText: Option[String] = None
    val directoriesDescriptionText: Option[String] = Some("The files to be checked in.")
    val unownText: Option[String] = None
  }

  object CheckOutCommand extends Command {
    val name = "checkout"
    val usage = "Check out flac files."
    val usersDescriptionText: Option[String] = None
    val directoriesDescriptionText: Option[String] = Some("The files to be checked out.")
    val unownText: Option[String] = Some("Also unown any checked out files.")
  }

  val values: Seq[Command] = findValues

  /**
    * Find a command by its name.
    * @param commandName The name of the command to find.
    * @return The command with the given name or none if no command could be found.
    */
  def apply(commandName: String): Either[NonEmptyList[String], Command] =
    values.find(_.name == commandName).toRight(NonEmptyList.of(s"$commandName is not a valid command."))
}