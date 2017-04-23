package modules

import com.google.inject.AbstractModule
import controllers.{CommandBuilder, CommandBuilderImpl}
import net.codingwell.scalaguice.ScalaModule

class ControllersModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[CommandBuilder].to[CommandBuilderImpl].asEagerSingleton()
  }
}

