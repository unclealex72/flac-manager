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

package checkout

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import common.async.CommandExecutionContext
import common.files.Directory.FlacDirectory
import common.files._
import common.message.Messages._
import common.message.{Message, MessageService, Messaging}
import common.validation.SequentialValidation
import javax.inject.Inject

import scala.collection.{SortedMap, SortedSet}
import scala.concurrent.Future

/**
  * The default implementation of [[CheckoutCommand]]
  * @param repositories The location of all repositories.
  * @param checkoutService The [[CheckoutService]] used to actually check out flac files.
  * @param commandExecutionContext The execution context used to execute the command.
  */
class CheckoutCommandImpl @Inject()(
                                     val repositories: Repositories,
                                     val checkoutService: CheckoutService)
                         (implicit val commandExecutionContext: CommandExecutionContext)
  extends CheckoutCommand with Messaging with SequentialValidation {

  /**
    * @inheritdoc
    */
  override def checkout(directories: SortedSet[FlacDirectory], unown: Boolean)
                       (implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]] = {
    val empty: SortedMap[FlacDirectory, SortedSet[FlacFile]] = SortedMap.empty[FlacDirectory, SortedSet[FlacFile]]
    val groupedFlacFiles: SortedMap[FlacDirectory, SortedSet[FlacFile]] = directories.foldLeft(empty)(_ ++ _.group)
    val validatedFlacFiles: ValidatedNel[Message, Seq[FlacFile]] = checkFlacFilesDoNotOverwrite(groupedFlacFiles.values.flatten.toSeq)
    validatedFlacFiles match {
      case Valid(_) => checkoutService.checkout(groupedFlacFiles, unown).map(_ => Valid({}))
      case iv @ Invalid(_) => Future.successful(iv)
    }
  }

  /**
    * Check that a sequence of flac files would not overwrite existing files in the staging repository if they
    * were to be checked out.
    * @param fileLocations The sequence of flac file locations to check.
    * @return A list of flac file locations that wouldn't overwrite a staged file or [[OVERWRITE]] for those that do.
    */
  def checkFlacFilesDoNotOverwrite(fileLocations: Seq[FlacFile]): ValidatedNel[Message, Seq[FlacFile]] = {
    runValidation(fileLocations) { flacFile =>
      val stagingFile: StagingFile = flacFile.toStagingFile
      if (stagingFile.exists) {
        Validated.invalid(OVERWRITE(flacFile, stagingFile))
      }
      else {
        Validated.valid(flacFile)
      }
    }
  }

}
