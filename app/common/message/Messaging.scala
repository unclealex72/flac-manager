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

import common.configuration.User
import common.files.{DeviceFileLocation, FileLocation, FlacFileLocation, StagedFlacFileLocation}
import sync.{Device, DeviceFile}

import scalaz.ValidationNel

/**
 * An interface for classes that can print internationalised messages to users.
 *
 * @author alex
 *
 */
trait MessageService {

  def exception(t: Throwable): Unit

  def finish: Unit

  private[message] def printMessage(template: MessageType): Unit
}

sealed abstract class MessageType(val key: String, val parameters: String*)(implicit messageService: MessageService)

object MessageTypes {

  import common.message.MessageTypes.MessageTypeImplicits._

  case class NO_FILES(fileLocations: Traversable[FileLocation])(implicit messageService: MessageService) extends MessageType("noFiles", fileLocations)

  /**
   * The key for producing an encoding message.
   */
  case class ENCODE(sourceFileLocation: FileLocation, targetFileLocation: FileLocation)(implicit messageService: MessageService) extends MessageType("encode", sourceFileLocation, targetFileLocation)

  /**
   * The key for producing a delete message.
   */
  case class DELETE(fileLocation: FileLocation)(implicit messageService: MessageService) extends MessageType("delete", fileLocation)

  /**
   * The key for producing a checkin message.
   */
  case class CHECKIN(stagedFlacFileLocation: StagedFlacFileLocation)(implicit messageService: MessageService) extends MessageType("checkin", stagedFlacFileLocation)

  /**
   * The key for producing a checkin message.
   */
  case class CHECKOUT(flacFileLocation: FlacFileLocation)(implicit messageService: MessageService) extends MessageType("checkout", flacFileLocation)

  /**
   * The key for producing a move message.
   */
  case class MOVE(source: FileLocation, target: FileLocation)(implicit messageService: MessageService) extends MessageType("move", source, target)

  /**
   * The key for producing a not flac file message.
   */
  case class INVALID_FLAC(fileLocation: FileLocation)(implicit messageService: MessageService) extends MessageType("invalidFlac", fileLocation)

  /**
   * The key for producing an overwrite message.
   */
  case class OVERWRITE(source: FileLocation, target: FileLocation)(implicit messageService: MessageService) extends MessageType("overwrite", source, target)

  /**
   * The key for producing non unique messages.
   */
  case class NON_UNIQUE(flacFileLocation: FlacFileLocation, stagedFlacFileLocations: Set[StagedFlacFileLocation])(implicit messageService: MessageService) extends MessageType("nonUnique", flacFileLocation, stagedFlacFileLocations)

  /**
   * The key for producing link messages.
   */
  case class LINK(fileLocation: FileLocation, linkLocation: FileLocation)(implicit messageService: MessageService) extends MessageType("link", fileLocation, linkLocation)

  /**
   * The key for producing link messages.
   */
  case class UNLINK(implicit messageService: MessageService) extends MessageType("unlink")

  /**
   * The key for producing add owner messages.
   */
  case class NOT_OWNED(stagedFlacFileLocation: StagedFlacFileLocation)(implicit messageService: MessageService) extends MessageType("notOwned", stagedFlacFileLocation)

  /**
   * The key for producing add owner messages.
   */
  case class READING_COLLECTION(user: User)(implicit messageService: MessageService) extends MessageType("readingCollection", user)

  /**
   * The key for producing add owner messages.
   */
  case class ADD_OWNER(fileLocation: FileLocation, user: User)(implicit messageService: MessageService) extends MessageType("addOwner", fileLocation, user)

  /**
   * The key for producing remove owner messages.
   */
  case class REMOVE_OWNER(implicit messageService: MessageService) extends MessageType("removeOwner")

  /**
   * The key for producing commit ownership changes messages.
   */
  case class COMMIT_OWNERSHIP(implicit messageService: MessageService) extends MessageType("commitOwnership")

