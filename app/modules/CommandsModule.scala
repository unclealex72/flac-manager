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

