package modules

import com.google.inject.AbstractModule
import controllers.{CommandBuilder, CommandBuilderImpl}
import net.codingwell.scalaguice.ScalaModule

/**
  * Dependency injection for controllers.
  */
class ControllersModule extends AbstractModule with ScalaModule {

  /**
    * @inheritdoc
    */
  override def configure(): Unit = {
    bind[CommandBuilder].to[CommandBuilderImpl].asEagerSingleton()
  }
}

