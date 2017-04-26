package modules

import com.google.inject.{AbstractModule, Provides}
import common.message.{I18nMessageServiceBuilder, MessageServiceBuilder}
import net.codingwell.scalaguice.ScalaModule
import play.api.i18n.MessagesApi

/**
  * Dependency injection for messaging.
  */
class MessagesModule extends AbstractModule with ScalaModule {

  /**
    * @inheritdoc
    */
  override def configure(): Unit = {}

  /**
    * Provide a [[MessageServiceBuilder]]
    * @param messagesApi Play's [[MessagesApi]]
    * @return A new [[MessageServiceBuilder]].
    */
  @Provides
  def provideMessageServiceBuilder(messagesApi: MessagesApi): MessageServiceBuilder = {
    I18nMessageServiceBuilder(messagesApi)
  }
}

