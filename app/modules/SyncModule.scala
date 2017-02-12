package modules

import com.google.inject.AbstractModule
import common.configuration.{DeviceLocator, DeviceLocatorImpl}
import net.codingwell.scalaguice.ScalaModule
import sync._

class SyncModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[DeviceConnectionService].to[DeviceConnectionServiceImpl].asEagerSingleton()
    bind[SynchronisationManager].to[SynchronisationManagerImpl].asEagerSingleton()
    bind[FilesystemDeviceFactory].to[FilesystemDeviceFactoryImpl].asEagerSingleton()
    bind[IpodDeviceFactory].to[IpodDeviceFactoryImpl].asEagerSingleton()
    bind[DeviceLocator].to[DeviceLocatorImpl].asEagerSingleton()
  }
}

