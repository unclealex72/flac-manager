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

import javax.inject.Inject

import common.commands.{CommandService, CommandType}
import common.commands.CommandType._
import common.configuration.Directories
import common.files.{DeviceFileLocation, DirectoryService}
import common.message.MessageTypes._
import common.message.{MessageService, Messaging}

/**
 * Created by alex on 16/11/14.
 */
class SyncCommandImpl @Inject()(
                       val deviceConnectionService: DeviceConnectionService,
                       val directoryService: DirectoryService,
                       val synchronisationManager: SynchronisationManager)(implicit val directories: Directories, val commandService: CommandService)
  extends SyncCommand with Messaging {

  override def synchronise(implicit messageService: MessageService): CommandType = synchronous {
    deviceConnectionService.listConnectedDevices.foreach { device =>
      log(SYNCHRONISING(device))
      val rootDeviceFileLocation = DeviceFileLocation(device.owner)
      val allDeviceFiles = directoryService.listFiles(Some(rootDeviceFileLocation))
      synchronisationManager.synchronise(device, allDeviceFiles)
      log(DEVICE_SYNCHRONISED(device))
    }
  }
}
