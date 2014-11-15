/*
 * Copyright 2014 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package checkin

import common.configuration.User
import common.files.FileLocationImplicits._
import common.files._
import common.message.MessageTypes._
import common.message._
import common.music.{Tags, TagsService}
import common.owners.OwnerService

import scala.collection.{GenTraversableOnce, SortedSet}

/**
 * Created by alex on 12/11/14.
 */
class CheckinCommandImpl(
                          val directoryService: DirectoryService,
                          val flacFileChecker: FlacFileChecker,
                          val fileUtils: FileUtils,
                          val ownerService: OwnerService,
                          val tagsService: TagsService,
                          val checkinService: CheckinService) extends CheckinCommand with Messaging {

  override def checkin(locations: Seq[StagedFlacFileLocation])(implicit messageService: MessageService): Unit = {
    val fileLocations = directoryService.listFiles(locations)
    val actions: Set[Action] = validate(fileLocations)
    if (actions.size == fileLocations.size) {
      actions.foreach { action => checkinService.checkin(action)}
    }
  }

  def validate(fileLocations: SortedSet[StagedFlacFileLocation])(implicit messageService: MessageService): Set[Action] = {
    val firstStageValidationResults = fileLocations.map(isValidFlacFile)
    val secondStageValidationResults = firstStageValidationResults.par.flatMap(isFullyTaggedFlacFile).seq
    val thirdStageValidationResults = secondStageValidationResults.flatMap(doesNotOverwriteFlacFile)
    val fourthStageValidationResults = thirdStageValidationResults.foldLeft(Set.empty[FourthStageValidationResult])(isUniqueFlacFile)
    fourthStageValidationResults.foreach { fsvr =>
      fsvr match {
        case NonUniqueFlacFileMapping(fl, sfls) => log(NON_UNIQUE(fl, sfls))
        case _ => {}
      }
    }
    val hasOwners: Tags => Set[User] = ownerService.listCollections()
    val fifthStageValidationResults = fourthStageValidationResults.flatMap(isOwnedFlacFile(hasOwners))
    val actions: Set[Action] = fifthStageValidationResults.map(generateActions)
    actions
  }

  def isValidFlacFile: (StagedFlacFileLocation) => FirstStageValidationResult = {
    fl =>
      if (flacFileChecker.isFlacFile(fl)) FlacFileType(fl) else NonFlacFileType(fl)
  }

  def isFullyTaggedFlacFile(implicit messageService: MessageService): FirstStageValidationResult => GenTraversableOnce[SecondStageValidationResult] = {
    fsvr =>
      fsvr match {
        case NonFlacFileType(sfl) => Some(NonFlacFileType(sfl))
        case FlacFileType(sfl) => {
          tagsService.readAndValidate(sfl) match {
            case Left(violations) => {
              log(INVALID_FLAC(sfl))
              None
            }
            case Right(tags) => Some(ValidFlacFile(sfl, sfl.toFlacFileLocation(tags), tags))
          }
        }
      }
  }

  def doesNotOverwriteFlacFile(implicit messageService: MessageService): SecondStageValidationResult => GenTraversableOnce[ThirdStageValidationResult] = {
    ssvr =>
      ssvr match {
        case NonFlacFileType(sfl) => Some(NonFlacFileType(sfl))
        case ValidFlacFile(sfl, fl, tags) => {
          if (fileUtils.exists(fl)) {
            log(OVERWRITE(sfl, fl))
            None
          }
          else {
            Some(ValidFlacFile(sfl, fl, tags))
          }
        }
      }
  }

  def isUniqueFlacFile(fsvrs: Set[FourthStageValidationResult], tsvr: ThirdStageValidationResult): Set[FourthStageValidationResult] = {
    tsvr match {
      case NonFlacFileType(sfl) => fsvrs + NonFlacFileType(sfl)
      case ValidFlacFile(sfl, fl, tags) => {
        val possiblyAlreadyExstingMapping = fsvrs.find { fsvr =>
          fsvr match {
            case ValidFlacFile(osfl, ofl, tags) => fl == ofl
            case NonUniqueFlacFileMapping(ofl, osfls) => fl == ofl
            case _ => false
          }
        }
        possiblyAlreadyExstingMapping match {
          case Some(ValidFlacFile(osfl, ofl, tags)) => {
            (fsvrs - ValidFlacFile(osfl, ofl, tags)) + NonUniqueFlacFileMapping(fl, (sfl, osfl))
          }
          case Some(NonUniqueFlacFileMapping(ofl, sfls)) => {
            (fsvrs - NonUniqueFlacFileMapping(ofl, sfls)) + (NonUniqueFlacFileMapping(ofl, sfls) + sfl)
          }
          case _ => fsvrs + ValidFlacFile(sfl, fl, tags)
        }
      }
    }
  }

  def isOwnedFlacFile(hasOwners: Tags => Set[User])(implicit messageService: MessageService): FourthStageValidationResult => GenTraversableOnce[FifthStageValidationResult] = { fsvr =>
    fsvr match {
      case ValidFlacFile(sfl, fl, tags) => {
        val owners = hasOwners(tags)
        if (owners.isEmpty) {
          log(NOT_OWNED(sfl))
          None
        }
        else {
          Some(OwnedFlacFile(sfl, fl, tags, owners))
        }

      }
      case NonFlacFileType(sfl) => Some(NonFlacFileType(sfl))
      case NonUniqueFlacFileMapping(fl, sfls) => None
    }

  }

  def generateActions: FifthStageValidationResult => Action = {
    fsvr =>
      fsvr match {
        case OwnedFlacFile(sfl, fl, tags, owners) => Encode(sfl, fl, tags, owners)
        case NonFlacFileType(sfl) => Delete(sfl)
      }
  }
}

sealed trait FirstStageValidationResult

sealed trait SecondStageValidationResult

sealed trait ThirdStageValidationResult

sealed trait FourthStageValidationResult

sealed trait FifthStageValidationResult

case class FlacFileType(val stagedFileLocation: StagedFlacFileLocation)
  extends FirstStageValidationResult

case class NonFlacFileType(val stagedFileLocation: StagedFlacFileLocation)
  extends FirstStageValidationResult with SecondStageValidationResult with ThirdStageValidationResult with FourthStageValidationResult with FifthStageValidationResult

case class ValidFlacFile(val stagedFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags)
  extends SecondStageValidationResult with ThirdStageValidationResult with FourthStageValidationResult

case class NonUniqueFlacFileMapping(val flacFileLocation: FlacFileLocation, stagedFileLocations: Set[StagedFlacFileLocation])
  extends FourthStageValidationResult {
  def +(stagedFlacFileLocation: StagedFlacFileLocation): NonUniqueFlacFileMapping =
    NonUniqueFlacFileMapping(flacFileLocation, stagedFileLocations + stagedFlacFileLocation)
}

object NonUniqueFlacFileMapping {
  def apply(flacFileLocation: FlacFileLocation, stagedFlacFileLocations: (StagedFlacFileLocation, StagedFlacFileLocation)): NonUniqueFlacFileMapping = {
    NonUniqueFlacFileMapping(flacFileLocation, Set(stagedFlacFileLocations._1, stagedFlacFileLocations._2))
  }
}

case class OwnedFlacFile(val stagedFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags, owners: Set[User])
  extends FifthStageValidationResult

sealed trait Action

case class Encode(val stagedFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags, owners: Set[User]) extends Action

case class Delete(val stagedFileLocation: StagedFlacFileLocation) extends Action
