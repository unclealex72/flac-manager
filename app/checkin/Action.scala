package checkin

import common.configuration.User
import common.files.{FlacFileLocation, StagedFlacFileLocation}
import common.music.Tags

/**
 * Created by alex on 16/11/14.
 */
sealed trait Action

case class Encode(val stagedFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags, owners: Set[User]) extends Action

case class Delete(val stagedFileLocation: StagedFlacFileLocation) extends Action

object Action {

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