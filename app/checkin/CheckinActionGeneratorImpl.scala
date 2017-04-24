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
import javax.inject.Inject

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import cats.implicits._
import common.configuration.User
import common.files.{FileLocationExtensions, FlacFileChecker, FlacFileLocation, StagedFlacFileLocation}
import common.message.Message
import common.message.Messages._
import common.music.{Tags, TagsService}
import common.owners.OwnerService

/**
  * The default implementation of [[CheckinActionGenerator]]
  **/
class CheckinActionGeneratorImpl @Inject()(val ownerService: OwnerService)
                                          (implicit val flacFileChecker: FlacFileChecker,
                                     val tagsService: TagsService,
                                     val fileLocationExtensions: FileLocationExtensions) extends CheckinActionGenerator {

  /**
    * @inheritdoc
    */
  override def generate(stagedFlacFileLocations: Seq[StagedFlacFileLocation]): ValidatedNel[Message, Seq[Action]] = {
    val (flacFileLocations, nonFlacFileLocations) = partitionFlacAndNonFlacFiles(stagedFlacFileLocations)
    // Validate the flac files only as non flac files just get deleted.
    validate(flacFileLocations).map(_.map(_.toAction)).map { actions =>
      actions ++ nonFlacFileLocations.map(Delete)
    }
  }

  /**
    * Validate a sequence of staged flac files.
    *
    * @param fileLocations The staged flac files to check.
    * @return A [[ValidatedNel]] that contains either a sequence of [[OwnedFlacFile]]s or a non-empty list
    *         of [[Message]]s to log in the case of failure.
    */
  def validate(fileLocations: Seq[StagedFlacFileLocation]): ValidatedNel[Message, Seq[OwnedFlacFile]] = {
    val validatedValidFlacFiles =
      checkThereAreSomeFiles(fileLocations).andThen(checkFullyTaggedFlacFiles)
    validatedValidFlacFiles.andThen { validFlacFiles =>
      (checkDoesNotOverwriteExistingFlacFile(validFlacFiles) |@|
        checkTargetFlacFilesAreUnique(validFlacFiles) |@|
        checkFlacFilesAreOwned(validFlacFiles)).map((_, _, ownedFlacFiles) => ownedFlacFiles)
    }
  }

  def partitionFlacAndNonFlacFiles(fileLocations: Seq[StagedFlacFileLocation]): (Seq[StagedFlacFileLocation], Seq[StagedFlacFileLocation]) = {
    fileLocations.partition(_.isFlacFile)
  }

  def checkThereAreSomeFiles(fileLocations: Seq[StagedFlacFileLocation]): ValidatedNel[Message, Seq[StagedFlacFileLocation]] = {
    if (fileLocations.isEmpty) {
      Validated.invalidNel(NO_FILES(fileLocations.toSet))
    }
    else {
      Validated.valid(fileLocations)
    }
  }

  def checkFullyTaggedFlacFiles(fileLocations: Seq[StagedFlacFileLocation]): ValidatedNel[Message, Seq[ValidFlacFile]] = {
    runValidation(fileLocations) { fileLocation =>
      fileLocation.readTags match {
        case Valid(tags) => Validated.valid(ValidFlacFile(fileLocation, fileLocation.toFlacFileLocation(tags), tags))
        case Invalid(_) => Validated.invalid(INVALID_FLAC(fileLocation))
      }
    }
  }

  def checkDoesNotOverwriteExistingFlacFile(validFlacFiles: Seq[ValidFlacFile]): ValidatedNel[Message, Seq[ValidFlacFile]] = {
    runValidation(validFlacFiles) { validFlacFile =>
      if (!validFlacFile.flacFileLocation.exists) {
        Validated.valid(validFlacFile)
      }
      else {
        Validated.invalid(OVERWRITE(validFlacFile.stagedFileLocation, validFlacFile.flacFileLocation))
      }
    }
  }

  def checkTargetFlacFilesAreUnique(validFlacFiles: Seq[ValidFlacFile]): ValidatedNel[Message, Seq[ValidFlacFile]] = {
    val (uniqueMappings, nonUniqueMappings) =
      validFlacFiles.groupBy(_.flacFileLocation).partition(kv => kv._2.size == 1)
    val uniqueFlacFiles: ValidatedNel[Message, Seq[ValidFlacFile]] =  Validated.valid(uniqueMappings.values.flatten.toSeq)
    val nonUniqueFlacFiles = runValidation(nonUniqueMappings.toSeq) {
      case (flacFileLocation, nonUniqueValidFlacFiles) =>
        Validated.invalid(NON_UNIQUE(flacFileLocation, nonUniqueValidFlacFiles.map(_.stagedFileLocation).toSet))
    }
    (uniqueFlacFiles |@| nonUniqueFlacFiles).map((uffs, _) => uffs)
  }

  def checkFlacFilesAreOwned(validFlacFiles: Seq[ValidFlacFile]): ValidatedNel[Message, Seq[OwnedFlacFile]] = {
    val hasOwners: Tags => Set[User] = ownerService.listCollections()
    runValidation(validFlacFiles) { validFlacFile =>
      val owners = hasOwners(validFlacFile.tags)
      if (owners.isEmpty) {
        Validated.invalid(NOT_OWNED(validFlacFile.stagedFileLocation))
      }
      else {
        Validated.valid(validFlacFile.ownedBy(owners))
      }
    }
  }

  def runValidation[A, B](as: Seq[A])(validationStep: A => Validated[Message, B]): ValidatedNel[Message, Seq[B]] = {
    val empty: ValidatedNel[Message, Seq[B]] = Valid(Seq.empty)
    as.foldLeft(empty) { (results, a) =>
      val result = validationStep(a).toValidatedNel
      (results |@| result).map(_ :+ _)
    }
  }

  case class ValidFlacFile(stagedFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags) {
    def ownedBy(owners: Set[User]): OwnedFlacFile =
      OwnedFlacFile(stagedFileLocation, flacFileLocation, tags, owners)
  }

  case class OwnedFlacFile(stagedFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags, owners: Set[User]) {
    def toAction: Action = {
      Encode(stagedFileLocation, flacFileLocation, tags, owners)
    }
  }

}
