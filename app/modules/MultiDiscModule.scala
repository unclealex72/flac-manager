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

import com.google.inject.AbstractModule
import logging.ApplicationLogging
import multidisc.{MultiDiscCommand, MultiDiscCommandImpl, MultiDiscService, MultiDiscServiceImpl}
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
  * Dependency injection for the checkin command.
  */
class MultiDiscModule extends AbstractModule with ScalaModule with AkkaGuiceSupport with ApplicationLogging {

  /**
    * @inheritdoc
    */
  override def configure(): Unit = {
    bind[MultiDiscService].to[MultiDiscServiceImpl].asEagerSingleton()
    bind[MultiDiscCommand].to[MultiDiscCommandImpl].asEagerSingleton()
  }
}

