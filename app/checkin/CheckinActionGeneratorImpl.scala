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

package checkin
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits._
import common.async.CommandExecutionContext
import common.configuration.User
import common.files.{FlacFile, StagingFile}
import common.message.Messages._
import common.message.{Message, MessageService}
import common.multi.AllowMultiService
import common.music.Tags
import common.owners.OwnerService
import common.validation.SequentialValidation
import javax.inject.Inject

import scala.concurrent.Future

/**
  * The default implementation of [[CheckinActionGenerator]]
  **/
class CheckinActionGeneratorImpl @Inject()(
                                            val ownerService: OwnerService,
                                            val allowMultiService: AllowMultiService)
                                          (implicit val commandExecutionContext: CommandExecutionContext)
  extends CheckinActionGenerator with SequentialValidation {

  /**
    * @inheritdoc
    */
  override def generate(stagedFlacFiles: Seq[StagingFile],
                        allowUnowned: Boolean)(implicit messageService: MessageService): Future[ValidatedNel[Message, Seq[Action]]] = {
    val eventualUsersByAlbumId: Future[Map[String, Set[User]]] = ownerService.listOwners()
    eventualUsersByAlbumId.map { usersByAlbumId =>
      val (flacFiles, nonFlacFiles) = partitionFlacAndNonFlacFiles(stagedFlacFiles)
      // Validate the flac files only as non flac files just get deleted.
      validate(flacFiles, allowUnowned, usersByAlbumId).map(_.map(_.toAction)).map { actions =>
        actions ++ nonFlacFiles.map(Delete)
      }
    }
  }

  /**
    * Validate a sequence of staged flac files.
    *
    * @param files The staged flac files to check.
    * @param allowUnowned True if unowned files are allowed to be checked in, false otherwise.
    * @param usersByAlbumId A list of users for each album.
    * @return A [[ValidatedNel]] that contains either a sequence of [[OwnedFlacFile]]s or a non-empty list
    *         of [[Message]]s to log in the case of failure.
    */
  def validate(files: Seq[StagingFile],
               allowUnowned: Boolean, usersByAlbumId: Map[String, Set[User]])(implicit messageService: MessageService): ValidatedNel[Message, Seq[OwnedFlacFile]] = {
    val validatedValidFlacFiles: Validated[NonEmptyList[Message], Seq[ValidFlacFile]] =
      checkThereAreSomeFiles(files).andThen(checkFullyTaggedFlacFiles)
    validatedValidFlacFiles.andThen { validFlacFiles =>
      (checkDoesNotOverwriteExistingFlacFile(validFlacFiles) |@|
        checkTargetFlacFilesAreUnique(validFlacFiles) |@|
        checkForMultiDisc(validFlacFiles) |@|
        checkFlacFilesAreOwned(validFlacFiles, allowUnowned, usersByAlbumId)).map((_, _, _, ownedFlacFiles) => ownedFlacFiles)
    }
  }

  /**
    * Partition a set of [[StagingFile]]s into those that start with a flac magic number and those that don't.
    * @param files The file locations to partition.
    * @return A [[Tuple2]] that contains a sequence of flac files and a sequence of non-flac files.
    */
  def partitionFlacAndNonFlacFiles(files: Seq[StagingFile]): (Seq[StagingFile], Seq[StagingFile]) = {
    files.partition(_.isFlacFile)
  }

  /**
    * Make sure that there is at least one flac file.
    * @param files The file locations to check.
    * @return The file locations if it is not empty or [[NO_FILES]] otherwise.
    */
  def checkThereAreSomeFiles(files: Seq[StagingFile]): ValidatedNel[Message, Seq[StagingFile]] = {
    if (files.isEmpty) {
      Validated.invalidNel(NO_FILES(files.toSet))
    }
    else {
      Validated.valid(files)
    }
  }

  /**
    * Make sure that all flac files are fully tagged.
    * @param files The file locations to check.
    * @return A [[ValidFlacFile]] for each fully tagged flac file and [[INVALID_FLAC]] for each non-fully tagged
    *         flac file.
    */
  def checkFullyTaggedFlacFiles(files: Seq[StagingFile])(implicit messageService: MessageService): ValidatedNel[Message, Seq[ValidFlacFile]] = {
    runValidationNel(files) { file =>
      file.toFlacFileAndTags.map { case (flacFile, tags) => ValidFlacFile(file, flacFile, tags) }
    }
  }

  /**
    * Make sure that no flac file overwrites a file that already exists in the flac repository.
    * @param validFlacFiles The fully tagged flac files to check.
    * @return The [[ValidFlacFile]]s that do not overwrite an existing flac file or [[OVERWRITE]] for each one
    *         that does.
    */
  def checkDoesNotOverwriteExistingFlacFile(validFlacFiles: Seq[ValidFlacFile]): ValidatedNel[Message, Seq[ValidFlacFile]] = {
    runValidation(validFlacFiles) { validFlacFile =>
      if (!validFlacFile.flacFile.exists) {
        Validated.valid(validFlacFile)
      }
      else {
        Validated.invalid(OVERWRITE(validFlacFile.stagedFile, validFlacFile.flacFile))
      }
    }
  }

  /**
    * Make sure that no two [[ValidFlacFile]]s resolve to the same file in the flac repository.
    * @param validFlacFiles The set of valid flac files to check.
    * @return The sequence of valid flac files that are unique or [[NON_UNIQUE]] for each one that isn't.
    */
  def checkTargetFlacFilesAreUnique(validFlacFiles: Seq[ValidFlacFile]): ValidatedNel[Message, Seq[ValidFlacFile]] = {
    val (uniqueMappings, nonUniqueMappings) =
      validFlacFiles.groupBy(_.flacFile).partition(kv => kv._2.size == 1)
    val uniqueFlacFiles: ValidatedNel[Message, Seq[ValidFlacFile]] =  Validated.valid(uniqueMappings.values.flatten.toSeq)
    val nonUniqueFlacFiles: ValidatedNel[Message, Seq[Nothing]] = runValidation(nonUniqueMappings.toSeq) {
      case (flacFile, nonUniqueValidFlacFiles) =>
        Validated.invalid(NON_UNIQUE(flacFile, nonUniqueValidFlacFiles.map(_.stagedFile)))
    }
    (uniqueFlacFiles |@| nonUniqueFlacFiles).map((uffs, _) => uffs)
  }

  /**
    * Make sure that if albums with multiple discs are not allowed then no files have a disc number greater than 1.
    * @param validFlacFiles The set of valid flac files to check.
    * @return The sequence of valid flac files that non-multi disc or [[MULTI_DISC]] for each one that isn't.
    */
  def checkForMultiDisc(validFlacFiles: Seq[ValidFlacFile]): ValidatedNel[Message, Seq[ValidFlacFile]] = {
    val maybeMultiDiscFlacFiles: Option[NonEmptyList[ValidFlacFile]] = for {
      _ <- Some(()).filterNot(_ => allowMultiService.allowMulti) // Short circuit if multi discs are allowed
      multiDiscFlacFiles <- NonEmptyList.fromList(
        validFlacFiles.filter(vff => vff.tags.discNumber > 1).toList)
    } yield {
      multiDiscFlacFiles
    }
    maybeMultiDiscFlacFiles match {
      case Some(multiDiscFlacFiles) =>
        Validated.invalid(multiDiscFlacFiles.map(vff => MULTI_DISC(vff.stagedFile)))
      case None =>
        Validated.valid(validFlacFiles)
    }
  }

  /**
    * Make sure that each flac file has at least one owner if unowned flac files are not allowed.
    * @param validFlacFiles The [[ValidFlacFile]]s to check.
    * @return A sequence of [[OwnedFlacFile]]s or [[NOT_OWNED]] for those that have no owner.
    */
  def checkFlacFilesAreOwned(validFlacFiles: Seq[ValidFlacFile],
                             allowUnowned: Boolean, usersByAlbumId: Map[String, Set[User]]): ValidatedNel[Message, Seq[OwnedFlacFile]] = {
    runValidation(validFlacFiles) { validFlacFile =>
      val owners: Set[User] = usersByAlbumId.getOrElse(validFlacFile.tags.albumId, Set.empty)
      if (owners.isEmpty && !allowUnowned) {
        Validated.invalid(NOT_OWNED(validFlacFile.stagedFile))
      }
      else {
        Validated.valid(validFlacFile.ownedBy(owners))
      }
    }
  }

  /**
    * A holder for all the information about a valid flac file.
    * @param stagedFile The location of the flac file in the staging repository.
    * @param flacFile The location of where the flac file will be in the flac repository.
    * @param tags The audio information stored in the flac file.
    */
  case class ValidFlacFile(stagedFile: StagingFile, flacFile: FlacFile, tags: Tags) {

    /**
      * Promote this valid flac file in to an owned flac file.
      * @param owners The set of users who own this flac file.
      * @return An [[OwnedFlacFile]] that additionally contains this flac file's owners.
      */
    def ownedBy(owners: Set[User]): OwnedFlacFile =
      OwnedFlacFile(stagedFile, flacFile, tags, owners)
  }

  /**
    * A holder for all the information about a valid flac file and who owns it.
    * @param stagedFile The location of the flac file in the staging repository.
    * @param flacFile The location of where the flac file will be in the flac repository.
    * @param tags The audio information stored in the flac file.
    * @param owners The owners of this flac file.
    */
  case class OwnedFlacFile(
                            stagedFile: StagingFile,
                            flacFile: FlacFile,
                            tags: Tags,
                            owners: Set[User]) {

    /**
      * Convert this flac file into an [[Encode]] action.
      * @return An [[Encode]] action that can be used to encode this flac file.
      */
    def toAction: Action = {
      Encode(stagedFile, flacFile, tags, owners)
    }
  }

}
