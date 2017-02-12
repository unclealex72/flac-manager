package modules

import com.google.inject.AbstractModule
import controllers.{ParameterBuilders, ParameterBuildersImpl}
import net.codingwell.scalaguice.ScalaModule

class ControllersModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[ParameterBuilders].to[ParameterBuildersImpl].asEagerSingleton()
  }
}

