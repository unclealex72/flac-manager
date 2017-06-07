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

import cats.data.NonEmptyList
import common.async.CommandExecutionContext
import common.configuration.User
import common.files.FileLocation._
import common.files._
import common.message.MessageService
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
                                directoryService: DirectoryService)
                              (implicit flacFileChecker: FlacFileChecker,
                               tagsService: TagsService,
                               fileLocationExtensions: FileLocationExtensions,
                               commandExecutionContext: CommandExecutionContext) extends OwnCommand {

  /**
    * @inheritdoc
    */
  override def changeOwnership(action: OwnAction,
                               users: Seq[User],
                               directoryLocations: Seq[Either[StagedFlacFileLocation, FlacFileLocation]])
                              (implicit messageService: MessageService): Future[_] = {
    val (stagedLocations, flacLocations) = {
      val empty: (SortedMap[String, Seq[StagedFlacFileLocation]], SortedMap[String, Seq[FlacFileLocation]]) =
        (SortedMap.empty[String, Seq[StagedFlacFileLocation]], SortedMap.empty[String, Seq[FlacFileLocation]])
      directoryLocations.foldLeft(empty){ (acc, location) =>
        val (stagedLocations, flacLocations) = acc
        location match {
          case Left(sfl) => (stagedLocations ++ childrenByAlbumId[StagedFlacFileLocation](sfl, _.isFlacFile), flacLocations)
          case Right(fl) => (stagedLocations, flacLocations ++ childrenByAlbumId[FlacFileLocation](fl, _ => true))
        }
      }
    }
    def executeChanges[FL <: FileLocation](locationsById: SortedMap[String, Seq[FL]],
                                           changer: (User, OwnAction, NonEmptyList[FL]) => Future[Unit]) = {
      val empty: Future[Unit] = Future.successful({})
      users.foldLeft(empty){(accA, user) =>
        accA.flatMap { _ =>
          locationsById.foldLeft(accA){ (accB, albumIdAndLocation) =>
            val (_, locations) = albumIdAndLocation
            accB.flatMap(_ => changer(user, action, NonEmptyList.fromListUnsafe(locations.toList)))
          }
        }
      }
    }
    for {
      _ <- executeChanges(stagedLocations, ownerService.changeStagedOwnership)
      _ <- executeChanges(flacLocations, ownerService.changeFlacOwnership)
    } yield {}
  }

  def childrenByAlbumId[FL <: FileLocation](fl: FL, filter: FL => Boolean)
                                           (implicit messageService: MessageService): Map[String, Seq[FL]] = {
    val fileLocations: SortedSet[FL] = directoryService.listFiles(Seq(fl)).filter(fl => filter(fl) && !fl.isDirectory)
    val fileLocationsByMaybeAlbumId: Map[Option[String], SortedSet[FL]] =
      fileLocations.groupBy(fl => fl.readTags.toOption.map(_.albumId))
    val empty: Map[String, Seq[FL]] = Map.empty
    fileLocationsByMaybeAlbumId.foldLeft(empty) { (fileLocationsByAlbumId, maybeAlbumIdAndFileLocation) =>
      maybeAlbumIdAndFileLocation match {
        case (Some(albumId), fls) => fileLocationsByAlbumId + (albumId -> fls.toSeq)
        case _ => fileLocationsByAlbumId
      }
    }
  }
}
