package sync

import java.nio.file.Path

import common.commands.{CommandService, ProcessCommunicator}
import common.configuration.User
import common.files.DeviceFileLocation

import scala.sys.process._

/**
 * Created by alex on 16/11/14.
 */
class IpodDevice(override val mountPoint: Path, val commandService: CommandService) extends Device {

  val LIST_REGEX = """(.+)\|(.+)\|(.+)""".r

  var processCommunicator: Option[ProcessCommunicator] = None

  override def afterMount: Unit = {
    val processCommunicator: ProcessCommunicator = ProcessCommunicator()
    this.processCommunicator = Some(processCommunicator)
    Seq(commandService.syncCommand, mountPoint.toString.toString) run processCommunicator
  }

  override def listDeviceFiles: Set[DeviceFile] = {
    processCommunicator match {
      case Some(pc) => {
        pc.write("LIST")
        pc.read.foldLeft(Set.empty[DeviceFile]) { (deviceFiles, str) =>
          str match {
            case LIST_REGEX(id, relativePath, lastModified) => {
              deviceFiles + DeviceFile(id, relativePath, lastModified.toLong)
            }
            case _ => deviceFiles
          }
        }
      }
      case _ => Set.empty
    }
  }

  override def remove(deviceFile: DeviceFile): Unit =
    singleLine("REMOVE", deviceFile.relativePath)

  override def add(deviceFileLocation: DeviceFileLocation): Unit =
    singleLine("ADD", deviceFileLocation.relativePath, deviceFileLocation.path)

  def singleLine(parts: Any*): Unit = processCommunicator.foreach { pc =>
    pc.write(parts.mkString("|"))
    pc.read
  }

  override def beforeUnmount: Unit = {
    processCommunicator.foreach { pc =>
      pc.write("QUIT")
      pc.close
    }
  }

  override def afterUnmount: Unit = {}

}

object IpodDevice {
  def apply(user: User)(implicit commandService: CommandService): IpodDevice = new IpodDevice(user.mountPoint, commandService)
}