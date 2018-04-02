/*
 * Copyright 2018 Alex Jones
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

package common.owners

import java.time.{Clock, Instant}

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}
import checkin.LossyEncoder
import common.async.CommandExecutionContext
import common.changes.{Change, ChangeDao}
import common.collections.CollectionDao
import common.configuration.{User, UserDao}
import common.files._
import common.message.Messages.{ADD_OWNER, REMOVE_OWNER}
import common.message.{Message, MessageService, Messaging}
import common.music.Tags
import javax.inject.Inject
import own.OwnAction
import own.OwnAction._

import scala.concurrent.Future

/**
  * The default implementation of [[OwnerService]]
  * @param collectionDao          The [[CollectionDao]] used to read and alter user's collections.
  * @param fileSystem             The [[FileSystem]] used to link and remove device files.
  * @param clock                  The [[java.time.Clock]] used to get the current time.
  * @param changeDao              The [[ChangeDao]] used to tell devices that encoded files need to be added or removed.
  * @param userDao                The [[UserDao]] used to list the current users.
  */
class OwnerServiceImpl @Inject()(
                                  val collectionDao: CollectionDao,
                                  val fileSystem: FileSystem,
                                  val clock: Clock,
                                  val lossyEncoders: Seq[LossyEncoder],
                                  val userDao: UserDao, val changeDao: ChangeDao)
                                (implicit val commandExecutionContext: CommandExecutionContext) extends OwnerService with Messaging {

  val extensions: Seq[Extension] = lossyEncoders.map(_.encodesTo)

  /**
    * @inheritdoc
    */
  override def listOwners(): Future[Map[String, Set[User]]] = {
    collectionDao.allOwnersByRelease().map(_.mapValues(_.toSet))
  }

  /**
    * @inheritdoc
    */
  override def changeStagingOwnership(
                                       user: User,
                                       action: OwnAction,
                                       stagingFiles: NonEmptyList[StagingFile])
                                    (implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]] = {
    changeOwnership(user, action, stagingFiles, Seq.empty)
  }

  /**
    * @inheritdoc
    */
  override def changeFlacOwnership(
                                    user: User,
                                    action: OwnAction,
                                    flacFiles: NonEmptyList[FlacFile])
                                  (implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]] = {
    val encodedFiles: List[EncodedFile] = for {
      flacFile <- flacFiles.toList
      extension <- extensions
    } yield {
      flacFile.toEncodedFile(extension)
    }
    changeOwnership(
      user,
      action,
      flacFiles,
      encodedFiles)
  }

  /**
    * @inheritdoc
    */
  override def ownDeviceFile(user: User,
                             deviceFile: DeviceFile)
                            (implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]] = {
    changeOwnership(user, Own, NonEmptyList.of(deviceFile), Seq.empty)
  }

  def changeOwnership[FL <: File](
                                   user: User,
                                   action: OwnAction,
                                   files: NonEmptyList[FL],
                                   encodedFiles: Seq[EncodedFile])
                     (implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]] = {
    files.head.tags.read() match {
      case Valid(tags) =>
        action match {
          case Own =>
            encodedFiles.foldLeft(addRelease(user, tags).map(_ => Valid({}))) { (acc, encodedFile) =>
              val deviceFile: DeviceFile = encodedFile.toDeviceFile(user)
              fileSystem.link(encodedFile, deviceFile)
              acc.flatMap(_ => changeDao.store(Change.added(deviceFile, Instant.now(clock)))).map(_ => Valid({}))
            }
          case Unown =>
            encodedFiles.foldLeft(removeRelease(user, tags).map(_ => Valid({}))) { (acc, encodedFile) =>
              val deviceFile: DeviceFile = encodedFile.toDeviceFile(user)
              fileSystem.remove(deviceFile)
              acc.flatMap(_ => changeDao.store(Change.removed(deviceFile, Instant.now(clock)))).map(_ => Valid({}))
            }
        }
      case iv @ Invalid(_) => Future.successful(iv)
    }
  }

  private def addRelease(user: User, tags: Tags)(implicit messageService: MessageService): Future[Unit] = {
    log(ADD_OWNER(user, tags.artist, tags.album))
    collectionDao.addRelease(user = user, releaseId = tags.albumId, artist = tags.artist, album = tags.album).map(_ => {})
  }

  private def removeRelease(user: User, tags: Tags)(implicit messageService: MessageService): Future[Unit] = {
    log(REMOVE_OWNER(user, tags.artist, tags.album))
    collectionDao.removeRelease(user = user, releaseId = tags.albumId).map(_ => {})
  }

  /**
    * @inheritdoc
    */
  override def unown(user: User, tags: Set[Tags])(implicit messageService: MessageService): Future[Unit] = {
    val empty: Future[Unit] = Future.successful({})
    tags.foldLeft(empty){ (acc, myTags) =>
      acc.flatMap(_ => removeRelease(user, myTags))
    }
  }

}
