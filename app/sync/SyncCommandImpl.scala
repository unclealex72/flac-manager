package sync

import common.commands.CommandService
import common.configuration.Directories
import common.files.{DeviceFileLocation, DirectoryService}
import common.message.MessageTypes._
import common.message.{MessageService, Messaging}

/**
 * Created by alex on 16/11/14.
 */
class SyncCommandImpl(
                       val deviceConnectionService: DeviceConnectionService,
                       val directoryService: DirectoryService,
                       val synchronisationManager: SynchronisationManager)(implicit val directories: Directories, val commandService: CommandService)
  extends SyncCommand with Messaging {

  override def synchronise(implicit messageService: MessageService): Unit = deviceConnectionService.listConnectedDevices.foreach { user =>
    log(SYNCHRONISING(user))
    val rootDeviceFileLocation = DeviceFileLocation(user)
    val allDeviceFiles = directoryService.listFiles(Some(rootDeviceFileLocation))
    val device = IpodDevice(user)
    synchronisationManager.synchronise(device, allDeviceFiles)
    log(DEVICE_SYNCHRONISED(user))
  }
}
