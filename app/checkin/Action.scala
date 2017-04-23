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

import common.configuration.User
import common.files.{FlacFileLocation, StagedFlacFileLocation}
import common.music.Tags

/**
  * Describes a change to be made to the music repository.
  * Created by alex on 16/11/14.
  */
sealed trait Action

/**
  * A file to be encoded
  * @param stagedFileLocation The source file location.
  * @param flacFileLocation The target file location.
  * @param tags The file's music tags.
  * @param owners The file's owners.
  */
case class Encode(stagedFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags, owners: Set[User]) extends Action

/**
  * A file to be deleted
  * @param stagedFileLocation The source file location.
  */
case class Delete(stagedFileLocation: StagedFlacFileLocation) extends Action

/**
  * Used to declare an implicit ordering on actions.
  */
object Action {

  /**
    * An ordering that always make sure deletions are first and encodes are sorted by track.
    */
  implicit val actingOrdering: Ordering[Action] = new Ordering[Action]() {
    override def compare(x: Action, y: Action): Int = (x, y) match {
      case (Encode(_, _, _, _), Delete(_)) => -1
      case (Delete(_), Encode(_, _, _, _)) => 1
      case (Delete(l), Delete(r)) =>
        Ordering.by((s: StagedFlacFileLocation) => s.relativePath.toString).compare(l, r)
      case (Encode(_, _, l, _), Encode(_, _, r, _)) =>
        Ordering.by((t: Tags) => (t.albumArtistSort, t.album, t.discNumber, t.trackNumber)).compare(l, r)
    }
  }
}