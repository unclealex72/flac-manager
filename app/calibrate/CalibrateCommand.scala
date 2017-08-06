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

package calibrate

import cats.data.ValidatedNel
import common.files.Directory.StagingDirectory
import common.message.{Message, MessageService}

import scala.collection.SortedSet
import scala.concurrent.Future

/**
 * The command that tries to work out what the best concurrency level for encoding is.
 */
trait CalibrateCommand {

  /**
    * Encode files to try and find the best concurrency level for encoding.
    * @param messageService The [[MessageService]] used to report progress.
    * @return A [[Future]].
    */
  def calibrate(maybeHighestConcurrencyLevel: Option[Int])(implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]]

}
