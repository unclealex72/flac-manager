package checkin

import common.configuration.{Directories, User}
import common.files._
import common.message.MessageService
import common.music.Tags

/**
 * Created by alex on 16/11/14.
 */
class CheckinServiceImpl(val fileUtils: FileUtils)(implicit val fileLocationUtils: FileLocationUtils, val directories: Directories)
  extends CheckinService {

  def delete(location: StagedFlacFileLocation)(implicit messageService: MessageService): Unit = {
    fileUtils.remove(location)
  }

  def encode(stagedFlacFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags, users: Set[User])(implicit messageService: MessageService): Unit = {
    val tfl = TemporaryFileLocation.create()
  }

  override def checkin(action: Action)(implicit messagingService: MessageService): Unit = {
    action match {
      case Delete(stagedFlacFileLocation) =>
        delete(stagedFlacFileLocation)
      case Encode(stagedFlacFileLocation, flacFileLocation, tags, owners) =>
        encode(stagedFlacFileLocation, flacFileLocation, tags, owners)
    }
  }
}
