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

package sync

import java.nio.file.Path
import java.text.SimpleDateFormat

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
    Seq(commandService.syncCommand, mountPoint.toString) run processCommunicator
  }

  override def listDeviceFiles: Set[DeviceFile] = {
    val df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    processCommunicator match {
      case Some(pc) => {
        pc.write("LIST")
        pc.read.foldLeft(Set.empty[DeviceFile]) { (deviceFiles, str) =>
          str match {
            case LIST_REGEX(id, relativePath, lastModified) => {
              deviceFiles + DeviceFile(id, relativePath, df.parse(lastModified).getTime())
            }
            case _ => deviceFiles
          }
        }
      }
      case _ => Set.empty
    }
  }

  override def remove(deviceFile: DeviceFile): Unit =
    singleLine("REMOVE", deviceFile.id)

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