  /**
   * The key for producing a message to say that a file is being kept on a device.
   */
  case class SYNC_KEEP(deviceFile: DeviceFile)(implicit messageService: MessageService) extends MessageType("syncKeep", deviceFile)

  /**
   * The key for producing a message to say that a file is being removed from a device.
   */
  case class SYNC_REMOVE(deviceFile: DeviceFile)(implicit messageService: MessageService) extends MessageType("syncRemove", deviceFile)

  /**
   * The key for producing a message to say that a file is being removed from a device.
   */
  case class SYNC_IGNORE(path: String)(implicit messageService: MessageService) extends MessageType("syncIgnore", path)

  /**
   * The key for producing a message to say that a file is being added to a device.
   */
  case class SYNC_ADD(fileLocation: FileLocation)(implicit messageService: MessageService) extends MessageType("syncAdd", fileLocation)

  /**
   * The key for producing a message to say that a file has been found.
   */
  case class FOUND_FILE(fileLocation: FileLocation)(implicit messageService: MessageService) extends MessageType("foundFile", fileLocation)

  /**
   * The key for producing a message to say that devices are being searched.
   */
  case class LOOKING_FOR_DEVICES(implicit messageService: MessageService) extends MessageType("lookingForDevices")

  /**
   * The key for producing a message to say that a device is being synchronised.
   */
  case class SYNCHRONISING(device: Device)(implicit messageService: MessageService) extends MessageType("sync", device.owner, device)

  /**
   * The key for producing a message to say that a device has been found.
   */
  case class FOUND_DEVICE(device: Device)(implicit messageService: MessageService) extends MessageType("foundDevice", device.owner, device)

  /**
   * The key for producing a message to say that a device has been synchronised.
   */
  case class DEVICE_SYNCHRONISED(device: Device)(implicit messageService: MessageService) extends MessageType("deviceSynchronised", device.owner, device)

  /**
   * The key for producing a message to say that the database is not empty and so initialisation cannot continue.
   * @param messageService
   */
  case class DATABASE_NOT_EMPTY(implicit messageService: MessageService) extends MessageType("databaseNotEmpty")

  case class INITIALISING(deviceFileLocation: DeviceFileLocation)(implicit messageService: MessageService) extends MessageType("initialising", deviceFileLocation)

  /**
   * The key for producing error keys.
   */
  case class ERROR(errorKey: String, errorMessage: String, args: Seq[Any])(implicit messageService: MessageService) extends MessageType(
    s"error.${"""\[\d+\]""".r.replaceAllIn(errorKey, "")}.$errorMessage", args.map(_.toString): _*)

  private object MessageTypeImplicits {

    implicit def fileLocationsToString[FL <: FileLocation](fls: Traversable[FL]): String = {
      fls.map(fileLocationToString(_)).mkString(", ")
    }

    implicit def fileLocationToString(fileLocation: FileLocation): String = fileLocation.toMessage

    implicit def deviceFileToString(deviceFile: DeviceFile): String = deviceFile.relativePath

    implicit def userToString(user: User): String = user.name

    implicit def deviceToString(device: Device): String = device.name
  }

}

trait Messaging {

  def log(template: MessageType)(implicit messageService: MessageService) = messageService.printMessage(template)

  implicit class TraversableLoggingImplicits[A](items: Set[A]) {
    def log(templateFactory: A => MessageType)(implicit messageService: MessageService): Set[A] = {
      items.foreach(item => Messaging.this.log(templateFactory(item)))
      items
    }
  }

  implicit class FailureLoggingImplicits[F, S, V <: ValidationNel[F, S]](validationNel: V) {
    def log(templateFactory: Seq[F] => Seq[MessageType])(implicit messageService: MessageService): V = {
      //logme
      validationNel
    }
  }

}

/**
 * A trait for building messaging services.
 */
trait MessageServiceBuilder {

  def build: MessageService

  def withPrinter(printer: String => Unit): MessageServiceBuilder

  def withExceptionHandler(handler: Throwable => Unit): MessageServiceBuilder

  def withOnFinish(onFinish: () => Unit): MessageServiceBuilder
}

