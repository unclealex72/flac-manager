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

import cats.data.ValidatedNel
import common.files.StagingFile
import common.message.{Message, MessageService}

import scala.concurrent.Future

/**
  * Given a list of [[StagingFile]]s, generate a list of [[Action]]s to check them in if they obey the
  * following rules:
  *
  *  - Non-flac files are deleted.
  *  - At least one file must be a flac file.
  *  - All flac files must be fully tagged with at least an album, artist, track number, title, cover art and
  *    [[http://www.musicbrainz.org MusicBrainz]] IDs.
  *  - No flac file can overwrite an existing flac file.
  *  - No two staged flac files can resolve to the same file in the flac repository.
  *  - All flac files must have at least one owner.
  *
  **/
trait CheckinActionGenerator {

  /**
    * Generate actions and validate staged flac file locations as described above.
    * @param stagingFiles The sequence of staged flac files to first validate and then generate the
    *                                checkin actions for.
    *@param allowUnowned True if unowned files can be checked in, false otherwise.
    * @return A [[ValidatedNel]] that contains either the sequence of actions or a non-empty list of error log
    *         messages.
    */
  def generate(stagingFiles: Seq[StagingFile],
               allowUnowned: Boolean)(implicit messageService: MessageService): Future[ValidatedNel[Message, Seq[Action]]]
}
