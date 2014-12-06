package initialise

import common.changes.{Change, ChangeDao}
import common.configuration.{Directories, Users}
import common.files.{DeviceFileLocation, DirectoryService, FileLocationExtensions}
import common.message.MessageTypes._
import common.message.{MessageService, Messaging}

import scala.collection.SortedSet

/**
 * Created by alex on 06/12/14.
 */
class InitialiseCommandImpl(val users: Users, val directoryService: DirectoryService)
                           (implicit val changeDao: ChangeDao, implicit val directories: Directories, val fileLocationExtensions: FileLocationExtensions) extends InitialiseCommand with Messaging {

  /**
   * Initialise the database with all device files.
   */
  override def initialiseDb(implicit messageService: MessageService): Unit = {
    if (changeDao.countChanges() != 0) {
      log(DATABASE_NOT_EMPTY())
    }
    else {
      users.allUsers.foreach { user =>
        val root = DeviceFileLocation(user)
        val deviceFileLocations: SortedSet[DeviceFileLocation] = directoryService.listFiles(Some(root))
        deviceFileLocations.foreach { deviceFileLocation =>
          log(INITIALISING(deviceFileLocation))
          Change.added(deviceFileLocation).store
        }
      }
    }
  }
}
