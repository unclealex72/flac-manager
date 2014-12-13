package sync

import java.nio.file.Path

import common.configuration.{User, Users}
import common.message.MessageTypes._
import common.message.{MessageService, Messaging}

import scala.sys.process._

/**
 * A device connection service that uses pmount and pumount to mount and unmount devices.
 * Created by alex on 16/11/14.
 */
class DeviceConnectionServiceImpl(users: Users) extends DeviceConnectionService with Messaging {

  /**
   * List the users who currently have devices connected to the system.
   * @return
   */
  override def listConnectedDevices()(implicit messageService: MessageService): Set[User] = {
    log(LOOKING_FOR_DEVICES())
    val connectedUsers = users.allUsers.filter { user =>
      val mountPoint = user.mountPoint.toFile
      mountPoint.isDirectory && mountPoint.list().length != 0
    }.toSet
    connectedUsers.foreach(user => log(FOUND_DEVICE(user)))
    connectedUsers
  }

  override def unmount(path: Path): Unit = {
    Seq("pumount", path.toString) !< ProcessLogger(_ => {})
  }
}
