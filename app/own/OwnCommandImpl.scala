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

package own

import javax.inject.Inject

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}
import cats.implicits._
import common.async.CommandExecutionContext
import common.configuration.User
import common.files.Directory.{FlacDirectory, StagingDirectory}
import common.files._
import common.message.{Message, MessageService}
import common.music.TagsService
import common.owners.OwnerService

import scala.collection.{SortedMap, SortedSet}
import scala.concurrent.Future

/**
  * The default implementation of [[OwnCommand]]
  * @param ownerService The [[OwnerService]] used to change ownership.
  * @param directoryService The [[DirectoryService]] used to list files.
  * @param flacFileChecker The [[FlacFileChecker]] used to check all files are valid FLAC files.
  * @param tagsService The [[TagsService]] used to read audio information from FLAC files.
  * @param ec The execution context used to execute the command.
  */
class OwnCommandImpl @Inject()(
                                ownerService: OwnerService,
                                repositories: Repositories)
                              (implicit commandExecutionContext: CommandExecutionContext) extends OwnCommand {

  /**
    * @inheritdoc
    */
  override def changeOwnership(action: OwnAction,
                               users: SortedSet[User],
                               directories: SortedSet[Either[StagingDirectory, FlacDirectory]])
                              (implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]] = {
    case class StagingOrFlacFiles(
      stagingFiles: SortedMap[String, Seq[StagingFile]] = SortedMap.empty,
      flacFiles: SortedMap[String, Seq[FlacFile]] = SortedMap.empty) {
      def +(stagingOrFlacFiles: StagingOrFlacFiles): StagingOrFlacFiles =
        StagingOrFlacFiles(stagingFiles ++ stagingOrFlacFiles.stagingFiles, flacFiles ++ stagingOrFlacFiles.flacFiles)
    }

    object StagingOrFlacFiles {

      def staging(stagingFiles: SortedMap[String, Seq[StagingFile]]): StagingOrFlacFiles =
        StagingOrFlacFiles(stagingFiles = stagingFiles)

      def flac(flacFiles: SortedMap[String, Seq[FlacFile]]): StagingOrFlacFiles =
        StagingOrFlacFiles(flacFiles = flacFiles)
    }

    def executeChanges[FL <: File](filesByAlbumId: SortedMap[String, Seq[FL]],
                                           changer: (User, OwnAction, NonEmptyList[FL]) => Future[ValidatedNel[Message, Unit]]) = {
      val empty: Future[ValidatedNel[Message, Unit]] = Future.successful(Valid({}))
      users.foldLeft(empty){(accA, user) =>
        accA.flatMap { _ =>
          filesByAlbumId.foldLeft(accA){ (accB, albumIdAndFiles) =>
            val (_, files) = albumIdAndFiles
            accB.flatMap(_ => changer(user, action, NonEmptyList.fromListUnsafe(files.toList)))
          }
        }
      }
    }

    val validatedStagingOrFlacFiles = {
      val empty: ValidatedNel[Message, StagingOrFlacFiles] = Valid(StagingOrFlacFiles())
      directories.foldLeft(empty){ (acc, directory) =>
        val validatedNewStagingOrFlacFiles = directory match {
          case Left(sfl) => childrenByAlbumId[StagingFile](sfl, _.isFlacFile).map(StagingOrFlacFiles.staging)
          case Right(fl) => childrenByAlbumId[FlacFile](fl, _ => true).map(StagingOrFlacFiles.flac)
        }
        (acc |@| validatedNewStagingOrFlacFiles).map(_ + _)
      }
    }
    validatedStagingOrFlacFiles match {
      case Valid(stagingOrFlacFiles) =>
        for {
          validatedStagingChanges <- executeChanges(stagingOrFlacFiles.stagingFiles, ownerService.changeStagingOwnership)
          validatedFlacChanges <- executeChanges(stagingOrFlacFiles.flacFiles, ownerService.changeFlacOwnership)
        } yield (validatedStagingChanges |@| validatedFlacChanges).map((_,_) => {})
      case iv @ Invalid(_) =>
        Future.successful(iv)
    }
  }

  def childrenByAlbumId[FL <: File](directory: Directory[FL], filter: FL => Boolean)
                                           (implicit messageService: MessageService): ValidatedNel[Message, SortedMap[String, Seq[FL]]] = {
    val files: SortedSet[FL] = directory.list.filter(filter)
    val empty: ValidatedNel[Message, SortedMap[String, Seq[FL]]] = Valid(SortedMap.empty)
    files.foldLeft(empty) { (acc, file) =>
      (acc |@| file.tags.read).map { (filesByAlbumId, tags) =>
        val albumId = tags.albumId
        val filesForAlbumId = filesByAlbumId.getOrElse(albumId, Seq.empty)
        filesByAlbumId + (albumId -> (filesForAlbumId :+ file))
      }
    }
  }
}
