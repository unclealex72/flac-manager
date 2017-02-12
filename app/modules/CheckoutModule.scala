package modules

import checkout.{CheckoutService, CheckoutServiceImpl}
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

class CheckoutModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[CheckoutService].to[CheckoutServiceImpl].asEagerSingleton()
  }
}

