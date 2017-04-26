package modules

import checkout.{CheckoutService, CheckoutServiceImpl}
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

/**
  * Dependency injection for the checkout command.
  */
class CheckoutModule extends AbstractModule with ScalaModule {

  /**
    * @inheritdoc
    */
  override def configure(): Unit = {
    bind[CheckoutService].to[CheckoutServiceImpl].asEagerSingleton()
  }
}

