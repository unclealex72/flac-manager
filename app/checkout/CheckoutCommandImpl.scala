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

package checkout

import javax.inject.Inject

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import common.configuration.Directories
import common.files.{DirectoryService, FileLocationExtensions, FileSystem, FlacFileLocation}
import common.message.Messages._
import common.message.{Message, MessageService, Messaging}
import common.commands.CommandExecution
import common.commands.CommandExecution._
import common.validation.SequentialValidation

/**
  * The default implementation of [[CheckoutCommand]]
  * @param fileSystem The [[FileSystem]] used to manipulate files.
  * @param directoryService The [[DirectoryService]] used to list flac files.
  * @param checkoutService The [[CheckoutService]] used to actually check out flac files.
  * @param directories The location of all repositories.
  * @param fileLocationExtensions A typeclass used to add [[java.nio.file.Path]] like functionality to [[FlacFileLocation]]s.
  */
class CheckoutCommandImpl @Inject()(
                                     val fileSystem: FileSystem,
                                     val directoryService: DirectoryService,
                                     val checkoutService: CheckoutService)
                         (implicit val directories: Directories,
                          fileLocationExtensions: FileLocationExtensions)
  extends CheckoutCommand with Messaging with SequentialValidation {

  /**
    * @inheritdoc
    */
  override def checkout(locations: Seq[FlacFileLocation], unown: Boolean)
                       (implicit messageService: MessageService): CommandExecution = synchronous {
    val groupedFlacFileLocations = directoryService.groupFiles(locations)
    val flacFileLocations = groupedFlacFileLocations.values.flatten.toSeq
    checkFlacFilesDoNotOverwrite(flacFileLocations) match {
      case Valid(_) =>
        checkoutService.checkout(groupedFlacFileLocations, unown)
      case Invalid(errors) =>
        errors.toList.foreach(log)
    }
  }

  /**
    * Check that a sequence of flac files would not overwrite existing files in the staging repository if they
    * were to be checked out.
    * @param fileLocations The sequence of flac file locations to check.
    * @return A list of flac file locations that wouldn't overwrite a staged file or [[OVERWRITE]] for those that do.
    */
  def checkFlacFilesDoNotOverwrite(fileLocations: Seq[FlacFileLocation]): ValidatedNel[Message, Seq[FlacFileLocation]] = {
    runValidation(fileLocations) { fileLocation =>
      val stagedFlacFileLocation = fileLocation.toStagedFlacFileLocation
      if (stagedFlacFileLocation.exists) {
        Validated.invalid(OVERWRITE(fileLocation, stagedFlacFileLocation))
      }
      else {
        Validated.valid(fileLocation)
      }
    }
  }

}
