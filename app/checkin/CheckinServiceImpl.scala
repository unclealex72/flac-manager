package checkin

import common.changes.{Change, ChangeDao}
import common.configuration.{Directories, User}
import common.files._
import common.message.MessageTypes._
import common.message.{MessageService, Messaging}
import common.music.{Tags, TagsService}

/**
 * Created by alex on 16/11/14.
 */
class CheckinServiceImpl(val fileUtils: FileUtils, changeDao: ChangeDao)
                        (implicit val fileLocationUtils: FileLocationUtils, val directories: Directories, val tagsService: TagsService, val mp3Encoder: Mp3Encoder)
  extends CheckinService with Messaging {

  def delete(location: StagedFlacFileLocation)(implicit messageService: MessageService): Unit = {
    fileUtils.remove(location)
  }

  def encode(stagedFlacFileLocation: StagedFlacFileLocation, flacFileLocation: FlacFileLocation, tags: Tags, users: Set[User])(implicit messageService: MessageService): Unit = {
    val tempEncodedLocation = TemporaryFileLocation.create()
    val encodedFileLocation = flacFileLocation.toEncodedFileLocation
    log(ENCODE(stagedFlacFileLocation, encodedFileLocation))
    stagedFlacFileLocation.encodeTo(tempEncodedLocation)
    tempEncodedLocation.writeTags(tags)
    fileUtils.move(tempEncodedLocation, encodedFileLocation)
    users.foreach { user =>
      val deviceFileLocation = encodedFileLocation.toDeviceFileLocation(user)
      fileUtils.link(encodedFileLocation, deviceFileLocation)
      changeDao.store(Change.added(deviceFileLocation))
    }
    fileUtils.move(stagedFlacFileLocation, flacFileLocation)
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
