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

package checkin

import javax.inject.Inject

import cats.data.Validated.Valid
import checkin.Action._
import common.commands.CommandType
import common.commands.CommandType._
import common.configuration.User
import common.files._
import common.message.MessageTypes._
import common.message._
import common.music.{Tags, TagsService}
import common.owners.OwnerService

import scala.collection.GenTraversableOnce

/**
  * The command that checks in flac files from the staging directory.
  * Created by alex on 12/11/14.
 */
class CheckinCommandImpl @Inject()(
                          val directoryService: DirectoryService,
                          val ownerService: OwnerService,
                          val checkinService: CheckinService)(implicit val flacFileChecker: FlacFileChecker,
                                                              val tagsService: TagsService, val fileLocationExtensions: FileLocationExtensions)
  extends CheckinCommand with Messaging {

  override def checkin(locations: Seq[StagedFlacFileLocation])(implicit messageService: MessageService): CommandType = {
    val fileLocations = directoryService.listFiles(locations)
    val actions: Seq[Action] = validate(fileLocations)
    if (actions.size == fileLocations.size && actions.nonEmpty) {
      checkinService.checkin(actions)
    }
    else synchronous {
      if (actions.isEmpty) {
        log(NO_FILES(fileLocations.toSet))
      }
    }
  }

  def validate(fileLocations: Traversable[StagedFlacFileLocation])(implicit messageService: MessageService): Seq[Action] = {
    val firstStageValidationResults = fileLocations.map(isValidFlacFile)
    val secondStageValidationResults = firstStageValidationResults.par.flatMap(isFullyTaggedFlacFile).seq
    val thirdStageValidationResults = secondStageValidationResults.flatMap(doesNotOverwriteFlacFile)
    val fourthStageValidationResults = thirdStageValidationResults.foldLeft(Set.empty[FourthStageValidationResult])(isUniqueFlacFile)
    fourthStageValidationResults.foreach {
      case NonUniqueFlacFileMapping(fl, sfls) => log(NON_UNIQUE(fl, sfls))
      case _ =>
    }
    val hasOwners: Tags => Set[User] = ownerService.listCollections()
    val fifthStageValidationResults = fourthStageValidationResults.flatMap(isOwnedFlacFile(hasOwners))
    val actions: Set[Action] = fifthStageValidationResults.map(generateActions)
    actions.toSeq.sorted
  }

  def isValidFlacFile: (StagedFlacFileLocation) => FirstStageValidationResult = {
    fl =>
      if (fl.isFlacFile) FlacFileType(fl) else NonFlacFileType(fl)
  }

  def isFullyTaggedFlacFile(implicit messageService: MessageService): FirstStageValidationResult => GenTraversableOnce[SecondStageValidationResult] = {
    case NonFlacFileType(sfl) => Some(NonFlacFileType(sfl))
    case FlacFileType(sfl) =>
      sfl.readTags match {
        case Valid(tags) => Some(ValidFlacFile(sfl, sfl.toFlacFileLocation(tags), tags))
        case _ =>
          log(INVALID_FLAC(sfl))
          None
      }
  }

  def doesNotOverwriteFlacFile(implicit messageService: MessageService): SecondStageValidationResult => GenTraversableOnce[ThirdStageValidationResult] = {
    case NonFlacFileType(sfl) => Some(NonFlacFileType(sfl))
    case ValidFlacFile(sfl, fl, tags) =>
      if (fl.exists) {
        log(OVERWRITE(sfl, fl))
        None
      }
      else {
        Some(ValidFlacFile(sfl, fl, tags))
      }
  }

  def isUniqueFlacFile(fsvrs: Set[FourthStageValidationResult], tsvr: ThirdStageValidationResult): Set[FourthStageValidationResult] = {
    tsvr match {
      case NonFlacFileType(sfl) => fsvrs + NonFlacFileType(sfl)
      case ValidFlacFile(sfl, fl, tags) =>
        val possiblyAlreadyExistingMapping = fsvrs.find {
          case ValidFlacFile(_, ofl, _) => fl == ofl
          case NonUniqueFlacFileMapping(ofl, _) => fl == ofl
          case _ => false
        }
        possiblyAlreadyExistingMapping match {
          case Some(ValidFlacFile(osfl, ofl, myTags)) =>
            (fsvrs - ValidFlacFile(osfl, ofl, myTags)) + NonUniqueFlacFileMapping(fl, (sfl, osfl))
          case Some(NonUniqueFlacFileMapping(ofl, sfls)) =>
            (fsvrs - NonUniqueFlacFileMapping(ofl, sfls)) + (NonUniqueFlacFileMapping(ofl, sfls) + sfl)
          case _ => fsvrs + ValidFlacFile(sfl, fl, tags)
        }
    }
  }

  def isOwnedFlacFile(hasOwners: Tags => Set[User])(implicit messageService: MessageService): FourthStageValidationResult => GenTraversableOnce[FifthStageValidationResult] = {
    case ValidFlacFile(sfl, fl, tags) =>
      val owners = hasOwners(tags)
      if (owners.isEmpty) {
        log(NOT_OWNED(sfl))
        None
      }
      else {
        Some(OwnedFlacFile(sfl, fl, tags, owners))
      }
    case NonFlacFileType(sfl) => Some(NonFlacFileType(sfl))
    case NonUniqueFlacFileMapping(_, _) => None
  }

  def generateActions: FifthStageValidationResult => Action = {
    case OwnedFlacFile(sfl, fl, tags, owners) => Encode(sfl, fl, tags, owners)
    case NonFlacFileType(sfl) => Delete(sfl)
  }
}

sealed trait FirstStageValidationResult

sealed trait SecondStageValidationResult

sealed trait ThirdStageValidationResult

sealed trait FourthStageValidationResult

sealed trait FifthStageValidationResult

case class FlacFileType(stagedFileLocation: StagedFlacFileLocation)
  extends FirstStageValidationResult

case class NonFlacFileType(stagedFileLocation: StagedFlacFileLocation)
  extends FirstStageValidationResult with SecondStageValidationResult with ThirdStageValidationResult with FourthStageValidationResult with FifthStageValidationResult

case class ValidFlacFile(stagedFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags)
  extends SecondStageValidationResult with ThirdStageValidationResult with FourthStageValidationResult

case class NonUniqueFlacFileMapping(flacFileLocation: FlacFileLocation, stagedFileLocations: Set[StagedFlacFileLocation])
  extends FourthStageValidationResult {
  def +(stagedFlacFileLocation: StagedFlacFileLocation): NonUniqueFlacFileMapping =
    NonUniqueFlacFileMapping(flacFileLocation, stagedFileLocations + stagedFlacFileLocation)
}

object NonUniqueFlacFileMapping {
  def apply(flacFileLocation: FlacFileLocation, stagedFlacFileLocations: (StagedFlacFileLocation, StagedFlacFileLocation)): NonUniqueFlacFileMapping = {
    NonUniqueFlacFileMapping(flacFileLocation, Set(stagedFlacFileLocations._1, stagedFlacFileLocations._2))
  }
}

case class OwnedFlacFile(stagedFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags, owners: Set[User])
  extends FifthStageValidationResult