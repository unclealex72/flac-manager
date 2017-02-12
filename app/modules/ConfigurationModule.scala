package modules

import com.google.inject.AbstractModule
import common.configuration._
import net.codingwell.scalaguice.ScalaModule

class ConfigurationModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Users].to[PlayConfigurationUsers].asEagerSingleton()
    bind[Directories].to[PlayConfigurationDirectories].asEagerSingleton()
  }
}

