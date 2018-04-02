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
import java.nio.file.Path
import java.time.{Clock, Duration, Instant}

import cats.data.ValidatedNel
import common.changes.{Change, ChangeType}
import common.configuration.User
import common.files._
import io.circe.DecodingFailure

/**
 * An interface for classes that can print internationalised messages to users.
 *
 * @author alex
 *
 */
trait MessageService {

  def exception(t: Throwable): Unit

  private[message] def printMessage(template: Message): Unit
}

sealed abstract class Message(val key: String, val parameters: String*)

object Messages {

  import common.message.Messages.MessageImplicits._

  case class NO_FILES(files: Traversable[File]) extends Message("noFiles", files)

  /**
   * The key for producing an encoding message.
   */
  case class ENCODE(sourceFile: File, targetFile: File) extends
    Message("encode", sourceFile, targetFile)

  /**
   * The key for producing a delete message.
   */
  case class DELETE(file: File) extends Message("delete", file)

  /**
   * The key for producing a checkin message.
   */
  case class CHECKIN(stagingFile: StagingFile) extends Message("checkin", stagingFile)

  /**
   * The key for producing a checkin message.
   */
  case class CHECKOUT(flacFile: FlacFile) extends Message("checkout", flacFile)

  /**
   * The key for producing a move message.
   */
  case class MOVE(source: File, target: File) extends Message("move", source, target)

  /**
   * The key for producing a not flac file message.
   */
  case class INVALID_FLAC(file: File) extends Message("invalidFlac", file)

  /**
    * The key for producing invalid tag messages.
    * @param tagError The error returned from the tags.
    */
  case class INVALID_TAGS(tagError: String) extends Message("invalidTag", tagError)

  /**
   * The key for producing an overwrite message.
   */
  case class OVERWRITE(source: File, target: File) extends Message("overwrite", source, target)

  /**
   * The key for producing non unique messages.
   */
  case class NON_UNIQUE(flacFile: FlacFile,
                        stagingFiles: Seq[StagingFile]) extends
    Message("nonUnique", flacFile, stagingFiles)

  /**
    * The key for producing multi disc messages.
    */
  case class MULTI_DISC(stagingFile: StagingFile) extends
    Message("multiDisc", stagingFile)

  /**
   * The key for producing link messages.
   */
  case class LINK(sourceFile: File,
                  targetFile: File) extends Message("link", sourceFile, targetFile)

  /**
   * The key for producing link messages.
   */
  case class UNLINK(encodedFile: EncodedFile,
                    deviceFile: DeviceFile) extends Message("unlink", encodedFile, deviceFile)


  /**
   * The key for producing add owner messages.
   */
  case class NOT_OWNED(stagingFile: StagingFile) extends
    Message("notOwned", stagingFile)

  /**
   * The key for producing add owner messages.
   */
  case class ADD_OWNER(user: User, artist: String, album: String) extends Message("addOwner", user, artist, album)

  /**
   * The key for producing remove owner messages.
   */
  case class REMOVE_OWNER(user: User, artist: String, album: String) extends Message("removeOwner", user, artist, album)

  /**
   * The key for producing a message to say that a file has been found.
   */
  case class FOUND_FILE(file: File) extends Message("foundFile", file)

  /**
   * The key for producing a message to say that devices are being searched.
   */
  case object LOOKING_FOR_DEVICES extends Message("lookingForDevices")

  /**
   * The key for producing a message to say that the database is not empty and so initialisation cannot continue.
   */
  case object DATABASE_NOT_EMPTY extends Message("databaseNotEmpty")

  /**
    * The key for producing a message to say that initialisation has started.
    */
  case object INITIALISATION_STARTED extends Message("initialisationStared")
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

  /**
    * The key for producing a message to say that a file is being initialised.
    * @param deviceFile The file being initialised.
    */
  case class INITIALISING(deviceFile: DeviceFile) extends
    Message("initialising", deviceFile)

  /**
    * The key for producing a message to say that a change is being added to the database.
    * @param change The change to be added.
    */
  case class ADD_CHANGE(change: Change) extends Message("addChange", change.action, change.relativePath, change.user)

  /**
   * The key for producing error keys.
   */
  case class INVALID_PARAMETERS(errorMessage: String) extends Message(errorMessage)

  /**
    * The key for producing te no users message.
    */
  case object NO_USERS extends Message("noUsers")

  case object NOT_ENOUGH_THREADS extends Message("notEnoughThreads")

  case class ENCODE_DURATION(flacFile: FlacFile, seconds: Double) extends Message("encodeDuration", flacFile, seconds)

  case class NO_DIRECTORIES(repositoryType: String) extends Message("noDirectories")

  case class NOT_A_DIRECTORY(path: Path, repositoryType: String) extends Message("notADirectory", path, repositoryType)

  case class NOT_A_FILE(path: Path, repositoryType: String) extends Message("notAFile", path, repositoryType)

  case class CALIBRATION_RUN_STARTING(
                                       numberOfEncodingJobs: Int,
                                       concurrencyLevel: Int) extends Message("calibrationRunStarting", numberOfEncodingJobs, concurrencyLevel)
  case class CALIBRATION_RUN_FINISHED(
                                       numberOfEncodingJobs: Int,
                                       concurrencyLevel: Int,
                                       totalDuration: Double,
                                       averageDuration: Double) extends Message("calibrationRunFinished", numberOfEncodingJobs, concurrencyLevel, totalDuration, averageDuration)
  case class CALIBRATION_RESULT(concurrencyLevel: Int, averageDuration: Double) extends Message("calibrationResult", concurrencyLevel, averageDuration)

  object MULTI_ACTION_REQUIRED extends Message("multiActionRequired")
  /**
    * The key for producing Json errors.
    * @param decodingFailure The decoding failure that occurred.
    */
  case class JSON_ERROR(decodingFailure: DecodingFailure) extends Message("exception", decodingFailure.getMessage())

  /**
    * The key for producing invalid user messages.
    * @param username The invalid user name.
    */
  case class INVALID_USER(username: String) extends Message("invalidUser", username)

  case class EXCEPTION(t: Throwable) extends Message("exception", {
    val stringWriter = new StringWriter
    val printWriter = new PrintWriter(stringWriter)
    printWriter.println(t.getMessage)
    t.printStackTrace(printWriter)
    stringWriter.toString
  })

  case class ERROR(message: String) extends Message("exception", message)

  private object MessageImplicits {

    implicit def filesToString[FL <: File](fls: Traversable[FL]): String = {
      fls.map(fileToString(_)).mkString(", ")
    }

    implicit def fileToString[FL <: File](file: FL): String = file.toString

    implicit def directoryToString[FL <: File](directory: Directory[FL]): String = directory.toString

    implicit def userToString(user: User): String = user.name

    implicit def longToString(l: Long): String = l.toString

    implicit def intToString(i: Int): String = i.toString

    implicit def changeTypeToString(changeType: ChangeType): String = changeType.action

    implicit def pathToString(path: Path): String = path.toString

    implicit def doubleToString(d: Double): String = d.toString
  }

}

trait Messaging {

  def log(template: Message)(implicit messageService: MessageService): Unit = messageService.printMessage(template)

  def time[T](template: Duration => Message)(block: => T)(implicit messageService: MessageService, clock: Clock): T = {
    val startTime: Instant = clock.instant()
    val result: T = block
    val endTime: Instant = clock.instant()
    val duration: Duration = Duration.between(startTime, endTime)
    log(template(duration))
    result
  }

  def logException(e: Exception)(implicit messageService: MessageService): Unit = {
    messageService.exception(e)
  }

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



