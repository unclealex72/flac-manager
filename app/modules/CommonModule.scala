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

