package modules

import com.google.inject.{AbstractModule, Provides}
import common.message.{I18nMessageServiceBuilder, MessageServiceBuilder}
import net.codingwell.scalaguice.ScalaModule
import play.api.i18n.MessagesApi

class MessagesModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {}

  @Provides
  def provideMessageServiceBuilder(messagesApi: MessagesApi): MessageServiceBuilder = {
    I18nMessageServiceBuilder(messagesApi)
  }
}

