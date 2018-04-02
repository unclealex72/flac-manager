/*
 * Copyright 2018 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package modules

import com.google.inject.{AbstractModule, Provides}
import common.message.{I18nMessageServiceBuilder, MessageServiceBuilder}
import net.codingwell.scalaguice.ScalaModule
import play.api.i18n.{Langs, MessagesApi}

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
  def provideMessageServiceBuilder(messagesApi: MessagesApi, langs: Langs): MessageServiceBuilder = {
    I18nMessageServiceBuilder(messagesApi, langs)
  }
}

