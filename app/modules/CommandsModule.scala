package modules

import checkin.{CheckinCommand, CheckinCommandImpl}
import checkout.{CheckoutCommand, CheckoutCommandImpl}
import com.google.inject.AbstractModule
import initialise.{InitialiseCommand, InitialiseCommandImpl}
import net.codingwell.scalaguice.ScalaModule
import own.{OwnCommand, OwnCommandImpl}

/**
  * Dependency injection for commands.
  */
class CommandsModule extends AbstractModule with ScalaModule {

  /**
    * @inheritdoc
    */
  override def configure(): Unit = {
    bind[CheckinCommand].to[CheckinCommandImpl].asEagerSingleton()
    bind[CheckoutCommand].to[CheckoutCommandImpl].asEagerSingleton()
    bind[OwnCommand].to[OwnCommandImpl].asEagerSingleton()
    bind[InitialiseCommand].to[InitialiseCommandImpl].asEagerSingleton()
  }
}

