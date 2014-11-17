package sync

import java.nio.file.{Files, Path, Paths}

import common.configuration.{User, Users}
import common.message.MessageTypes._
import common.message.{MessageService, Messaging}

import scala.collection.JavaConversions._
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
    val connectedDeviceIds = Files.list(Paths.get("/dev", "disks", "by-uuid")).iterator().toSeq.map(_.getFileName.toString)
    val connectedUsers = users.allUsers.filter(user => connectedDeviceIds.contains(user.uuid)).toSet
    connectedUsers.foreach(user => log(FOUND_DEVICE(user)))
    connectedUsers
  }

  override def mount(uuid: String): Path = {
    Seq("pmount", uuid) !< ProcessLogger(_ => {})
    Paths.get("/media", uuid)
  }

  override def unmount(path: Path): Unit = {
    Seq("pumount", path.toString) !< ProcessLogger(_ => {})
  }
}
