/*
 * Copyright 2017 Alex Jones
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

import akka.actor.ActorSystem
import checkin.Throttler
import com.google.inject.{AbstractModule, Provides}
import common.async.{BackgroundExecutionContext, CommandExecutionContext, AkkaExecutionContext, PlayAwareThreadPoolThrottler}
import net.codingwell.scalaguice.ScalaModule
import play.api.inject.ApplicationLifecycle

/**
  * Dependency injection for thread pools.
  */
class AsyncModule extends AbstractModule with ScalaModule {

  /**
    * @inheritdoc
    */
  override def configure(): Unit = {
    bind[Throttler].to[PlayAwareThreadPoolThrottler].asEagerSingleton()
  }

  @Provides
  def provideBackgroundExecutionContext(actorSystem: ActorSystem): BackgroundExecutionContext = {
    new AkkaExecutionContext("background", actorSystem) with BackgroundExecutionContext
  }

  @Provides
  def provideCommandExecutionContext(actorSystem: ActorSystem): CommandExecutionContext = {
    new AkkaExecutionContext("command", actorSystem) with CommandExecutionContext
  }
}

