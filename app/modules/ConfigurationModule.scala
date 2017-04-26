package modules

import com.google.inject.AbstractModule
import common.configuration._
import net.codingwell.scalaguice.ScalaModule

/**
  * Dependency injection for configuration.
  */
class ConfigurationModule extends AbstractModule with ScalaModule {

  /**
    * @inheritdoc
    */
  override def configure(): Unit = {
    bind[UserDao].to[FileSystemUserDao].asEagerSingleton()
    bind[Directories].to[PlayConfigurationDirectories].asEagerSingleton()
  }
}

