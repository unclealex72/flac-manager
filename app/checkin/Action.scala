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
