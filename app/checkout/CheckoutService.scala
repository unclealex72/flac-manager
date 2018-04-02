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

import common.files.Directory.FlacDirectory
import common.files.FlacFile
import common.message.MessageService

import scala.collection.{SortedMap, SortedSet}
import scala.concurrent.Future

/**
  * Checkout a sequence of [[FlacFile]]s.
  */
trait CheckoutService {

  /**
    * Checkout a sequence of flac files.
    * @param flacFileLocationsByParent A map of flac files grouped by their parent file.
    * @param unown True if checked out files should have their owners removed, false otherwise.
    * @param messageService The [[MessageService]] used to report progress and log errors.
    */
  def checkout(
                flacFileLocationsByParent: SortedMap[FlacDirectory, SortedSet[FlacFile]],
                unown: Boolean)(implicit messageService: MessageService): Future[Unit]
}
