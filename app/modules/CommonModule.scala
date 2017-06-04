package modules

import java.time.Clock

import com.google.inject.AbstractModule
import common.changes.{ChangeDao, SlickChangeDao}
import common.collections.{CollectionDao, SlickCollectionDao}
import common.multi.{AllowMultiService, PlayConfigurationAllowMultiService}
import common.owners.{OwnerService, OwnerServiceImpl}
import net.codingwell.scalaguice.ScalaModule

/**
  * Dependency injection for common services.
  */
class CommonModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[ChangeDao].to[SlickChangeDao].asEagerSingleton()
    bind[CollectionDao].to[SlickCollectionDao].asEagerSingleton()
    bind[OwnerService].to[OwnerServiceImpl].asEagerSingleton()
    bind[Clock].toInstance(Clock.systemDefaultZone())
    bind[AllowMultiService].to[PlayConfigurationAllowMultiService].asEagerSingleton()
  }
}

