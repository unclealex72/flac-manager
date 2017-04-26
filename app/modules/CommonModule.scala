package modules

import com.google.inject.AbstractModule
import common.changes.{ChangeDao, SquerylChangeDao}
import common.collections.{CollectionDao, SquerylCollectionDao}
import common.commands.{CommandService, TempFileCommandService}
import common.now.{NowService, NowServiceImpl}
import common.owners.{OwnerService, OwnerServiceImpl}
import net.codingwell.scalaguice.ScalaModule

/**
  * Dependency injection for common services.
  */
class CommonModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[ChangeDao].to[SquerylChangeDao].asEagerSingleton()
    bind[CollectionDao].to[SquerylCollectionDao].asEagerSingleton()
    bind[CommandService].to[TempFileCommandService].asEagerSingleton()
    bind[OwnerService].to[OwnerServiceImpl].asEagerSingleton()
    bind[NowService].to[NowServiceImpl].asEagerSingleton()
  }
}

