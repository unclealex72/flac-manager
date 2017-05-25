/*
 * Copyright 2014 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.message

import java.io.{PrintWriter, StringWriter}

import cats.data.ValidatedNel
import common.configuration.User
import common.files.{DeviceFileLocation, FileLocation, FlacFileLocation, StagedFlacFileLocation}

/**
 * An interface for classes that can print internationalised messages to users.
 *
 * @author alex
 *
 */
trait MessageService {

  def exception(t: Throwable): Unit

  def finish(): Unit

  private[message] def printMessage(template: Message): Unit
}

sealed abstract class Message(val key: String, val parameters: String*)

object Messages {

  import common.message.Messages.MessageImplicits._

  case class NO_FILES(fileLocations: Traversable[FileLocation]) extends Message("noFiles", fileLocations)

  /**
   * The key for producing an encoding message.
   */
  case class ENCODE(sourceFileLocation: FileLocation, targetFileLocation: FileLocation) extends
    Message("encode", sourceFileLocation, targetFileLocation)

  /**
   * The key for producing a delete message.
   */
  case class DELETE(fileLocation: FileLocation) extends Message("delete", fileLocation)

  /**
   * The key for producing a checkin message.
   */
  case class CHECKIN(stagedFlacFileLocation: StagedFlacFileLocation) extends Message("checkin", stagedFlacFileLocation)

  /**
   * The key for producing a checkin message.
   */
  case class CHECKOUT(flacFileLocation: FlacFileLocation) extends Message("checkout", flacFileLocation)

  /**
   * The key for producing a move message.
   */
  case class MOVE(source: FileLocation, target: FileLocation) extends Message("move", source, target)

  /**
   * The key for producing a not flac file message.
   */
  case class INVALID_FLAC(fileLocation: FileLocation) extends Message("invalidFlac", fileLocation)

  /**
   * The key for producing an overwrite message.
   */
  case class OVERWRITE(source: FileLocation, target: FileLocation) extends Message("overwrite", source, target)

  /**
   * The key for producing non unique messages.
   */
  case class NON_UNIQUE(flacFileLocation: FlacFileLocation,
                        stagedFlacFileLocations: Set[StagedFlacFileLocation]) extends
    Message("nonUnique", flacFileLocation, stagedFlacFileLocations)

  /**
    * The key for producing multi disc messages.
    */
  case class MULTI_DISC(stagedFlacFileLocation: StagedFlacFileLocation) extends
    Message("multiDisc", stagedFlacFileLocation)

  /**
   * The key for producing link messages.
   */
  case class LINK(fileLocation: FileLocation,
                  linkLocation: FileLocation) extends Message("link", fileLocation, linkLocation)

  /**
   * The key for producing link messages.
   */
  case class UNLINK(implicit messageService: MessageService) extends Message("unlink")

  /**
   * The key for producing add owner messages.
   */
  case class NOT_OWNED(stagedFlacFileLocation: StagedFlacFileLocation) extends
    Message("notOwned", stagedFlacFileLocation)

  /**
   * The key for producing add owner messages.
   */
  case class ADD_OWNER(fileLocation: FileLocation, user: User) extends Message("addOwner", fileLocation, user)

  /**
   * The key for producing remove owner messages.
   */
  case class REMOVE_OWNER(implicit messageService: MessageService) extends Message("removeOwner")

  /**
   * The key for producing commit ownership changes messages.
   */
  case class COMMIT_OWNERSHIP(implicit messageService: MessageService) extends Message("commitOwnership")

  /**
   * The key for producing a message to say that a file has been found.
   */
  case class FOUND_FILE(fileLocation: FileLocation) extends Message("foundFile", fileLocation)

  /**
   * The key for producing a message to say that devices are being searched.
   */
  case object LOOKING_FOR_DEVICES extends Message("lookingForDevices")

  /**
   * The key for producing a message to say that the database is not empty and so initialisation cannot continue.
   */
  case object DATABASE_NOT_EMPTY extends Message("databaseNotEmpty")

  /**
    * The key for producing a message to say that a multi-disc album is being joined.
    * @param albumTitle The name of the album being joined.
    */
  case class JOIN_ALBUM(albumTitle: String) extends Message("joinAlbum", albumTitle)

  /**
    * The key for producing a message to say that a multi-disc album is being split.
    * @param albumTitle The name of the album being split.
    */
  case class SPLIT_ALBUM(albumTitle: String) extends Message("splitAlbum", albumTitle)

  case class INITIALISING(deviceFileLocation: DeviceFileLocation) extends
    Message("initialising", deviceFileLocation)

  /**
   * The key for producing error keys.
   */
  case class INVALID_PARAMETERS(errorMessage: String) extends Message(errorMessage)

  case class EXCEPTION(e: Exception)(implicit messageService: MessageService) extends Message("exception", {
    val stringWriter = new StringWriter
    val printWriter = new PrintWriter(stringWriter)
    printWriter.println(e.getMessage)
    e.printStackTrace(printWriter)
    stringWriter.toString
  })

  private object MessageImplicits {

    implicit def fileLocationsToString[FL <: FileLocation](fls: Traversable[FL]): String = {
      fls.map(fileLocationToString(_)).mkString(", ")
    }

    implicit def fileLocationToString(fileLocation: FileLocation): String = fileLocation.toString

    implicit def userToString(user: User): String = user.name
  }

}

trait Messaging {

  def log(template: Message)(implicit messageService: MessageService): Unit = messageService.printMessage(template)

  implicit class TraversableLoggingImplicits[A](items: Set[A]) {
    def log(templateFactory: A => Message)(implicit messageService: MessageService): Set[A] = {
      items.foreach(item => Messaging.this.log(templateFactory(item)))
      items
    }
  }

  implicit class InvalidLoggingImplicits[F, S, V <: ValidatedNel[F, S]](validationNel: V) {
    def log(templateFactory: Seq[F] => Seq[Message])(implicit messageService: MessageService): V = {
      //logme
      validationNel
    }
  }

}



