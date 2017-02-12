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
import javax.inject.Inject

import logging.ApplicationLogging
import common.configuration.Users
import common.message.MessageTypes._
import common.message.{MessageService, Messaging}

import scala.sys.process._

/**
 * A device connection service that uses pmount and pumount to mount and unmount devices.
 * Created by alex on 16/11/14.
 */
class DeviceConnectionServiceImpl @Inject()(users: Users) extends DeviceConnectionService with Messaging with ApplicationLogging {

  /**
   * List the users who currently have devices connected to the system.
   * @return
   */
  override def listConnectedDevices(implicit messageService: MessageService): Set[Device] = {
    log(LOOKING_FOR_DEVICES())
    val devices: Set[Device] = for {
      user <- users.allUsers
      device <- user.devices if device.isConnected
    } yield device
    devices.log((device: Device) => FOUND_DEVICE(device))
  }

  override def unmount(path: Path): Unit = {
    Seq("pumount", path.toString) !< ProcessLogger(_ => {})
  }
}
